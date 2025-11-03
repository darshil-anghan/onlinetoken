package com.example.mytoken.projection;

public interface QueueServiceSettingProjection {
    boolean isOnlineCheckIn();
    int getOnlineWaitTimeLow();
    int getOnlineWaitTimeHigh();
    boolean isOnlineOtpVerification();
    boolean isOnlineNotifyUser();
    boolean isOnlineCheckInMobile();
    boolean isOnlineCheckInEmail();
    boolean isAppointmentEnable();
    int getAppointmentMaxFutureDays();
    boolean isAppointmentNotifyUsers();
    boolean isAppointmentOtpVerification();
    boolean isAppointmentAptReminder();
    boolean isAppointmentSms();
    boolean isAppointmentMail();
    boolean isAdvanceMarketplace();
    boolean isAdvanceDynamicCapacity();
    boolean isAdvanceBuildCustomerData();
    boolean isAdvanceBusinessNotification();
    boolean isAdvanceBreakHour();
}
