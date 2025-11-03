package com.example.mytoken.service;

import com.example.mytoken.payload.PatientQueueOtpPayload;

public interface PatientQueueOtpService {
    boolean verifyOtp(PatientQueueOtpPayload patientQueueOtpPayload);
}
