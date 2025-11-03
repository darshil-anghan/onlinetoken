package com.example.mytoken.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "patient_queue_dump")
@AllArgsConstructor
@NoArgsConstructor
public class PatientQueueDump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private Long token;

    @Column(name = "queue_id")
    private Long queueId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "notes")
    private String notes;

    @Column(name = "announcement", nullable = false)
    private Long announcement = 0L;

    @Column(name = "appointment_booking_date")
    private String appointmentBookingDate;

    @Column(name = "appointment_type")
    private String appointmentType; // WalkIn or Online

    @Column(name = "status")
    private String status = "Pending";

    @Column(name = "online_booking_slot")
    private Long onlineBookingSlot; // Online booking use only

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "delay_time")
    private Long delayTime;

    @Column(name = "exp_service_time") // Use for showing how many time will be taken while appointment
    private Long expServiceTime; // Without hold or new delay time

    @Column(name = "total_service_time") // when appointment done this occurs
    private Long totalServiceTime;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
