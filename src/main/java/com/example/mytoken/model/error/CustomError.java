package com.example.mytoken.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomError {

    private String code;
    private String message;
    private String detail;
}
