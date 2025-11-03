package com.example.mytoken.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "patient_queue")
@AllArgsConstructor
@NoArgsConstructor
public class PatientQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", nullable = false)
    private Long token;

    @Column(name = "queue_id", nullable = false)
    private Long queueId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "notes", nullable = false)
    private String notes;

    @Column(name = "announcement", nullable = false)
    private Long announcement = 0L;

    @Column(name = "appointment_booking_date", nullable = false)
    private String appointmentBookingDate;

    @Column(name = "appointment_type", nullable = false)
    private String appointmentType; // WalkIn or Online

    @Column(name = "status", nullable = false)
    private String status = "Pending";

    @Column(name = "online_booking_slot", nullable = false)
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

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
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
