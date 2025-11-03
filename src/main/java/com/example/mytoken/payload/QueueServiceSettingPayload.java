package com.example.mytoken.payload;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueServiceSettingPayload {

    private boolean onlineCheckIn;
    private int onlineWaitTimeLow;
    private int onlineWaitTimeHigh;
    private boolean onlineOtpVerification;
    private boolean onlineNotifyUser;
    private boolean onlineCheckInMobile;
    private boolean onlineCheckInEmail;

    private boolean appointmentEnable;
    private int appointmentMaxFutureDays;
    private boolean appointmentNotifyUsers;
    private boolean appointmentOtpVerification;
    private boolean appointmentAptReminder;
    private boolean appointmentSms;
    private boolean appointmentMail;

    private boolean advanceMarketplace;
    private boolean advanceDynamicCapacity;
    private boolean advanceBuildCustomerData;
    private boolean advanceBusinessNotification;
    private boolean advanceBreakHour;
}
