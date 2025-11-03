package com.example.mytoken.repository;

import com.example.mytoken.model.QueueServiceSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueServiceSettingRepository extends JpaRepository<QueueServiceSetting, Long> {
}
