package com.example.mytoken.controller;

import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.QueueSessionPayload;
import com.example.mytoken.service.QueueSessionService;
import com.example.mytoken.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue/session")
@RequiredArgsConstructor
public class QueueSessionController extends BaseController{

    private final QueueSessionService sessionService;

    /**
     * This function use for create session(s) for a specific queue
     */
    @PostMapping
    public ResponseEntity<GlobalResponse> createSessions(
            @RequestParam Long queueId,
            @RequestBody List<QueueSessionPayload> sessions) {
        try {
            sessionService.createSessions(queueId, sessions);
            return ok("", "Session created successfully.");
        } catch (Exception e){
            return ok(null, "Error while generating session");
        }
    }

    /**
     * This function use for update a specific session
     */
    @PutMapping
    public ResponseEntity<GlobalResponse> updateSession(
            @RequestParam Long sessionId,
            @RequestBody QueueSessionPayload payload) {
        try {
            sessionService.updateSession(sessionId, payload);
            return ok("", "Session updated successfully");
        } catch (Exception e) {
            return ok(null, "Error while updating session");
        }
    }

    /**
     * This function use for delete a specific session
     */
    @DeleteMapping
    public ResponseEntity<GlobalResponse> deleteSession(@RequestParam Long sessionId) {
        try {
            sessionService.deleteSession(sessionId);
            return ok("", "Session deleted successfully");
        } catch (Exception e) {
            return ok(null, "Error while deleting session");
        }
    }

    /**
     * This function use for get all sessions for a specific queue
     */
    @GetMapping
    public ResponseEntity<GlobalResponse> getSessions(@RequestParam Long queueId) {
        try {
            List<QueueSessionPayload> sessionList = sessionService.getSessionsByQueueId(queueId);
            return ok(sessionList, GlobalConstant.RETRIEVE_SUCCESS_MSG);
        } catch (Exception e) {
            return ok(null, "Error while fetching data");
        }
    }
}
