package com.example.mytoken.controller;

import com.example.mytoken.model.response.GlobalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class BaseController {

    /**
     * OK
     *
     * @param entity - generic response object
     * @param message - To display appropriate message.
     * @return ResponseEntity
     */
    protected ResponseEntity<GlobalResponse> ok(Object entity, String message) {
        GlobalResponse resp = new GlobalResponse(entity, Collections.emptyList(), message);
        return ResponseEntity.ok(resp);
    }
}
