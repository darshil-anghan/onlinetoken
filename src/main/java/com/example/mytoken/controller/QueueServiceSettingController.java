package com.example.mytoken.controller;

import com.example.mytoken.model.QueueServiceSetting;
import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.QueueServiceSettingPayload;
import com.example.mytoken.service.QueueServiceSettingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setting/service")
public class QueueServiceSettingController extends BaseController {

    @Autowired
    private QueueServiceSettingService service;

    /**
     *  This function use for update queue setting -> service
     */
    @PutMapping("/{queueId}")
    public ResponseEntity<GlobalResponse> saveSettings(
            @PathVariable Long queueId,
            @RequestBody @Valid QueueServiceSettingPayload payload
    ) {
        QueueServiceSetting setting = service.saveSettings(queueId, payload);
        return ok(setting, "Queue service settings saved successfully.");
    }

    /**
     *  This function use for get queue setting -> service details
     */
    @GetMapping("/{queueId}")
    public ResponseEntity<GlobalResponse> getSettings(@PathVariable Long queueId) {
        QueueServiceSetting setting = service.getSettings(queueId);
        if (setting == null) {
            return ok(null, "Settings not found.");
        }
        return ok(setting, "Queue service settings fetched successfully.");
    }
}
