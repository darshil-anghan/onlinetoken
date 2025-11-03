package com.example.mytoken.service;

import com.example.mytoken.payload.UserSubscriptionPayload;

public interface UserSubscriptionService {

    String addUserSubscription(UserSubscriptionPayload userSubscriptionPayload);

    String changeSubscription(UserSubscriptionPayload userSubscriptionPayload, Long existingSubscriptionId);

    boolean changeStatus(Long userId, Long subscriptionId, boolean status);

    boolean hasActiveSubscription(Long userId);
}
