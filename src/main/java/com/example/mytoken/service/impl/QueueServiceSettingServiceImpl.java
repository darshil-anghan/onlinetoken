package com.example.mytoken.service.impl;

import com.example.mytoken.model.Queue;
import com.example.mytoken.model.QueueServiceSetting;
import com.example.mytoken.payload.QueueServiceSettingPayload;
import com.example.mytoken.repository.QueueServiceSettingRepository;
import com.example.mytoken.repository.QueueRepository;
import com.example.mytoken.service.QueueServiceSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QueueServiceSettingServiceImpl implements QueueServiceSettingService {

    @Autowired
    private QueueServiceSettingRepository settingRepo;

    @Autowired
    private QueueRepository queueRepository;

    @Override
    public QueueServiceSetting saveSettings(Long queueId, QueueServiceSettingPayload payload) {
        Queue queue = queueRepository.findById(queueId).orElse(null);

        if (queue == null) {
            throw new RuntimeException("Queue is not available.");
        }

        QueueServiceSetting setting;

        if (queue.getSettingService() != null) {
            // Update existing setting
            setting = settingRepo.findById(queue.getSettingService()).orElse(new QueueServiceSetting());
        } else {
            // No setting exists yet, create new one
            setting = new QueueServiceSetting();
        }

        // Set all fields from payload
        setting.setOnlineCheckIn(payload.isOnlineCheckIn());
        setting.setOnlineWaitTimeLow(payload.getOnlineWaitTimeLow());
        setting.setOnlineWaitTimeHigh(payload.getOnlineWaitTimeHigh());
        setting.setOnlineOtpVerification(payload.isOnlineOtpVerification());
        setting.setOnlineNotifyUser(payload.isOnlineNotifyUser());
        setting.setOnlineCheckInMobile(payload.isOnlineCheckInMobile());
        setting.setOnlineCheckInEmail(payload.isOnlineCheckInEmail());

        setting.setAppointmentEnable(payload.isAppointmentEnable());
        setting.setAppointmentMaxFutureDays(payload.getAppointmentMaxFutureDays());
        setting.setAppointmentNotifyUsers(payload.isAppointmentNotifyUsers());
        setting.setAppointmentOtpVerification(payload.isAppointmentOtpVerification());
        setting.setAppointmentAptReminder(payload.isAppointmentAptReminder());
        setting.setAppointmentSms(payload.isAppointmentSms());
        setting.setAppointmentMail(payload.isAppointmentMail());

        setting.setAdvanceMarketplace(payload.isAdvanceMarketplace());
        setting.setAdvanceDynamicCapacity(payload.isAdvanceDynamicCapacity());
        setting.setAdvanceBuildCustomerData(payload.isAdvanceBuildCustomerData());
        setting.setAdvanceBusinessNotification(payload.isAdvanceBusinessNotification());
        setting.setAdvanceBreakHour(payload.isAdvanceBreakHour());

        setting = settingRepo.save(setting);

        if (queue.getSettingService() == null) {
            queue.setSettingService(setting.getId());
            queue.setUpdatedDate(LocalDateTime.now());
            queueRepository.save(queue);
        }

        return setting;
    }

    @Override
    public QueueServiceSetting getSettings(Long queueId) {
        Queue queue = queueRepository.findById(queueId).orElse(null);
        if (queue != null && queue.getSettingService() != null) {
            return settingRepo.findById(queue.getSettingService()).orElse(null);
        }
        return null;
    }
}
