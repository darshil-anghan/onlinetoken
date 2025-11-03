package com.example.mytoken.service;

import com.example.mytoken.model.response.patientqueue.PatientQueueResponse;
import com.example.mytoken.payload.AddTimePayload;
import com.example.mytoken.payload.PatientQueuePayload;
import com.example.mytoken.payload.StartAppointmentPayload;

import java.util.List;

public interface PatientQueueService {

    Object addPatientWithAppointment(PatientQueuePayload patientQueuePayload);

    PatientQueueResponse getPatientQueueList(Long queueId, String status);

    String startAppointment(StartAppointmentPayload startAppointmentPayload);

    String cancelAppointment(Long queueId, Long tokenId);

    String holdAppointment(Long queueId, Long tokenId);

    String completeAppointment(Long queueId, Long tokenId);

    String addTime(AddTimePayload addTimePayload);

    String tokenAnnouncement(Long queueId, Long tokenId);

    String clearQueue(Long queueId);
}
