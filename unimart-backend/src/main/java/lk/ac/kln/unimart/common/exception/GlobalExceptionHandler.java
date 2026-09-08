package lk.ac.kln.unimart.common.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import org.springframework.security.authentication.BadCredentialsException;

import jakarta.persistence.EntityNotFoundException;
import lk.ac.kln.unimart.common.api.ApiError;
import lk.ac.kln.unimart.common.exception.ConflictException;
import lk.ac.kln.unimart.common.exception.ForbiddenException;
import lk.ac.kln.unimart.common.exception.ResourceNotFoundException;

/**
 * One global handler so every endpoint returns the same predictable error
 * shape. See Guide 03, section 6 ("Use one global exception handler with a
 * predictable Problem Details-style response").
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        ApiError body = new ApiError(
                "VALIDATION_FAILED",
                "One or more fields failed validation.",
                path(request),
                Instant.now(),
                fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, WebRequest request) {
        ApiError body = new ApiError(
                "NOT_FOUND",
                ex.getMessage(),
                path(request),
                Instant.now(),
                null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        ApiError body = new ApiError(
                "INVALID_CREDENTIALS",
                ex.getMessage(),
                path(request),
                Instant.now(),
                null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex, WebRequest request) {
        ApiError body = new ApiError(
                "FORBIDDEN",
                ex.getMessage(),
                path(request),
                Instant.now(),
                null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler({IllegalStateException.class, ConflictException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, WebRequest request) {
        ApiError body = new ApiError(
                "STATE_CONFLICT",
                ex.getMessage(),
                path(request),
                Instant.now(),
                null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                             WebRequest request) {
        ApiError body = new ApiError(
                "METHOD_NOT_ALLOWED",
                ex.getMessage(),
                path(request),
                Instant.now(),
                null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, WebRequest request) {
        ApiError body = new ApiError(
                "INTERNAL_ERROR",
                "An unexpected error occurred.",
                path(request),
                Instant.now(),
                null);
        return ResponseEntity.internalServerError().body(body);
    }

    private String path(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
