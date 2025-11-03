package com.example.mytoken.controller;

import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.HolidayPayload;
import com.example.mytoken.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/holiday")
@RequiredArgsConstructor
public class HolidayController extends BaseController {

    private final HolidayService holidayService;

    /**
     * This function use for add holiday
     */
    @PostMapping
    public ResponseEntity<GlobalResponse> addHoliday(@RequestParam Long queueId,
                                                     @RequestBody HolidayPayload payload) {
        try {
            holidayService.addHoliday(queueId, payload);
            return ok("", "Holiday added successfully");
        } catch (Exception e) {
            return ok(null, "Error while creating holiday.");
        }
    }

    /**
     * This function use for update holiday
     */
    @PutMapping
    public ResponseEntity<GlobalResponse> updateHoliday(@RequestParam Long holidayId,
                                                        @RequestBody HolidayPayload payload) {
        try {
            holidayService.updateHoliday(holidayId, payload);
            return ok("", "Holiday updated successfully");
        } catch (Exception e) {
            return ok(null, "Error while updating holiday.");
        }
    }

    /**
     * This function use for delete holiday
     */
    @DeleteMapping
    public ResponseEntity<GlobalResponse> deleteHoliday(@RequestParam Long holidayId) {
        try {
            holidayService.deleteHoliday(holidayId);
            return ok("", "Holiday deleted successfully");
        } catch (Exception e) {
            return ok(null, "Error while deleting holiday.");
        }
    }

    /**
     * This function use for get all holiday of specific month
     */
    @GetMapping("/monthly")
    public ResponseEntity<GlobalResponse> getMonthlyHolidays(@RequestParam Long queueId,
                                                                   @RequestParam int year,
                                                                   @RequestParam int month) {
        try {
            List<HolidayPayload> holidayList = holidayService.getHolidaysByMonth(queueId, year, month);
            return ok(holidayList, "Holiday details fetch successfully");
        } catch (Exception e) {
            return ok(null, "Error while fetching holiday list.");
        }
    }

    /**
     * This function use for get all holiday for a specific year
     */
    @GetMapping("/yearly")
    public ResponseEntity<GlobalResponse> getYearlyHolidays(@RequestParam Long queueId,
                                                                  @RequestParam int year) {
        try {
            List<HolidayPayload> holidayList = holidayService.getHolidaysByYear(queueId, year);
            return ok(holidayList, "Holiday details fetch successfully");
        } catch (Exception e) {
            return ok(null, "Error while fetching holiday list.");
        }
    }
}
