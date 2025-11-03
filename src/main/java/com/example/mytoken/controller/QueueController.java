package com.example.mytoken.controller;

import com.example.mytoken.model.Queue;
import com.example.mytoken.model.UserInfo;
import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.QueuePayload;
import com.example.mytoken.service.JwtService;
import com.example.mytoken.service.QueueService;
import com.example.mytoken.service.UserInfoService;
import com.example.mytoken.util.GlobalConstant;
import com.example.mytoken.util.Utility;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueController extends BaseController {

    private final UserInfoService userInfoService;
    private final QueueService queueService;
    private final JwtService jwtService;

    public QueueController(UserInfoService userInfoService,
                           QueueService queueService,
                           JwtService jwtService){
        this.userInfoService = userInfoService;
        this.queueService = queueService;
        this.jwtService = jwtService;
    }

    /**
     *  This function use for create a new queue
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse> addNewQueue(@RequestHeader("Authorization") String token,
                                                      @RequestBody QueuePayload payload) {
        try {
            String email = Utility.getEmailFromToken(token, jwtService);
            UserInfo userInfo = userInfoService.getUserInfoByEmail(email);

            queueService.saveQueue(userInfo.getId(), payload);

            return ok("", "Queue added successfully.");
        } catch (Exception e) {
            return ok(null, "Error while creating queue: " + e.getMessage());
        }
    }

    /**
     *  This function use for update queue data
     */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse> updateQueue(@RequestHeader("Authorization") String token,
                                                      @RequestBody QueuePayload payload) {
        try {
            String email = Utility.getEmailFromToken(token, jwtService);
            UserInfo userInfo = userInfoService.getUserInfoByEmail(email);

            queueService.updateQueue(userInfo.getId(), payload);

            return ok("", "Queue added successfully.");
        } catch (Exception e) {
            return ok(null, "Error while creating queue: " + e.getMessage());
        }
    }

    /**
     *  This function use for assign queue to user
     */
    @PutMapping(value = "/assign_role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse> assignQueueToUser(@RequestHeader("Authorization") String token,
                                                            @RequestParam("userId") Long userId,
                                                            @RequestParam("queueId") Long queueId){
        try {
            String email = Utility.getEmailFromToken(token, jwtService);
            UserInfo userInfo = userInfoService.getUserInfoByEmail(email);

            if (userInfo == null) {
                return ok(null, GlobalConstant.USER_NOT_FOUND_MSG);
            }

            if (userId.equals(userInfo.getId()) && userInfo.isAdmin()) {
                return ok(null, "Admin can't assign queue itself");
            }

            queueService.assignQueueToUser(userId, queueId, userInfo.getId());

            return ok("", "Queue assigned successfully.");
        } catch (Exception e) {
            return ok(null, "Error while assigning queue: " + e.getMessage());
        }
    }

    /**
     *  This function use for fetch all queue data for particular admin
     */
    @GetMapping(value = "/get_all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse> getAdminAllQueue(@RequestHeader("Authorization") String token){
        try {
            String email = Utility.getEmailFromToken(token, jwtService);
            UserInfo userInfo = userInfoService.getUserInfoByEmail(email);

            if (userInfo == null) {
                return ok(null, GlobalConstant.USER_NOT_FOUND_MSG);
            }

            List<Queue> queueList = queueService.getAdminAllQueue(userInfo.getId());

            return ok(queueList, GlobalConstant.RETRIEVE_SUCCESS_MSG);
        } catch (Exception e) {
            return ok(null, "Error while assigning queue: " + e.getMessage());
        }
    }

    /**
     *  This function use for fetch all queue data for particular admin
     *  This API call when user fetching it's data
     */
    @GetMapping(value = "/get_queue_list")
    public ResponseEntity<GlobalResponse> fetchUserQueueList(@RequestParam Long userId){
        try {
            if(userId == null){
                return ok(null, "Invalid UserId");
            }

            List<Queue> queueList = queueService.getUserAllQueue(userId);

            return ok(queueList, GlobalConstant.RETRIEVE_SUCCESS_MSG);
        } catch (Exception e) {
            return ok(null, "Error while fetching queue data");
        }
    }

    /**
     *  This function use for set queue status active or inactive like in that time inactive so no one can book appointment at that time
     */
    @PutMapping(value = "/queue_status")
    public ResponseEntity<GlobalResponse> setActiveStatus(@RequestParam("queueId") Long queueId,
                                                          @RequestParam("isActive") boolean isActive) {
        try {
            queueService.updateQueueActiveStatus(queueId, isActive);

            return ok("", "Queue status updated successfully.");
        } catch (Exception e) {
            return ok(null, "Error while updating queue status: " + e.getMessage());
        }
    }

    /**
     *  This function use for update active counter from dashboard
     */
    @PutMapping(value = "/active_counter")
    public ResponseEntity<GlobalResponse> setActiveCounter(@RequestParam("queueId") Long queueId,
                                                          @RequestParam("noOfCounter") int isActive) {
        try {
            queueService.updateQueueActiveCounter(queueId, isActive);

            return ok("", "Queue status updated successfully.");
        } catch (Exception e) {
            return ok(null, "Error while updating queue status: " + e.getMessage());
        }
    }

    /**
     * This function use for generate a link
     */
    @PostMapping(value = "/generate_link")
    public ResponseEntity<GlobalResponse> generateLink(@RequestParam("queueId") Long queueId) {
        try {
            String tokenLink = queueService.generateToken(queueId);

            return ok(tokenLink, "Queue status updated successfully.");
        } catch (Exception e) {
            return ok(null, "Error while updating queue status: " + e.getMessage());
        }
    }

    /**
     * This function use for get queue data by token
     */
    @GetMapping(value = "/get_queue_by_token")
    public ResponseEntity<GlobalResponse> getQueueByToken(@RequestParam("token") String token) {
        try {
            Queue queue = queueService.getQueueByToken(token);
            if (queue == null) {
                return ok(null, "No queue found for the provided token.");
            }
            return ok(queue, GlobalConstant.RETRIEVE_SUCCESS_MSG);
        } catch (Exception e) {
            return ok(null, "Error while fetching queue data: " + e.getMessage());
        }
    }
}

