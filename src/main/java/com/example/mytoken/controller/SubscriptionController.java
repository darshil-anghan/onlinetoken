package com.example.mytoken.controller;

import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.model.response.SubscriptionResponse;
import com.example.mytoken.payload.SubscriptionPayload;
import com.example.mytoken.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController extends BaseController{

    private final SubscriptionService subscriptionService;

    /**
     * This function is use for add new subscription
     */
    @PostMapping
    public ResponseEntity<GlobalResponse> addSubscription(
            @RequestBody SubscriptionPayload subscriptionPayload
    ) {
        try {
            String response = subscriptionService.addSubscription(subscriptionPayload);
            return ok(response, "Subscription added successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     * This function is use for update existing subscription
     */
    @PutMapping
    public ResponseEntity<GlobalResponse> updateSubscription(
            @RequestParam Long subscriptionId,
            @RequestBody SubscriptionPayload subscriptionPayload
    ) {
        try {
            String response = subscriptionService.updateSubscription(subscriptionPayload, subscriptionId);
            return ok(response, "Subscription updated successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     * This function use for get all subscriptions
     */
    @GetMapping
    public ResponseEntity<GlobalResponse> getAllSubscriptions(
            @RequestParam(required = false) Long subscriptionId
    ) {
        try {
            List<SubscriptionResponse> subscriptionResponse = subscriptionService.getAllSubscriptions(subscriptionId);
            return ok(subscriptionResponse, "Subscriptions fetched successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }
}
