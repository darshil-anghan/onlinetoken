package com.example.mytoken.service;

import com.example.mytoken.payload.BookingPortalPayload;

import java.util.Optional;

public interface BookingPortalService {

    void addBookingPortal(BookingPortalPayload payload);

    void updateBookingPortal(Long portalId, BookingPortalPayload payload);

    void deleteBookingPortal(Long portalId);

    Optional<BookingPortalPayload> getBookingPortalsByQueueId(Long queueId);

    boolean checkDomainAvailability(String domain);

    boolean changeWaitListStatus(Long queueId, boolean showWaitList);
}
