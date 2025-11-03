package com.example.mytoken.repository;

import com.example.mytoken.model.BookingPortal;
import com.example.mytoken.projection.BookingPortalProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingPortalRepository extends JpaRepository<BookingPortal, Long> {

    Optional<BookingPortal> findByPortalUrl(String portalUrl);

    List<BookingPortalProjection> findAllByQueueId(Long queueId);

    boolean existsByQueueId(Long queueId);

    Optional<BookingPortal> findByQueueId(Long queueId);

}
