package com.example.mytoken.service.impl;

import com.example.mytoken.model.Subscription;
import com.example.mytoken.model.UserInfo;
import com.example.mytoken.model.UserSubscription;
import com.example.mytoken.payload.UserSubscriptionPayload;
import com.example.mytoken.repository.SubscriptionRepository;
import com.example.mytoken.repository.UserInfoRepository;
import com.example.mytoken.repository.UserSubscriptionRepository;
import com.example.mytoken.service.UserSubscriptionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserInfoRepository userInfoRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscriptionServiceImpl(
            UserInfoRepository userInfoRepository,
            SubscriptionRepository subscriptionRepository,
            UserSubscriptionRepository userSubscriptionRepository
    ) {
        this.userInfoRepository = userInfoRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    @Override
    public String addUserSubscription(UserSubscriptionPayload userSubscriptionPayload) {
        // Check first user exist and subscription exist
        if (userSubscriptionPayload.getUserId() == null || userSubscriptionPayload.getSubscriptionId() == null) {
            return "User ID and Subscription ID cannot be null";
        }

        UserInfo userInfo = userInfoRepository.findById(userSubscriptionPayload.getUserId()).orElse(null);
        if (userInfo == null) {
            return "User not found";
        }

        Subscription subscription = subscriptionRepository.findById(userSubscriptionPayload.getSubscriptionId()).orElse(null);
        if (subscription == null) {
            return "Subscription not found";
        }

        List<UserSubscription> userSubscriptionsList = userSubscriptionRepository.findByUserId(
                userSubscriptionPayload.getUserId()
        );

        for (UserSubscription userSubscription : userSubscriptionsList) {
            if (userSubscription.isActive() && userSubscription.getEndDate().isAfter(LocalDateTime.now())) {
                return "User already has an active subscription";
            }
        }

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUserId(userInfo.getId());
        userSubscription.setSubscriptionId(subscription.getId());
        userSubscription.setActive(true);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(subscription.getDuration());

        userSubscription.setStartDate(startDate);
        userSubscription.setEndDate(endDate);

        try {
            UserSubscription userSubscriptionDetails = userSubscriptionRepository.save(userSubscription);

            // We are updating the subscription ID for all users under the admin
            List<UserInfo> userInfoList = userInfoRepository.findByAdminId(userInfo.getId());
            for (UserInfo user : userInfoList) {
                user.setSubscriptionId(userSubscriptionDetails.getId());
                userInfoRepository.save(user);
            }

        } catch (Exception e) {
            return "Error while adding user subscription: " + e.getMessage();
        }

        return "User subscription purchased successfully";
    }

    @Override
    public String changeSubscription(UserSubscriptionPayload userSubscriptionPayload, Long existingSubscriptionId) {
        // Validate input
        if (userSubscriptionPayload.getUserId() == null || userSubscriptionPayload.getSubscriptionId() == null || existingSubscriptionId == null) {
            return "User ID, Subscription ID, and Existing Subscription ID cannot be null";
        }

        // Check if user exists
        UserInfo userInfo = userInfoRepository.findById(userSubscriptionPayload.getUserId()).orElse(null);
        if (userInfo == null) {
            return "User not found";
        }

        // Check if new subscription exists
        Subscription newSubscription = subscriptionRepository.findById(userSubscriptionPayload.getSubscriptionId()).orElse(null);
        if (newSubscription == null) {
            return "New subscription not found";
        }

        // Check if the existing user subscription exists and is active
        UserSubscription existingSubscription = userSubscriptionRepository.findById(existingSubscriptionId).orElse(null);
        if (existingSubscription == null || !existingSubscription.isActive() || !existingSubscription.getUserId().equals(userInfo.getId())) {
            return "Existing active subscription not found for the user";
        }

        try {
            // Deactivate the existing subscription
            existingSubscription.setActive(false);
            userSubscriptionRepository.save(existingSubscription);

            // Create and activate the new subscription
            UserSubscription newUserSubscription = new UserSubscription();
            newUserSubscription.setUserId(userInfo.getId());
            newUserSubscription.setSubscriptionId(newSubscription.getId());
            newUserSubscription.setActive(true);

            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusDays(newSubscription.getDuration());

            newUserSubscription.setStartDate(startDate);
            newUserSubscription.setEndDate(endDate);

            UserSubscription savedSubscription = userSubscriptionRepository.save(newUserSubscription);

            // Update subscription ID for all users under this admin (if applicable)
            List<UserInfo> userInfoList = userInfoRepository.findByAdminId(userInfo.getId());
            for (UserInfo user : userInfoList) {
                user.setSubscriptionId(savedSubscription.getId());
                userInfoRepository.save(user);
            }

            return "Subscription changed successfully";
        } catch (Exception e) {
            return "Error while changing subscription: " + e.getMessage();
        }
    }

    @Override
    public boolean changeStatus(Long userId, Long subscriptionId, boolean status) {
        if (userId == null || subscriptionId == null) {
            return false;
        }

        List<UserSubscription> userSubscription = userSubscriptionRepository.findByUserIdAndSubscriptionId(userId, subscriptionId);
        if (userSubscription == null) {
            return false;
        }

        for (UserSubscription subscription : userSubscription) {
            if (subscription.isActive() == status && subscription.getEndDate().isBefore(LocalDateTime.now())){
                return false;
            }
            subscription.setActive(status);
            userSubscriptionRepository.save(subscription);
        }

        return true;
    }

    @Override
    public boolean hasActiveSubscription(Long userId) {
        if (userId == null) {
            return false;
        }

        List<UserSubscription> userSubscriptions = userSubscriptionRepository.findByUserId(userId);
        for (UserSubscription userSubscription : userSubscriptions) {
            if (userSubscription.isActive() && userSubscription.getEndDate().isAfter(LocalDateTime.now())) {
                return true;
            }
        }

        return false;
    }
}
