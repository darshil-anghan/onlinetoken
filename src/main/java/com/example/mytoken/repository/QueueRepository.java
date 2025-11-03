package com.example.mytoken.repository;

import com.example.mytoken.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {

    List<Queue> findByAdminId(Long adminId);

    List<Queue> findByUserId(Long userId);

    // it returning true
    boolean existsByQueueLink(String queueLink);

    // but in same same link it returning stack overflow
    Queue  findByQueueLink(String queueLink);
}
