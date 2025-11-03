package com.example.mytoken.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientQueueOtpResponse {

    private Long otpId;
    private String msg;
    private Long patientId;
}
