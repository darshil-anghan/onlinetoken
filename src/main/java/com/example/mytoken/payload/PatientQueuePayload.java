package com.example.mytoken.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientQueuePayload {

    @NotBlank(message = "Patient name must be required.")
    private String firstName;

    @NotBlank(message = "Queue Id not be blank.")
    private Long queueId;

    @NotBlank(message = "Appointment booking date can't be blank.")
    private String appointmentBookingDate;

    private String lastName;

    private String mobileNumber;

    private String email;

    private String notes;

    private String appointmentType;

    private Long onlineBookingSlot;
}
