package com.utn.API_CentroDeportivo.controller;

import com.utn.API_CentroDeportivo.model.dto.request.MemberRequestDTO;

import com.utn.API_CentroDeportivo.model.dto.response.*;
import com.utn.API_CentroDeportivo.model.entity.Instructor;
import com.utn.API_CentroDeportivo.service.IAuthService;
import com.utn.API_CentroDeportivo.service.IInstructorService;
import com.utn.API_CentroDeportivo.service.IMemberService;
import com.utn.API_CentroDeportivo.service.ISportActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/instructors")
@Tag(name = "Gestión de Instructores", description = "Endpoints para la consulta y gestión de instructores y sus actividades.")
@SecurityRequirement(name = "Bearer Authentication")
public class InstructorController {

    @Autowired
    private IInstructorService instructorService;

    @Autowired
    private ISportActivityService sportActivityService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IAuthService authService;

    @Operation(
            summary = "Obtener resumen de instructor por ID",
            description = "Permite obtener un resumen de la información de un instructor específico por su ID. Accesible para cualquier usuario autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resumen del instructor recuperado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InstructorSummaryDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Instructor no encontrado con el ID proporcionado"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<InstructorSummaryDTO> getInstructor(@PathVariable Long id) {
        return instructorService.getInstructorSummaryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Obtener mis actividades (para instructor autenticado)",
            description = "Recupera una lista detallada de todas las actividades deportivas asignadas al instructor actualmente autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de actividades del instructor recuperada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = SportActivityDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (requiere rol de INSTRUCTOR)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Instructor no encontrado para el usuario autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/my-activities")
    public ResponseEntity<List<SportActivityDetailsDTO>> getMyActivities() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return instructorService.findByUsername(username)
                .map(instructor -> {
                    List<SportActivityDetailsDTO> activities = sportActivityService.getActivitiesDetailsByInstructor(instructor);
                    return ResponseEntity.ok(activities);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Obtener detalles de una actividad específica (para mi instructor)",
            description = "Recupera los detalles de una actividad deportiva específica, solo si está asignada al instructor autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Detalles de la actividad recuperados exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SportActivityDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (requiere rol de INSTRUCTOR)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Actividad no encontrada o no asignada a este instructor"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/my-activities/{activityId}")
    public ResponseEntity<SportActivityDetailsDTO> getMyActivityDetails(@PathVariable Long activityId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Instructor> optionalInstructor = instructorService.findByUsername(username);
        Optional<SportActivityDetailsDTO> optionalActivity = sportActivityService.getActivityById(activityId);

        if (optionalInstructor.isPresent() && optionalActivity.isPresent()) {
            Instructor instructor = optionalInstructor.get();
            SportActivityDetailsDTO activity = optionalActivity.get();

            if (activity.getInstructorName().equals(instructor.getName())) { // o getFullName()
                return ResponseEntity.ok(activity);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Obtener todos los miembros paginados (para instructor)",
            description = "Recupera una lista paginada de todos los miembros registrados en el sistema. Accesible solo para instructores.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de miembros recuperada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class, subTypes = {MembersDetailsDTO.class})
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (requiere rol de INSTRUCTOR)"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/members")
    public ResponseEntity<Page<MembersDetailsDTO>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(memberService.getAllMembers(page, size));
    }

    @Operation(
            summary = "Obtener detalles de un miembro por ID (para instructor)",
            description = "Recupera los detalles completos de un miembro específico utilizando su ID. Accesible solo para instructores.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Detalles del miembro recuperados exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MembersDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (requiere rol de INSTRUCTOR)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Miembro no encontrado con el ID proporcionado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<MembersDetailsDTO> getMemberDetails(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberDetailsById(memberId));
    }

    @Operation(
            summary = "Registrar un nuevo miembro (por instructor)",
            description = "Permite a un instructor autenticado registrar un nuevo miembro en el sistema. Se crea una nueva cuenta de miembro.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del miembro a registrar (nombre, apellido, email, contraseña, etc.)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Socio registrado correctamente por el instructor",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Socio registrado correctamente por el instructor"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida o datos de registro incompletos/incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (requiere rol de INSTRUCTOR)"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto: el email o nombre de usuario ya existen"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/register-member")
    public ResponseEntity<String> registerMemberByInstructor(
            @Valid @RequestBody MemberRequestDTO memberDTO) {

        authService.registerMember(memberDTO);
        return ResponseEntity.ok("Socio registrado correctamente por el instructor");
    }

    @Operation(
            summary = "Obtener detalle completo de instructor por ID",
            description = "Permite obtener todos los datos personales del usuario, su especialidad y la lista completa de actividades asignadas a un instructor específico por su ID. Accesible para cualquier usuario autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Detalle completo del instructor recuperado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InstructorDetailsDTO.class) // ⬅️ DTO COMPLETO
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Instructor no encontrado con el ID proporcionado"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @GetMapping("/{id}/details")
    public ResponseEntity<InstructorDetailsDTO> getInstructorDetails(@PathVariable Long id) {
        return instructorService.getInstructorDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/profile")
    public ResponseEntity<Optional<UserDetailsDTO>> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserDetailsDTO> dto = instructorService.findUserDetailsByUsername(username);
        return ResponseEntity.ok(dto);
    }
}
