package com.example.mytoken.repository;

import com.example.mytoken.model.PatientQueueDump;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientQueueDumpRepository extends JpaRepository<PatientQueueDump, Long> {

    List<PatientQueueDump> findByQueueIdAndStatusAndStartTimeBetween(Long queueId, String status, Long startTime, Long endTime);
}
