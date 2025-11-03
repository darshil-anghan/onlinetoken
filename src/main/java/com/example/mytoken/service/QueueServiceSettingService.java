package com.example.mytoken.service;

import com.example.mytoken.payload.QueueServiceSettingPayload;
import com.example.mytoken.model.QueueServiceSetting;

public interface QueueServiceSettingService {
    QueueServiceSetting saveSettings(Long queueId, QueueServiceSettingPayload payload);
    QueueServiceSetting getSettings(Long queueId);
}
