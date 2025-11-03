package com.example.mytoken.repository;

import com.example.mytoken.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findByQueueIdAndDateBetween(Long queueId, LocalDate start, LocalDate end);

    boolean existsByQueueIdAndDate(Long queueId, LocalDate date);
}
