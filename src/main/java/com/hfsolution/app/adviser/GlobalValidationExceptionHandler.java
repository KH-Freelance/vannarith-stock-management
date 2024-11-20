package com.hfsolution.app.adviser;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.hfsolution.app.dto.ExceptionResponse;

import java.util.stream.Collectors;

/**
 * Global exception handler for handling MethodArgumentNotValidException.
 */
@ControllerAdvice
@Order(1)
public class GlobalValidationExceptionHandler {

    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    private static final String DEV_MESSAGE = "Validation failed for request";

    /**
     * Handle MethodArgumentNotValidException and return a structured response.
     *
     * @param ex the MethodArgumentNotValidException
     * @return ResponseEntity containing the exception response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ExceptionResponse exceptionResponse = createExceptionResponse(ex);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    private ExceptionResponse createExceptionResponse(MethodArgumentNotValidException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCode(VALIDATION_ERROR_CODE);
        exceptionResponse.setDevMsg(DEV_MESSAGE);
        exceptionResponse.setMsg(ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", ")));
        return exceptionResponse;
    }
}