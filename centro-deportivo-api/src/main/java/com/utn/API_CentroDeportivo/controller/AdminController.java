package com.utn.API_CentroDeportivo.controller;

import com.utn.API_CentroDeportivo.model.dto.request.EnrollmentRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.request.UserRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.response.AdminViewDTO;
import com.utn.API_CentroDeportivo.model.dto.response.UserDetailsDTO;
import com.utn.API_CentroDeportivo.model.enums.PermissionLevel;
import com.utn.API_CentroDeportivo.model.enums.Role;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.service.IAdminService;
import com.utn.API_CentroDeportivo.service.IEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Optional;


@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Administración de Usuarios", description = "Endpoints para la gestión de usuarios (requiere rol de ADMINISTRADOR).")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private IEnrollmentService enrollmentService;


    @Operation(
            summary = "Crear un nuevo miembro",
            description = "Permite al administrador crear una nueva cuenta de miembro en el sistema con los datos proporcionados.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON con los datos del nuevo miembro (ej. nombre, apellido, email, contraseña).",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Socio creado correctamente",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "El socio se creo correctamente"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (datos incompletos o incorrectos)."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario autenticado no tiene el rol de ADMIN)."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto: el email o nombre de usuario ya existen."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-member")
    public ResponseEntity<String> createMember(@RequestBody UserRequestDTO userDTO) {
        adminService.createUser(userDTO);
        return ResponseEntity.ok("Socio creado correctamente");
    }

    @Operation(
            summary = "Crear un nuevo instructor",
            description = "Permite al administrador crear una nueva cuenta de instructor en el sistema. Requiere los permisos 'PERMISSION_USER_MANAGER' o 'PERMISSION_SUPER_ADMIN'.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON con los datos del nuevo instructor (ej. nombre, apellido, email, contraseña).",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Instructor creado correctamente",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Instructor creado correctamente"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (datos incompletos o incorrectos)."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario autenticado no tiene el rol de ADMIN o los permisos requeridos)."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto: el email o nombre de usuario ya existen."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') and (hasAuthority('PERMISSION_USER_MANAGER') or hasAuthority('PERMISSION_SUPER_ADMIN'))")
    @PostMapping("/create-instructor")
    public ResponseEntity<String> createInstructor(@RequestBody UserRequestDTO userDTO) {
        adminService.createUser(userDTO);
        return ResponseEntity.ok("Instructor creado correctamente");
    }

    @Operation(
            summary = "Crear un nuevo administrador",
            description = "Permite a un administrador con permisos de 'SUPER_ADMIN' crear una nueva cuenta de administrador en el sistema.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON con los datos del nuevo administrador (ej. nombre, apellido, email, contraseña).",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Administrador creado correctamente",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Admin creado correctamente"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (datos incompletos o incorrectos)."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario autenticado no tiene el rol de ADMIN o los permisos de SUPER_ADMIN)."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto: el email o nombre de usuario ya existen."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_SUPER_ADMIN')")
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody UserRequestDTO userDTO) {
        adminService.createUser(userDTO);
        return ResponseEntity.ok("Admin creado correctamente");
    }

    @Operation(
            summary = "Inscribir un miembro en una actividad",
            description = "Permite al administrador inscribir a un miembro existente en una actividad deportiva específica. Requiere los permisos 'PERMISSION_USER_MANAGER' o 'PERMISSION_SUPER_ADMIN'.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON con el nombre de usuario (username) del miembro y el ID de la actividad.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnrollmentRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "El socio se inscribió correctamente en la actividad",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "El socio se inscribió correctamente en la actividad"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (ej. el miembro ya está inscrito, actividad no válida, etc.)."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de ADMIN o los permisos requeridos)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Miembro o actividad no encontrada."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto: el miembro ya está inscrito en la actividad."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') and (hasAuthority('PERMISSION_SUPER_ADMIN') or hasAuthority('PERMISSION_USER_MANAGER'))")
    @PostMapping("/enroll-member")
    public ResponseEntity<String> enrollMemberInActivity(@RequestBody EnrollmentRequestDTO request) {
        enrollmentService.enrollMemberToActivity(request.getUsername(), request.getActivityId());
        return ResponseEntity.ok("El socio se inscribió correctamente en la actividad");
    }

    @Operation(
            summary = "Dar de baja un miembro de una actividad",
            description = "Permite al administrador dar de baja a un miembro específico de una actividad deportiva utilizando el ID de la actividad y el nombre de usuario del miembro. Requiere los permisos 'PERMISSION_USER_MANAGER' o 'PERMISSION_SUPER_ADMIN'.",
            parameters = {
                    @Parameter(
                            name = "activityId",
                            description = "ID de la actividad deportiva.",
                            example = "1",
                            required = true
                    ),
                    @Parameter(
                            name = "username",
                            description = "Nombre de usuario (username) del miembro a dar de baja.",
                            example = "john.doe",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "El socio se dio de baja correctamente de la actividad",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "El socio se dio de baja correctamente de la actividad"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de ADMIN o los permisos requeridos)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Inscripción, actividad o miembro no encontrado."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') and (hasAuthority('PERMISSION_SUPER_ADMIN') or hasAuthority('PERMISSION_USER_MANAGER'))")
    @DeleteMapping("/activity/{activityId}/member/{username}")
    public ResponseEntity<String> unsubscribeMemberFromActivity(@PathVariable Long activityId, @PathVariable String username) {
        enrollmentService.unsubscribeMemberFromActivity(username, activityId);
        return ResponseEntity.ok("El socio se dio de baja correctamente de la actividad");
    }

    @Operation(
            summary = "Obtener todos los usuarios con opciones de filtrado",
            description = "Permite al administrador obtener una lista paginada de todos los usuarios, con la opción de filtrar por rol, estado y nivel de permiso.",
            parameters = {
                    @Parameter(
                            name = "role",
                            description = "Filtrar por rol del usuario (ej. MEMBER, INSTRUCTOR, ADMIN).",
                            example = "MEMBER",
                            schema = @Schema(implementation = Role.class)
                    ),
                    @Parameter(
                            name = "status",
                            description = "Filtrar por estado del usuario (ej. ACTIVE, INACTIVE, SUSPENDED).",
                            example = "ACTIVE",
                            schema = @Schema(implementation = Status.class)
                    ),
                    @Parameter(
                            name = "permission",
                            description = "Filtrar por nivel de permiso (ej. PERMISSION_READ_ONLY, PERMISSION_USER_MANAGER, PERMISSION_SUPER_ADMIN).",
                            example = "PERMISSION_USER_MANAGER",
                            schema = @Schema(implementation = PermissionLevel.class)
                    ),
                    @Parameter(
                            name = "page",
                            description = "Número de página (0-indexed) de los resultados.",
                            example = "0"
                    ),
                    @Parameter(
                            name = "size",
                            description = "Número de elementos por página.",
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de usuarios recuperada exitosamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class, subTypes = {AdminViewDTO.class})
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario autenticado no tiene el rol de ADMIN)."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Page<AdminViewDTO>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) PermissionLevel permission,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<AdminViewDTO> dtoPage = adminService.getUsers(role, status, permission, pageable);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(
            summary = "Obtener detalles de un usuario por nombre de usuario",
            description = "Permite al administrador obtener los detalles completos de un usuario específico utilizando su nombre de usuario (username).",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "Nombre de usuario (username) del usuario a buscar.",
                            example = "john.doe",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Detalles del usuario recuperados exitosamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdminViewDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario autenticado no tiene el rol de ADMIN)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado con el nombre de usuario proporcionado."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{username}")
    public ResponseEntity<UserDetailsDTO> getUserDetailsByUsername(@PathVariable String username) {
        return adminService.findUserDetailsByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Eliminar un usuario por ID",
            description = "Permite al administrador eliminar un usuario del sistema utilizando su ID. Requiere el rol de ADMIN y el permiso 'PERMISSION_USER_MANAGER' o 'PERMISSION_SUPER_ADMIN'.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID del usuario a eliminar.",
                            example = "1",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario eliminado correctamente.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Usuario fue eliminado correctamente."))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de ADMIN o los permisos requeridos)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado con el ID proporcionado."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') and (hasAuthority('PERMISSION_USER_MANAGER') or hasAuthority('PERMISSION_SUPER_ADMIN'))")
    @DeleteMapping("/users/id/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.ok("Usuario fue eliminado correctamente.");
    }

    @Operation(
            summary = "Eliminar un usuario por nombre de usuario",
            description = "Permite al administrador eliminar un usuario del sistema utilizando su nombre de usuario (username). Requiere el rol de ADMIN y el permiso 'PERMISSION_USER_MANAGER' o 'PERMISSION_SUPER_ADMIN'.",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "Nombre de usuario (username) del usuario a eliminar.",
                            example = "jane.doe",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario eliminado correctamente.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Usuario fue eliminado correctamente."))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (el usuario no tiene el rol de ADMIN o los permisos requeridos)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado con el nombre de usuario proporcionado."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') and (hasAuthority('PERMISSION_USER_MANAGER') or hasAuthority('PERMISSION_SUPER_ADMIN'))")
    @DeleteMapping("/users/username/{username}")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
        adminService.deleteUserByUsername(username);
        return ResponseEntity.ok("Usuario fue eliminado correctamente.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<Optional<UserDetailsDTO>> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserDetailsDTO> dto = adminService.findUserDetailsByUsername(username);
        return ResponseEntity.ok(dto);
    }
}
