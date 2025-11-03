package com.example.mytoken.repository;

import com.example.mytoken.model.QueueSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface QueueSessionRepository extends JpaRepository<QueueSession, Long> {

    List<QueueSession> findByQueueId(Long queueId);

    List<QueueSession> findByQueueIdAndDayOfWeek(Long queueId, DayOfWeek dayOfWeek);
}
