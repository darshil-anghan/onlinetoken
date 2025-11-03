package com.example.mytoken.service;

import com.example.mytoken.model.response.SubscriptionResponse;
import com.example.mytoken.payload.SubscriptionPayload;

import java.util.List;

public interface SubscriptionService {

    String addSubscription(SubscriptionPayload subscriptionPayload);

    String updateSubscription(SubscriptionPayload subscriptionPayload, Long subscriptionId);

    List<SubscriptionResponse> getAllSubscriptions(Long subscriptionId);
}
