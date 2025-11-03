package com.example.mytoken.repository;

import com.example.mytoken.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    List<UserSubscription> findByUserId(Long userId);

    List<UserSubscription> findByUserIdAndSubscriptionId(Long userId, Long subscriptionId);
}
