package com.example.mytoken.repository;

import com.example.mytoken.model.PatientQueueOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientQueueOtpRepository extends JpaRepository<PatientQueueOtp, Long> {
    Optional<PatientQueueOtp> findByPatientQueueId(Long patientQueueId);
}
