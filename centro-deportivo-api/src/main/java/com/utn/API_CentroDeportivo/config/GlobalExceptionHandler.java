package com.utn.API_CentroDeportivo.config;

import com.utn.API_CentroDeportivo.model.dto.response.ErrorResponseDTO;
import com.utn.API_CentroDeportivo.model.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UsernameNotFoundException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.user.not.found.detail", null, locale));
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("error.data.not.found", null, locale),
                details,
                "USER_NOT_FOUND");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, Locale locale) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.validation", null, locale),
                errors,
                "VALIDATION_FAILED");
    }

    @ExceptionHandler(FieldAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleFieldAlreadyExistsException(FieldAlreadyExistsException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("field", ex.getField());
        details.put("message", messageSource.getMessage("error.field.already.exists.detail", null, locale));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.validation", null, locale),
                details,
                "FIELD_ALREADY_EXISTS");
    }

    @ExceptionHandler(MemberAlreadyEnrolledException.class)
    public ResponseEntity<ErrorResponseDTO> handleMemberAlreadyEnrolledException(MemberAlreadyEnrolledException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.member.already.enrolled.detail", null, locale));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.validation", null, locale),
                details,
                "MEMBER_ALREADY_ENROLLED");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleMemberNotFoundException(MemberNotFoundException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.member.not.found.detail", null, locale));
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("error.data.not.found", null, locale),
                details,
                "MEMBER_NOT_FOUND");
    }

    @ExceptionHandler(SportActivityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleSportActivityNotFoundException(SportActivityNotFoundException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.sport.activity.not.found.detail", null, locale));
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("error.data.not.found", null, locale),
                details,
                "SPORT_ACTIVITY_NOT_FOUND");
    }

    @ExceptionHandler(InvalidTimeFormatException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTimeFormatException(InvalidTimeFormatException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.invalid.time.format.detail", null, locale));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.validation", null, locale),
                details,
                "INVALID_TIME_FORMAT");
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRoleException(InvalidRoleException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.invalid.role.detail", null, locale));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.validation", null, locale),
                details,
                "INVALID_ROLE");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex, Locale locale) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            String field = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            errors.put(field, message);
        });
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.validation", null, locale),
                errors,
                "VALIDATION_FAILED");
    }
    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEnrollmentNotFoundException(EnrollmentNotFoundException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.enrollment.not.found.detail", null, locale));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.not.found", null, locale),
                details,
                "ENROLLMENT_NOT_FOUND");
    }
    @ExceptionHandler(MaxCapacityException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxCapacity(MaxCapacityException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.max.capacity.detail", null, locale));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.max.capacity", null, locale),
                details,
                "MAX_CAPACITY");
    }


    @ExceptionHandler(InvalidFilterCombinationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidFilterCombination(InvalidFilterCombinationException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("error", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("error.data.validation", null, locale),
                details,
                "INVALID_FILTER_COMBINATION");
    }

    @ExceptionHandler(RoutineNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRoutineNotFoundException(RoutineNotFoundException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.routine.not.found.detail", null, locale));
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("error.data.not.found", null, locale),
                details,
                "ROUTINE_NOT_FOUND");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundExceptionCustom(UserNotFoundException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.user.not.found.detail", null, locale));
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("error.data.not.found", null, locale),
                details,
                "USER_NOT_FOUND");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.unauthorized.detail", null, locale));
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                "Acceso denegado",
                details,
                "UNAUTHORIZED");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex, Locale locale) {
        Map<String, String> details = new HashMap<>();
        details.put("message", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                "Acceso denegado",
                details,
                "ACCESS_DENIED");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, Locale locale) {
        log.error("Unhandled exception caught by generic handler", ex);
        Map<String, String> details = new HashMap<>();
        details.put("message", messageSource.getMessage("error.generic.detail", null, locale));
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("error.data.unexpected", null, locale),
                details,
                "GENERIC_ERROR");
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String message, Map<String, String> details, String code) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .details(details != null ? details : new HashMap<>())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

}