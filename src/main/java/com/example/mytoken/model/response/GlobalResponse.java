package com.example.mytoken.model.response;

import com.example.mytoken.model.error.CustomError;
import com.example.mytoken.util.GlobalConstant;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"status", "message", "error", "data"})
public class GlobalResponse {

    private Object data;
    private String status;
    private Collection<CustomError> error = Collections.emptyList();
    private String message;

    public GlobalResponse(Object data, Collection<CustomError> error, String message) {
        this.data = data;
        this.error = error;
        this.message = message;
        this.status = determineStatus(data, error, message);
    }

    private String determineStatus(Object data, Collection<CustomError> error, String message) {
        if (error != null && !error.isEmpty()) {
            return "Error";
        } else if (data != null) {
            return "Success";
        } else if (GlobalConstant.DELETE_SUCCESS_MSG.equals(message)) {
            return "Success";
        } else {
            return "Error";
        }
    }
}