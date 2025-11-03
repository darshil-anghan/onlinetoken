package com.example.mytoken.handler;

import com.example.mytoken.exception.InvalidRequestException;
import com.example.mytoken.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler({InvalidRequestException.class})
    public ErrorResponse handleCustomException(InvalidRequestException e) {
        log.error("Handling InvalidRequestException: {}", e.getMessage());
        List<FieldError> errorList = e.getErrors() != null ? e.getErrors().getFieldErrors() : Collections.emptyList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), errorList);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "An unexpected error occurred.");
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
