package com.example.mytoken.controller;

import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.UserSubscriptionPayload;
import com.example.mytoken.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-subscription")
@RequiredArgsConstructor
public class UserSubscriptionController extends BaseController {

    private final UserSubscriptionService userSubscriptionService;

    /**
     * This function use for add new user subscription.
     */
    @PostMapping
    public ResponseEntity<GlobalResponse> addUserSubscription(
            @RequestBody UserSubscriptionPayload userSubscriptionPayload
    ) {
        try {
            String response = userSubscriptionService.addUserSubscription(userSubscriptionPayload);
            return ok(response, "Subscription purchased successfully.");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     * This function use for check if user has active subscription or not.
     */
    @PostMapping("/change-subscription")
    public ResponseEntity<GlobalResponse> changeSubscription(
            @RequestParam Long existingSubscriptionId,
            @RequestBody UserSubscriptionPayload userSubscriptionPayload
    ) {
        try {
            String response = userSubscriptionService.changeSubscription(userSubscriptionPayload, existingSubscriptionId);
            return ok(response, "Subscription changed successfully.");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     * This function use for active or inactive user subscription.
     */
    @PostMapping("/status")
    public ResponseEntity<GlobalResponse> changeSubscriptionStatus(
            @RequestParam Long userId,
            @RequestParam Long subscriptionId,
            @RequestParam boolean status
    ) {
        try {
            boolean isActive = userSubscriptionService.changeStatus(userId, subscriptionId, status);
            return ok(isActive, "User subscription status");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }
}
