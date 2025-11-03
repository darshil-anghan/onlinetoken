package com.example.mytoken.service.impl;

import com.example.mytoken.model.Subscription;
import com.example.mytoken.model.response.SubscriptionResponse;
import com.example.mytoken.payload.SubscriptionPayload;
import com.example.mytoken.repository.SubscriptionRepository;
import com.example.mytoken.service.SubscriptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionService) {
        this.subscriptionRepository = subscriptionService;
    }

    @Override
    public String addSubscription(SubscriptionPayload subscriptionPayload) {
        Subscription subscription = new Subscription();
        subscription.setName(subscriptionPayload.getName());
        subscription.setDescription(subscriptionPayload.getDescription());
        subscription.setPrice(subscriptionPayload.getPrice());
        subscription.setDuration(subscriptionPayload.getDuration());
        subscription.setActive(subscriptionPayload.isActive());
        subscription.setMaxUsers(subscriptionPayload.getMaxUsers());
        subscription.setMaxQueues(subscriptionPayload.getMaxQueues());

        try {
            subscriptionRepository.save(subscription);
        } catch (Exception e){
            return "Error while adding subscription";
        }

        return "Subscription added successfully";
    }

    @Override
    public String updateSubscription(SubscriptionPayload subscriptionPayload, Long subscriptionId) {
        if (subscriptionId == null) {
            return "Subscription ID cannot be null";
        }

        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
        if (subscription == null) {
            return "Subscription not found";
        }

        subscription.setName(subscriptionPayload.getName());
        subscription.setDescription(subscriptionPayload.getDescription());
        subscription.setPrice(subscriptionPayload.getPrice());
        subscription.setDuration(subscriptionPayload.getDuration());
        subscription.setActive(subscriptionPayload.isActive());
        subscription.setMaxUsers(subscriptionPayload.getMaxUsers());
        subscription.setMaxQueues(subscriptionPayload.getMaxQueues());
        try {
            subscriptionRepository.save(subscription);
            return "Subscription updated successfully";
        } catch (Exception e) {
            return "Error while updating subscription: " + e.getMessage();
        }
    }

    @Override
    public List<SubscriptionResponse> getAllSubscriptions(Long subscriptionId) {
        List<Subscription> subscriptions;

        try{
            if (subscriptionId != null) {
                Subscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
                if (subscription != null) {
                    subscriptions = List.of(subscription);
                } else {
                    return List.of();
                }
            } else {
                subscriptions = subscriptionRepository.findAll();
            }

            return subscriptions.stream()
                .map(sub -> new SubscriptionResponse(
                    sub.getId(),
                    sub.getName(),
                    sub.getDescription(),
                    sub.getPrice(),
                    sub.getDuration(),
                    sub.isActive(),
                    sub.getMaxUsers(),
                    sub.getMaxQueues(),
                    sub.getCreatedDate(),
                    sub.getUpdatedDate()
                ))
                .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching subscriptions: " + e.getMessage());
        }
    }
}
