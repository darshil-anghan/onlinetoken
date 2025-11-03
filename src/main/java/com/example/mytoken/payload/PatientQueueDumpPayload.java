package com.example.mytoken.payload;

import jakarta.validation.constraints.NotBlank;

public class PatientQueueDumpPayload {

    @NotBlank(message = "token")
    private Long token;

    @NotBlank(message = "queue_id")
    private Long queueId;

    @NotBlank(message = "first_name")
    private String firstName;

    @NotBlank(message = "last_name")
    private String lastName;

    @NotBlank(message = "mobile_number")
    private String mobileNumber;

    @NotBlank(message = "email")
    private String email;

    @NotBlank(message = "notes")
    private String notes;

    @NotBlank(message = "appointment_booking_date")
    private String appointmentBookingDate;

    @NotBlank(message = "appointment_type")
    private String appointmentType;

    @NotBlank(message = "status")
    private String status = "Pending";

    @NotBlank(message = "online_booking_slot")
    private Long onlineBookingSlot;

    @NotBlank(message = "start_time")
    private Long startTime;

    @NotBlank(message = "end_time")
    private Long endTime;

    @NotBlank(message = "delay_time")
    private Long delayTime;

    @NotBlank(message = "exp_service_time")
    private Long expServiceTime;

    @NotBlank(message = "total_service_time")
    private Long totalServiceTime;
}
