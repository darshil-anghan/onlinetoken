package com.example.mytoken.controller;

import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.model.response.patientqueue.PatientQueueResponse;
import com.example.mytoken.payload.AddTimePayload;
import com.example.mytoken.payload.PatientQueuePayload;
import com.example.mytoken.payload.StartAppointmentPayload;
import com.example.mytoken.service.PatientQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patient_queue")
public class PatientQueueController extends BaseController {

    @Autowired
    private final PatientQueueService patientQueueService;

    /**
     * This function use for book WalkIn/Online appointment
     */
    @PostMapping
    public ResponseEntity<GlobalResponse> addNewPatientInQueue(
            @RequestBody PatientQueuePayload patientQueuePayload
    ) {
        try {
            Object result = patientQueueService.addPatientWithAppointment(patientQueuePayload);
            return ok(result, "Patient added successfully.");
        } catch (Exception e) {
            return ok(null, e.getMessage());
        }
    }

    /**
     * This function use for fetch patient queue list it contains
     * - Pending
     * - Processing
     * - Hold
     * - Completed
     * - Cancelled
     * This will be passed in response
     *
     * This API use for only admin side not for user side
     */
    @GetMapping
    public ResponseEntity<GlobalResponse> getPatientList(
            @RequestParam Long queueId,
            @RequestParam String status
    ){
        try {
            PatientQueueResponse patientQueueResponses = patientQueueService.getPatientQueueList(queueId, status);
            return ok(patientQueueResponses, "Patient list fetch successfully.");
        } catch (Exception e) {
            return ok(null, e.getMessage());
        }
    }

    /**
     *  This function use for start appointment from waiting list
     */
    @PostMapping("/start_appointment")
    public ResponseEntity<GlobalResponse> startAppointment(
            @RequestBody StartAppointmentPayload startAppointmentPayload
    ){
        try {
            String responseMessage = patientQueueService.startAppointment(startAppointmentPayload);
            return ok(responseMessage, "Appointment Started.");
        } catch (Exception e) {
            return ok(null, e.getMessage());
        }
    }

    /**
     *  This function use for cancel appointment
     */
    @PostMapping("/cancel_appointment")
    public ResponseEntity<GlobalResponse> cancelAppointment(
            @RequestParam Long queueId,
            @RequestParam Long tokenId
    ){
        try {
            String responseMessage = patientQueueService.cancelAppointment(queueId, tokenId);
            return ok(responseMessage, "Appointment Canceled.");
        } catch (Exception e) {
            return ok(null, e.getMessage());
        }
    }

    /**
     *  This function use for hold any appointment
     */
    @PostMapping("/hold_appointment")
    public ResponseEntity<GlobalResponse> holdAppointment(
            @RequestParam Long queueId,
            @RequestParam Long tokenId
    ){
        try {
            String responseMessage = patientQueueService.holdAppointment(queueId, tokenId);
            return ok(responseMessage, "Appointment Now on Hold.");
        } catch (Exception e) {
            return ok(null, e.getMessage());
        }
    }

    /**
     *  This function use for complete appointment
     */
    @PostMapping("/complete_appointment")
    public ResponseEntity<GlobalResponse> completeAppointment(
            @RequestParam Long queueId,
            @RequestParam Long tokenId
    ){
        try {
            String responseMessage = patientQueueService.completeAppointment(queueId, tokenId);
            return ok(responseMessage, "Appointment Completed.");
        } catch (Exception e) {
            return ok(null, e.getMessage());
        }
    }

    /**
     *  This function use for add time while serving appointment
     */
    @PostMapping("/add_time")
    public ResponseEntity<GlobalResponse> addTime(
            @RequestBody AddTimePayload addTimePayload
    ){
        try {
            String responseMessage = patientQueueService.addTime(addTimePayload);
            return ok(responseMessage, "Time added.");
        } catch (Exception e){
            return ok(null, e.getMessage());
        }
    }

    /**
     * This function use for announce the token number again
     */
    @PostMapping("/token_announcement")
    public ResponseEntity<GlobalResponse> tokenAnnouncement(
            @RequestParam Long queueId,
            @RequestParam Long tokenId
    ){
        try {
            String responseMessage = patientQueueService.tokenAnnouncement(queueId, tokenId);
            return ok(responseMessage, "We will announcement in few second.");
        } catch (Exception e){
            return ok(null, e.getMessage());
        }
    }

    /**
     * This function use for move all appointment to dump
     * Logic: What if organization don't wants to continue appointment from last token.
     */
    @GetMapping("/clear_queue")
    public ResponseEntity<GlobalResponse> clearQueue(
            @RequestParam Long queueId
    ){
        try {
            String responseMessage = patientQueueService.clearQueue(queueId);
            return ok(responseMessage, "Queue cleared.");
        } catch (Exception e){
            return ok(null, e.getMessage());
        }
    }
}
