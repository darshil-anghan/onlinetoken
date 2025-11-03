package com.example.mytoken.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private List<FieldError> errorsList;
    private boolean isSuccess;

    public ErrorResponse(HttpStatus code, String message, List<FieldError> errorsList) {
        this.code = String.valueOf(code);
        this.message = message;
        this.errorsList = errorsList;
    }

    public ErrorResponse(String message, HttpStatus badRequest, boolean isSuccess) {
        this.message = message;
        this.code = String.valueOf(badRequest);
        this.isSuccess = isSuccess;
    }
}
