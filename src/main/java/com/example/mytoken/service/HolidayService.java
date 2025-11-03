package com.example.mytoken.service;

import com.example.mytoken.payload.HolidayPayload;

import java.util.List;

public interface HolidayService {
    void addHoliday(Long queueId, HolidayPayload payload);
    void updateHoliday(Long holidayId, HolidayPayload payload);
    void deleteHoliday(Long holidayId);
    List<HolidayPayload> getHolidaysByMonth(Long queueId, int year, int month);
    List<HolidayPayload> getHolidaysByYear(Long queueId, int year);
}
