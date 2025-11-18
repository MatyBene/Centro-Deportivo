package com.utn.API_CentroDeportivo.controller;

import com.utn.API_CentroDeportivo.model.dto.request.MemberEditDTO;
import com.utn.API_CentroDeportivo.model.dto.response.EnrollmentDTO;
import com.utn.API_CentroDeportivo.model.dto.response.MembersDetailsDTO;
import com.utn.API_CentroDeportivo.service.IEnrollmentService;
import com.utn.API_CentroDeportivo.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "Gestión de Miembros", description = "Endpoints para la gestión de perfiles y actividades de los miembros (requiere autenticación de miembro).")
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IEnrollmentService enrollmentService;

    @Operation(
            summary = "Inscribir al miembro en una actividad",
            description = "Permite al miembro autenticado inscribirse en una actividad deportiva específica.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "ID de la actividad a la que se desea inscribir",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "integer", format = "int64", example = "1"))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "El socio se inscribió correctamente en la actividad",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "El socio se inscribió correctamente en la actividad"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (ej. actividad no existe, ya inscrito, etc.)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de MEMBER)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Actividad no encontrada"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto (ej. el miembro ya está inscrito en la actividad)"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/enroll/{activityId}")
    public ResponseEntity<String> enrollMemberInActivity(@PathVariable Long activityId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        enrollmentService.enrollMemberToActivity(username, activityId);

        return ResponseEntity.ok("El socio se inscribió correctamente en la actividad");
    }

    @Operation(
            summary = "Eliminar cuenta de miembro",
            description = "Permite al miembro autenticado eliminar su propia cuenta del sistema. Esta acción es irreversible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cuenta eliminada correctamente",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Cuenta eliminada correctamente"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de MEMBER)"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('MEMBER')")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteOwnAccount(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        memberService.deleteMemberByUsername(username);

        return ResponseEntity.ok("Cuenta eliminada correctamente");
    }

    @Operation(
            summary = "Actualizar perfil de miembro",
            description = "Permite al miembro autenticado actualizar su información de perfil.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del perfil a actualizar",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberEditDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Se modificó el usuario correctamente",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Se modifico el usuario correctamente"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida o datos de perfil incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de MEMBER)"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody MemberEditDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        memberService.updateMemberProfile(username, dto);
        return ResponseEntity.ok("Se modifico el usuario correctamente");
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/profile")
    public ResponseEntity<MembersDetailsDTO> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        MembersDetailsDTO dto = memberService.getMemberDetailsByUsername(username);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Darse de baja de una actividad",
            description = "Permite al miembro autenticado darse de baja de una actividad en la que está inscrito.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Te diste de baja de la actividad con éxito",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Te diste de baja de la actividad con éxito"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de MEMBER)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Actividad o inscripción no encontrada"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('MEMBER')")
    @DeleteMapping("/activities/{activityId}")
    public ResponseEntity<String> unsubscribeFromActivity(@PathVariable Long activityId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        enrollmentService.unsubscribeMemberFromActivity(username, activityId);
        return ResponseEntity.ok("Te diste de baja de la actividad con éxito");
    }

    @Operation(
            summary = "Obtener actividades inscritas por el miembro",
            description = "Recupera una lista de todas las actividades en las que el miembro autenticado está actualmente inscrito.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de inscripciones recuperada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = EnrollmentDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de MEMBER)"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/activities")
    public ResponseEntity<List<EnrollmentDTO>> getMyActivities() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByUsername(username);
        return ResponseEntity.ok(enrollments);
    }
}
