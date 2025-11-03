package com.example.mytoken.model.response.patientqueue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PatientDetailsResponse {

    private Long id;
    private Long token;
    private Long queueId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String notes;
    private Long announcement;
    private String appointmentBookingDate;
    private String appointmentType;
    private String status;
    private Long onlineBookingSlot;
    private Long startTime;
    private Long endTime;
    private Long delayTime;
    private Long expServiceTime;
    private Long totalServiceTime;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
