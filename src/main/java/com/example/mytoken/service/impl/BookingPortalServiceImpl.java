package com.example.mytoken.service.impl;

import com.example.mytoken.model.BookingPortal;
import com.example.mytoken.model.Queue;
import com.example.mytoken.payload.BookingPortalPayload;
import com.example.mytoken.repository.BookingPortalRepository;
import com.example.mytoken.repository.QueueRepository;
import com.example.mytoken.service.BookingPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingPortalServiceImpl implements BookingPortalService {

    private final BookingPortalRepository bookingPortalRepository;
    private final QueueRepository queueRepository;

    @Override
    public void addBookingPortal(BookingPortalPayload payload) {

        boolean portalExists = bookingPortalRepository.existsByQueueId(payload.getQueueId());

        if (portalExists) {
            throw new RuntimeException("A booking portal already exists for this queue.");
        }

        // Check if portal URL is unique
        bookingPortalRepository.findByPortalUrl(payload.getPortalUrl())
                .ifPresent(p -> {
                    throw new RuntimeException("Portal URL already exists");
                });

        // Check if the queue exists
        Queue queue = queueRepository.findById(payload.getQueueId())
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        BookingPortal bookingPortal = BookingPortal.builder()
                .us(payload.getUs())
                .disclaimer(payload.getDisclaimer())
                .websiteUrl(payload.getWebsiteUrl())
                .showWaitlist(payload.isShowWaitlist())
                .portalUrl(payload.getPortalUrl())
                .queue(queue)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookingPortalRepository.save(bookingPortal);
    }

    @Override
    public void updateBookingPortal(Long portalId, BookingPortalPayload payload) {
        BookingPortal bookingPortal = bookingPortalRepository.findById(portalId)
                .orElseThrow(() -> new RuntimeException("Booking Portal not found"));

        // Check if portal URL is unique (except for the current portal)
        if (bookingPortalRepository.findByPortalUrl(payload.getPortalUrl())
                .filter(p -> !p.getId().equals(portalId))
                .isPresent()) {
            throw new RuntimeException("Portal URL already exists");
        }

        Queue queue = queueRepository.findById(payload.getQueueId())
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        bookingPortal.setUs(payload.getUs());
        bookingPortal.setDisclaimer(payload.getDisclaimer());
        bookingPortal.setWebsiteUrl(payload.getWebsiteUrl());
        bookingPortal.setShowWaitlist(payload.isShowWaitlist());
        bookingPortal.setPortalUrl(payload.getPortalUrl());
        bookingPortal.setQueue(queue);
        bookingPortal.setUpdatedAt(LocalDateTime.now());

        bookingPortalRepository.save(bookingPortal);
    }

    @Override
    public void deleteBookingPortal(Long portalId) {
        if (!bookingPortalRepository.existsById(portalId)) {
            throw new RuntimeException("Booking Portal not found");
        }

        bookingPortalRepository.deleteById(portalId);
    }

    @Override
    public Optional<BookingPortalPayload> getBookingPortalsByQueueId(Long queueId) {

        // first fetch queue data and then save in queue.getPortalBookingId() to fetch it
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        if(queue == null || queue.getBookingPortalId() == null){
            throw new RuntimeException("Something went wrong while fetching booking portal id.");
        }

        Optional<BookingPortal> portals = bookingPortalRepository.findById(queue.getBookingPortalId());

        // Check if no portals were found
        if (portals.isEmpty()) {
            throw new RuntimeException("No booking portals found for queue with ID: " + queueId);
        }

        BookingPortal bookingPortal = portals.get();
        BookingPortalPayload payload = new BookingPortalPayload();

        payload.setUs(bookingPortal.getUs());
        payload.setDisclaimer(bookingPortal.getDisclaimer());
        payload.setWebsiteUrl(bookingPortal.getWebsiteUrl());
        payload.setShowWaitlist(bookingPortal.isShowWaitlist());
        payload.setPortalUrl(bookingPortal.getPortalUrl());
        payload.setQueueId(queueId);


        // Mapping BookingPortalProjection to BookingPortalPayload
        return Optional.of(payload);
    }

    @Override
    public boolean checkDomainAvailability(String domain) {
        return bookingPortalRepository.findByPortalUrl(domain).isPresent();
    }

    @Override
    public boolean changeWaitListStatus(Long queueId, boolean showWaitList) {
        Optional<BookingPortal> portalOptional = bookingPortalRepository.findByQueueId(queueId);

        if (portalOptional.isEmpty()) {
            throw new IllegalArgumentException("Booking portal not found for the given queue ID");
        }

        BookingPortal portal = portalOptional.get();
        portal.setShowWaitlist(showWaitList);
        portal.setUpdatedAt(LocalDateTime.now());
        bookingPortalRepository.save(portal);

        return true;
    }

}
