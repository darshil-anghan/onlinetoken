package com.example.mytoken.controller;

import com.example.mytoken.payload.BookingPortalPayload;
import com.example.mytoken.service.BookingPortalService;
import com.example.mytoken.model.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/booking-portal")
@RequiredArgsConstructor
public class BookingPortalController extends BaseController {

    private final BookingPortalService bookingPortalService;

    /**
     *  This function do not use
     */
    @PostMapping
    public ResponseEntity<GlobalResponse> addBookingPortal(@RequestBody BookingPortalPayload payload) {
        try {
            bookingPortalService.addBookingPortal(payload);
            return ok("", "Booking portal created successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     *  This function use for update booking portal details.
     */
    @PutMapping("/{portalId}")
    public ResponseEntity<GlobalResponse> updateBookingPortal(@PathVariable Long portalId,
                                                              @RequestBody BookingPortalPayload payload) {
        try {
            bookingPortalService.updateBookingPortal(portalId, payload);
            return ok("", "Booking portal updated successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     *  This function never use
     */
    @DeleteMapping("/{portalId}")
    public ResponseEntity<GlobalResponse> deleteBookingPortal(@PathVariable Long portalId) {
        try {
            bookingPortalService.deleteBookingPortal(portalId);
            return ok("", "Booking portal deleted successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     *  This function fetch booking portal details
     */
    @GetMapping("/queue/{queueId}")
    public ResponseEntity<GlobalResponse> getBookingPortalsByQueueId(@PathVariable Long queueId) {
        try {
            Optional<BookingPortalPayload> bookingPortals = bookingPortalService.getBookingPortalsByQueueId(queueId);
            return ok(bookingPortals, "Booking portals fetched successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    /**
     *  This function use for check portal domain name available or not
     */
    @GetMapping("/queue/portal_domain")
    public ResponseEntity<GlobalResponse> checkPortalDomainAvailable(@RequestParam("domain") String domain) {
        try {
            boolean available = bookingPortalService.checkDomainAvailability(domain);
            if (available){
                return ok(null, "Booking portal name not available.");
            } else {
                return ok(true, "Booking portal name available.");
            }
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }

    @PutMapping("/queue/show_waitlist")
    public ResponseEntity<GlobalResponse> setWaitListStatus(
            @RequestParam("queueId") Long queueId,
            @RequestParam("show") boolean showWaitList) {
        try {
            boolean changed = bookingPortalService.changeWaitListStatus(queueId, showWaitList);
            return ok(null, "WaitList status updated to: " + showWaitList);
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }
}
