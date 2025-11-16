package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.request.ChatMessageRequest;
import com.utn.API_CentroDeportivo.model.dto.response.*;
import com.utn.API_CentroDeportivo.service.IEnrollmentService;
import com.utn.API_CentroDeportivo.service.IMemberService;
import com.utn.API_CentroDeportivo.service.ISportActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final GeminiService geminiService;
    private final ISportActivityService sportActivityService;
    private final IMemberService memberService;
    private final IEnrollmentService enrollmentService;

    public ChatMessageResponse processMessage(ChatMessageRequest request) {
        try {
            String context = buildSystemContext(request.getUserId());

            String fullPrompt = buildPrompt(context, request.getMessage());

            String response = geminiService.generateResponse(fullPrompt);

            return ChatMessageResponse.success(response);
        } catch (Exception e) {
            return ChatMessageResponse.error("Lo siento, ocurrió un error al procesar tu mensaje: " + e.getMessage());
        }
    }

    private String buildSystemContext(String username) {
        StringBuilder context = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        Locale spanish = Locale.of("es", "ES");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy, HH:mm", spanish);
        String currentDateTime = now.format(dateFormatter);

        context.append("=== FECHA Y HORA ACTUAL ===\n");
        context.append(String.format("Hoy es: %s\n", currentDateTime));
        context.append(String.format("Día de la semana: %s\n\n",
                now.format(DateTimeFormatter.ofPattern("EEEE", spanish))));

        context.append("Eres ChatBum un asistente informativo del Centro Deportivo. Tu rol es SOLO proporcionar información sobre las actividades disponibles.\n\n");

        context.append("REGLAS IMPORTANTES:\n");
        context.append("1. NO eres médico ni fisioterapeuta. NO diagnosticas ni recetas tratamientos.\n");
        context.append("2. Si mencionan problemas de salud, lesiones o dolores:\n");
        context.append("   - Recomienda consultar con un profesional de la salud PRIMERO\n");
        context.append("   - Luego sugiere actividades de bajo impacto disponibles (yoga, pilates, natación)\n");
        context.append("   - Menciona que los instructores pueden adaptar ejercicios\n");
        context.append("3. Tu enfoque: Informar sobre las clases, horarios, instructores y disponibilidad.\n");
        context.append("4. Mantén un tono amigable pero profesional.\n");
        context.append("5. Usa la fecha actual para responder preguntas sobre 'hoy', 'mañana', 'esta semana', etc.\n\n");

        context.append("=== ACTIVIDADES DISPONIBLES ===\n");
        Page<SportActivitySummaryDTO> activities = sportActivityService.getActivities(PageRequest.of(0, 50));

        for (SportActivitySummaryDTO activitySummary : activities.getContent()) {
            Optional<SportActivityDetailsDTO> detailsOpt = sportActivityService.getActivityById(activitySummary.getId());
            if (detailsOpt.isPresent()) {
                SportActivityDetailsDTO activity = detailsOpt.get();
                context.append(String.format("- %s\n", activity.getName()));
                context.append(String.format("  Descripción: %s\n", activity.getDescription()));
                context.append(String.format("  Instructor: %s\n", activity.getInstructorName()));
                context.append(String.format("  Horario: %s a %s\n",
                        activity.getStartTime(), activity.getEndTime()));
                context.append(String.format("  Días: %s\n", activity.getClassDays()));
                context.append(String.format("  Capacidad: %d/%d personas\n",
                        activity.getCurrentMembers(), activity.getMaxMembers()));
                context.append(String.format("  Estado: %s\n\n",
                        activity.getCurrentMembers() >= activity.getMaxMembers() ? "COMPLETO" : "DISPONIBLE"));
            }
        }

        if (username != null && !username.isEmpty()) {
            context.append("=== INFORMACIÓN DEL USUARIO ===\n");
            try {
                MembersDetailsDTO memberDetails = memberService.getMemberDetailsByUsername(username);
                context.append(String.format("Usuario: %s %s\n",
                        memberDetails.getName(), memberDetails.getLastname()));
                context.append(String.format("Email: %s\n", memberDetails.getEmail()));
                context.append(String.format("Teléfono: %s\n", memberDetails.getPhone()));
                context.append(String.format("Estado: %s\n\n", memberDetails.getStatus()));

                List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByUsername(username);
                if (!enrollments.isEmpty()) {
                    context.append("Actividades en las que está inscrito:\n");
                    for (EnrollmentDTO enrollment : enrollments) {
                        context.append(String.format("- %s (desde %s hasta %s)\n",
                                enrollment.getActivityName(),
                                enrollment.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                enrollment.getEndDate() != null ? enrollment.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "actualidad"));
                    }
                    context.append("\n");
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener información del usuario: {}", username);
            }
        }

        context.append("\n=== INSTRUCCIONES ===\n");
        context.append("- Responde basándote ÚNICAMENTE en la información proporcionada arriba.\n");
        context.append("- Si no tienes la información, indícalo claramente.\n");
        context.append("- Sé conciso pero informativo.\n");
        context.append("- Si el usuario pregunta por sus inscripciones, usa la información de 'Actividades en las que está inscrito'.\n");
        context.append("- Si preguntan sobre disponibilidad, verifica la capacidad actual vs máxima.\n");
        context.append("- IMPORTANTE: Responde SIEMPRE en texto plano, sin usar formato Markdown.\n" +
                "No uses asteriscos (**), guiones (-) ni otros caracteres especiales para dar formato.");
        context.append("- Mantén un tono amigable y profesional.\n\n");

        return context.toString();
    }

    private String buildPrompt(String context, String userMessage) {
        return context + "PREGUNTA DEL USUARIO: " + userMessage + "\n\nRESPUESTA:";
    }
}
