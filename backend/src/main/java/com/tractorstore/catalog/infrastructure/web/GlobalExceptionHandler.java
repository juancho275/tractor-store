package com.tractorstore.catalog.infrastructure.web;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.URI;

/**
 * Global exception handler following RFC 9457 (Problem Details for HTTP APIs).
 *
 * <p>Converts exceptions to structured ProblemDetail responses
 * instead of raw stack traces. This ensures consistent error
 * format across all API endpoints.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9457">RFC 9457</a>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles entity not found exceptions (404).
     * Thrown by services when a requested resource doesn't exist.
     *
     * @param ex the entity not found exception
     * @return RFC 9457 ProblemDetail with 404 status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("/errors/not-found"));
        return problem;
    }

    /**
     * Handles unexpected exceptions (500).
     * Catches any unhandled exception to prevent stack trace exposure.
     *
     * @param ex the unexpected exception
     * @return RFC 9457 ProblemDetail with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("/errors/internal"));
        return problem;
    }
}