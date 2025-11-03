package com.example.mytoken.service.impl;

import com.example.mytoken.model.PatientQueue;
import com.example.mytoken.model.PatientQueueOtp;
import com.example.mytoken.model.Queue;
import com.example.mytoken.payload.PatientQueueOtpPayload;
import com.example.mytoken.repository.PatientQueueOtpRepository;
import com.example.mytoken.repository.PatientQueueRepository;
import com.example.mytoken.repository.QueueRepository;
import com.example.mytoken.repository.QueueServiceSettingRepository;
import com.example.mytoken.service.PatientQueueOtpService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PatientQueueOtpServiceImpl implements PatientQueueOtpService {

    private final QueueRepository queueRepository;
    private final PatientQueueOtpRepository otpRepository;
    private final PatientQueueRepository patientQueueRepository;
    private final QueueServiceSettingRepository queueServiceSettingRepository;

    public PatientQueueOtpServiceImpl(
            QueueRepository queueRepository,
            PatientQueueOtpRepository otpRepository,
            PatientQueueRepository patientQueueRepository,
            QueueServiceSettingRepository queueServiceSettingRepository
    ) {
        this.queueRepository = queueRepository;
        this.otpRepository = otpRepository;
        this.patientQueueRepository = patientQueueRepository;
        this.queueServiceSettingRepository = queueServiceSettingRepository;
    }

    @Override
    public boolean verifyOtp(PatientQueueOtpPayload patientQueueOtpPayload) {
        Optional<PatientQueueOtp> patientOtp = otpRepository.findById(patientQueueOtpPayload.getOtpId());

        if (patientOtp.isPresent()) {
            PatientQueueOtp otpEntity = patientOtp.get();

            if (LocalDateTime.now().isAfter(otpEntity.getExpiryDate())){
                throw new RuntimeException("OTP has been expired.");
            }

            if (!otpEntity.getOtp().equals(patientQueueOtpPayload.getOtp())) {
                throw new RuntimeException("OTP can't match please try again later.");
            }

            Optional<PatientQueue> patientQueue = patientQueueRepository.findById(patientOtp.get().getPatientQueueId());

            if (patientQueue.isEmpty()){
                throw new RuntimeException("Patient Queue id invalid.");
            }

            Optional<Queue> queue = queueRepository.findById(patientQueue.get().getQueueId());

            if (queue.isEmpty()){
                throw new RuntimeException("QueueId not found");
            }

            /*
             * Change status to pending and generate token number
             */
            PatientQueue patient = patientQueue.get();
            patient.setToken(queue.get().getLastToken() + 1);
            patient.setStatus("Pending");
            patient.setUpdatedDate(LocalDateTime.now());
            patientQueueRepository.save(patient);

            /*
             * Increment next token number here
             */
            Queue actualQueue = queue.get();
            actualQueue.setLastToken(actualQueue.getLastToken() + 1);
            queueRepository.save(actualQueue);

            return true;
        }
        return false;
    }
}
