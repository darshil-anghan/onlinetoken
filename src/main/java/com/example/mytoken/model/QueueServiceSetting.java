package com.example.mytoken.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "setting_service")
public class QueueServiceSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "online_check_in", nullable = false)
    private boolean onlineCheckIn = false;

    @Column(name = "online_wait_time_low", nullable = false)
    private int onlineWaitTimeLow = 0;

    @Column(name = "online_wait_time_high", nullable = false)
    private int onlineWaitTimeHigh = 0;

    @Column(name = "online_otp_verification", nullable = false)
    private boolean onlineOtpVerification = false;

    @Column(name = "online_notify_user", nullable = false)
    private boolean onlineNotifyUser = false;

    @Column(name = "online_check_in_mobile", nullable = false)
    private boolean onlineCheckInMobile = false;

    @Column(name = "online_check_in_email", nullable = false)
    private boolean onlineCheckInEmail = false;

    @Column(name = "appointment_enable", nullable = false)
    private boolean appointmentEnable = false;

    @Column(name = "appointment_max_future_days", nullable = false)
    private int appointmentMaxFutureDays = 1;

    @Column(name = "appointment_notify_users", nullable = false)
    private boolean appointmentNotifyUsers = false;

    @Column(name = "appointment_otp_verification", nullable = false)
    private boolean appointmentOtpVerification = false;

    @Column(name = "appointment_apt_reminder", nullable = false)
    private boolean appointmentAptReminder = false;

    @Column(name = "appointment_sms", nullable = false)
    private boolean appointmentSms = false;

    @Column(name = "appointment_mail", nullable = false)
    private boolean appointmentMail = false;

    @Column(name = "advance_marketplace", nullable = false)
    private boolean advanceMarketplace = false;

    @Column(name = "advance_dynamic_capacity", nullable = false)
    private boolean advanceDynamicCapacity = false;

    @Column(name = "advance_build_customer_data", nullable = false)
    private boolean advanceBuildCustomerData = false;

    @Column(name = "advance_business_notification", nullable = false)
    private boolean advanceBusinessNotification = false;

    @Column(name = "advance_break_hour", nullable = false)
    private boolean advanceBreakHour = false;

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
