package com.example.skye.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleResponseStatusException() {
        ResponseEntity<Map<String, String>> response = handler.handleResponseStatusException(
                new ResponseStatusException(HttpStatus.CONFLICT, "duplicate"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("duplicate", response.getBody().get("error"));
    }

    @Test
    void handleValidationExceptions() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "username", "required"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("required", response.getBody().get("username"));
    }

    @Test
    void handleUnreadableMessage() {
        ResponseEntity<Map<String, String>> response = handler.handleUnreadableMessage(
                new HttpMessageNotReadableException("bad json"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Malformed"));
    }

    @Test
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        ResponseEntity<Map<String, String>> response = handler.handleTypeMismatch(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
