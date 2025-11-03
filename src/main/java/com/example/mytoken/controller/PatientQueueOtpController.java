package com.example.mytoken.controller;

import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.PatientQueueOtpPayload;
import com.example.mytoken.service.PatientQueueOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient_queue_otp")
@RequiredArgsConstructor
public class PatientQueueOtpController extends BaseController {

    private final PatientQueueOtpService otpService;

    @PostMapping("/verify")
    public ResponseEntity<GlobalResponse> verifyOtp(
            @RequestBody PatientQueueOtpPayload patientQueueOtpPayload
    ) {
        try {
            boolean isValid = otpService.verifyOtp(patientQueueOtpPayload);
            return ok(isValid, "Otp Verification done.");
        } catch (Exception e) {
            return ok("", e.getMessage());
        }
    }
}
