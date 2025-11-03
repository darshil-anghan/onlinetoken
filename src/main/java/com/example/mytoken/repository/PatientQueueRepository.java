package com.example.mytoken.repository;

import com.example.mytoken.model.PatientQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientQueueRepository extends JpaRepository<PatientQueue, Long>  {

    Optional<PatientQueue> findTopByQueueIdOrderByTokenDesc(Long queueId);

    boolean existsByQueueIdAndAppointmentBookingDateAndOnlineBookingSlot(Long queueId, String appointmentBookingDate, Long onlineBookingSlot);

    List<PatientQueue> findByQueueId(Long queueId);

    Optional<PatientQueue> findByIdAndQueueId(Long queueId, Long token);

    long countByQueueIdAndStatus(Long queueId, String status);
}
