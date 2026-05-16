package com.tractorstore.shared.config;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler — RFC 9457 error responses")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("EntityNotFoundException → 404 Not Found")
    void entityNotFound_returns404() {
        ProblemDetail problem = handler.handleNotFound(new EntityNotFoundException("Product not found"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problem.getDetail()).isEqualTo("Product not found");
        assertThat(problem.getType().toString()).isEqualTo("/errors/not-found");
    }

    @Test
    @DisplayName("NoSuchElementException → 404 Not Found")
    void noSuchElement_returns404() {
        ProblemDetail problem = handler.handleNotFound(new NoSuchElementException("Cart not found"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException → 400 with field errors")
    void validationFailed_returns400WithFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "customerEmail", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
            new org.springframework.core.MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("validationFailed_returns400WithFieldErrors"),
                -1
            ),
            bindingResult
        );

        ProblemDetail problem = handler.handleValidation(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Validation Failed");
        assertThat(problem.getType().toString()).isEqualTo("/errors/validation");
        assertThat(problem.getProperties()).containsKey("errors");
    }

    @Test
    @DisplayName("IllegalArgumentException → 400 Bad Request")
    void illegalArgument_returns400() {
        ProblemDetail problem = handler.handleIllegalArgument(new IllegalArgumentException("Invalid UUID format"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getType().toString()).isEqualTo("/errors/bad-request");
    }

    @Test
    @DisplayName("IllegalStateException → 422 Unprocessable Entity")
    void illegalState_returns422() {
        ProblemDetail problem = handler.handleIllegalState(new IllegalStateException("Order already confirmed"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(problem.getTitle()).isEqualTo("Unprocessable Entity");
        assertThat(problem.getType().toString()).isEqualTo("/errors/unprocessable");
    }

    @Test
    @DisplayName("DataIntegrityViolationException → 409 Conflict")
    void dataIntegrity_returns409() {
        ProblemDetail problem = handler.handleDataIntegrity(new DataIntegrityViolationException("Duplicate key"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Conflict");
        assertThat(problem.getType().toString()).isEqualTo("/errors/conflict");
    }

    @Test
    @DisplayName("Unexpected Exception → 500 Internal Server Error")
    void genericException_returns500() {
        ProblemDetail problem = handler.handleGenericException(new RuntimeException("Unexpected"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getDetail()).isEqualTo("An unexpected error occurred");
        assertThat(problem.getType().toString()).isEqualTo("/errors/internal");
    }
}
