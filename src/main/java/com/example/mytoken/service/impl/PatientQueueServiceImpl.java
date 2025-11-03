package com.example.mytoken.service.impl;

import com.example.mytoken.model.*;
import com.example.mytoken.model.Queue;
import com.example.mytoken.model.response.AvailableSlotsResponse;
import com.example.mytoken.model.response.PatientQueueOtpResponse;
import com.example.mytoken.model.response.patientqueue.PatientDetailsResponse;
import com.example.mytoken.model.response.patientqueue.PatientQueueResponse;
import com.example.mytoken.payload.AddTimePayload;
import com.example.mytoken.payload.PatientQueuePayload;
import com.example.mytoken.payload.StartAppointmentPayload;
import com.example.mytoken.repository.*;
import com.example.mytoken.service.*;
import com.example.mytoken.util.GlobalConstant;
import com.example.mytoken.util.Utility;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientQueueServiceImpl implements PatientQueueService {

    private final SlotService slotService;
    private final EmailService emailService;
    private final QueueRepository queueRepository;
    private final UserInfoService userInfoService;
    private final PatientQueueRepository patientQueueRepository;
    private final UserSubscriptionService userSubscriptionService;
    private final PatientQueueOtpRepository patientQueueOtpRepository;
    private final PatientQueueDumpRepository patientQueueDumpRepository;
    private final QueueServiceSettingRepository queueServiceSettingRepository;

    @Autowired
    public PatientQueueServiceImpl(SlotService slotService,
                                   EmailService emailService,
                                   QueueRepository queueRepository,
                                   UserInfoService userInfoService,
                                   PatientQueueRepository patientQueueRepository,
                                   UserSubscriptionService userSubscriptionService,
                                   PatientQueueOtpRepository patientQueueOtpRepository,
                                   PatientQueueDumpRepository patientQueueDumpRepository,
                                   QueueServiceSettingRepository queueServiceSettingRepository) {
        this.slotService = slotService;
        this.emailService = emailService;
        this.queueRepository = queueRepository;
        this.userInfoService = userInfoService;
        this.patientQueueRepository = patientQueueRepository;
        this.userSubscriptionService = userSubscriptionService;
        this.patientQueueOtpRepository = patientQueueOtpRepository;
        this.patientQueueDumpRepository = patientQueueDumpRepository;
        this.queueServiceSettingRepository = queueServiceSettingRepository;
    }

    @Override
    public Object addPatientWithAppointment(PatientQueuePayload payload) {

        if(payload.getQueueId() == null){
            throw new RuntimeException("QueueId not found");
        }

        Optional<Queue> queue = queueRepository.findById(payload.getQueueId());

        if (queue.isEmpty()){
            throw new RuntimeException("Queue not found, enter correct queueId.");
        }

        if(!queue.get().isActive()){
            throw new RuntimeException("We are currently not accepting any king of appointment.");
        }

        UserInfo userInfo = userInfoService.getById(queue.get().getUserId());
        if (userInfo == null) {
            throw new RuntimeException("User not found for the given queue.");
        }

        if (!userSubscriptionService.hasActiveSubscription(userInfo.getAdminId() == null ? userInfo.getId() : userInfo.getAdminId() )) {
            throw new RuntimeException("Organization is not active.");
        }

        Optional<QueueServiceSetting> queueServiceSetting = queueServiceSettingRepository.findById(queue.get().getSettingService());

        if (queueServiceSetting.isEmpty()){
            throw new RuntimeException("Something went wrong please contact to developer.");
        }

        if (payload.getAppointmentType().equals(GlobalConstant.ONLINE)) {
            AvailableSlotsResponse availableSlotsResponse = slotService.getAvailableSlots(queue.get().getId(), payload.getAppointmentBookingDate());

            boolean isSlotValidAndAvailable = availableSlotsResponse.getAvailableSlots().stream()
                    .anyMatch(slot ->
                            Objects.equals(slot.getSlot(), payload.getOnlineBookingSlot()) && slot.isAvailable());

            if (!isSlotValidAndAvailable) {
                throw new RuntimeException("Selected time slot is not available or already booked.");
            }
        }

        /*
         * Saving patient details here
         */
        PatientQueue patient = new PatientQueue();
        patient.setQueueId(payload.getQueueId());
        patient.setFirstName(payload.getFirstName());
        patient.setLastName(payload.getLastName());
        patient.setMobileNumber(payload.getMobileNumber());
        patient.setEmail(payload.getEmail());
        patient.setNotes(payload.getNotes());
        patient.setAppointmentType(payload.getAppointmentType());
        patient.setAppointmentBookingDate(payload.getAppointmentBookingDate());
        patient.setOnlineBookingSlot(payload.getOnlineBookingSlot());
        patient.setStatus(queueServiceSetting.get().isAppointmentOtpVerification() ? "" : GlobalConstant.STATUS_WAITING);
        patient.setCreatedDate(LocalDateTime.now());
        patient.setUpdatedDate(LocalDateTime.now());
        patient.setToken(queueServiceSetting.get().isAppointmentOtpVerification() ? 0 : queue.get().getLastToken() + 1);

        /*
         * This logic is use for when user come with offline
         */
        if (payload.getAppointmentType().equals(GlobalConstant.WALK_IN)){
            patient.setStatus(GlobalConstant.STATUS_WAITING);
            patientQueueRepository.save(patient);

            return "Patient added to Queue";
        }

        patient = patientQueueRepository.save(patient);

        /*
         * If Email OTP verification on then we are sending OTP here
         * - While sending email decrease credits logic made here
         * - This logic user for when user will book "ONLINE APPOINTMENT" with email verification
         */
        if (payload.getAppointmentType().equals(GlobalConstant.ONLINE) && queueServiceSetting.get().isAppointmentOtpVerification()) {
            String otp = Utility.generateOtp1();
            PatientQueueOtp patientQueueOtp = new PatientQueueOtp();
            patientQueueOtp.setPatientQueueId(patient.getId());
            patientQueueOtp.setOtp(otp);
            patientQueueOtp.setCreatedDate(LocalDateTime.now());
            patientQueueOtp.setUpdatedDate(LocalDateTime.now());
            patientQueueOtp.setExpiryDate(LocalDateTime.now().plusMinutes(5));
            patientQueueOtpRepository.save(patientQueueOtp);

            /*
             * Send OTP and magic link to mail
             */
            emailService.sendAppointmentOtpEmail(patient.getEmail(), otp, patientQueueOtp.getId(), patient.getId());

            /*
             * Add generated OTP record in table
             */
            PatientQueueOtpResponse patientQueueOtpResponse = new PatientQueueOtpResponse();
            patientQueueOtpResponse.setMsg("OTP sent to your mail please check your mail id.");
            patientQueueOtpResponse.setOtpId(patientQueueOtp.getId());
            patientQueueOtpResponse.setPatientId(patient.getId());
            return patientQueueOtpResponse;
        }

        return Map.of("msg", "Token generated: " + patient.getToken());
    }

    @Override
    public PatientQueueResponse getPatientQueueList(Long queueId, String status) {
        if (queueId == null) {
            throw new RuntimeException("QueueId not found");
        }

        Optional<Queue> optionalQueue = queueRepository.findById(queueId);
        if (optionalQueue.isEmpty()) {
            throw new RuntimeException("Queue not found, enter correct queueId.");
        }

        Queue queue = optionalQueue.get();

        List<PatientQueue> patientQueueList = patientQueueRepository.findByQueueId(queueId);
        if (patientQueueList == null || patientQueueList.isEmpty()) {
            return null;
        }

        long currentTime = System.currentTimeMillis();
        long avgServiceDurationMs = (long) queue.getAvgServiceDuration() * 60 * 1000;

        // ✅ Move expired appointments to dump
        List<PatientQueue> expiredAppointments = patientQueueList.stream()
                .filter(p -> p.getEndTime() != null && p.getEndTime() <= currentTime)
                .collect(Collectors.toList());

        if (!expiredAppointments.isEmpty()) {
            List<PatientQueueDump> dumpList = expiredAppointments.stream()
                    .map(p -> {
                        PatientQueueDump dump = new PatientQueueDump();
                        BeanUtils.copyProperties(p, dump);
                        dump.setStatus("Completed");
                        return dump;
                    })
                    .collect(Collectors.toList());

            patientQueueDumpRepository.saveAll(dumpList);
            patientQueueRepository.deleteAll(expiredAppointments);
            patientQueueList.removeAll(expiredAppointments);
        }

        // Separate appointments
        List<PatientQueue> onlineAppointments = patientQueueList.stream()
                .filter(p -> "Online".equalsIgnoreCase(p.getAppointmentType()))
                .toList();

        List<PatientQueue> walkInAppointments = patientQueueList.stream()
                .filter(p -> "WalkIn".equalsIgnoreCase(p.getAppointmentType()))
                .toList();

        // Appointments that already have a token
        List<PatientQueue> resultList = patientQueueList.stream()
                .filter(p -> p.getToken() != null && p.getToken() > 0)
                .sorted(Comparator.comparing(PatientQueue::getStartTime, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());

        long estimatedStartTime = currentTime;

        List<PatientDetailsResponse> responseList = new ArrayList<>();

        // 🧩 Step 1: Prepare responses for already-tokenized patients
        for (PatientQueue patient : resultList) {
            Long startTime = patient.getStartTime() != null ? patient.getStartTime() : estimatedStartTime;
            Long endTime = patient.getEndTime();

            if (endTime == null || endTime == 0) {
                long serviceTime = (patient.getExpServiceTime() != null && patient.getExpServiceTime() > 0)
                        ? patient.getExpServiceTime()
                        : queue.getAvgServiceDuration();
                endTime = startTime + serviceTime * 60 * 1000;
            }

            // ⏱️ If appointment is not completed/cancelled and actual time has passed
            if (!GlobalConstant.STATUS_COMPLETED.equalsIgnoreCase(patient.getStatus())
                    && !GlobalConstant.STATUS_CANCELED.equalsIgnoreCase(patient.getStatus())
                    && endTime < currentTime) {
                // Extend current appointment by 1 more minute
                endTime = currentTime + (1 * 60 * 1000);
            }

            // Add response
            responseList.add(mapToQueueResponse(patient, startTime, endTime));

            // Update estimatedStartTime for next patient
            estimatedStartTime = endTime;

            if (patient.getAnnouncement() != 0){
                patient.setAnnouncement(0L);
                patientQueueRepository.save(patient);
            }
        }

        // 🧩 Step 2: Prepare list of remaining appointments (without token)
        List<PatientQueue> remainingOnline = onlineAppointments.stream()
                .filter(p -> p.getToken() == null || p.getToken() == 0)
                .sorted(Comparator.comparing(PatientQueue::getStartTime, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());

        List<PatientQueue> remainingWalkIn = walkInAppointments.stream()
                .filter(p -> p.getToken() == null || p.getToken() == 0)
                .sorted(Comparator.comparing(PatientQueue::getStartTime, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());

        // 🧩 Step 3: Dynamically assign tokens and schedule new appointments
        while (!remainingOnline.isEmpty() || !remainingWalkIn.isEmpty()) {
            boolean added = false;

            if (!remainingOnline.isEmpty()) {
                PatientQueue online = remainingOnline.get(0);
                if (online.getStartTime() != null && online.getStartTime() <= currentTime) {
                    generateTokenOnly(online, queue);

                    Long startTime = estimatedStartTime;
                    Long endTime = startTime + avgServiceDurationMs;

                    responseList.add(mapToQueueResponse(online, startTime, endTime));
                    estimatedStartTime = endTime;

                    remainingOnline.remove(0);
                    added = true;
                }
            }

            if (!added && !remainingWalkIn.isEmpty()) {
                PatientQueue walkIn = remainingWalkIn.get(0);
                generateTokenOnly(walkIn, queue);

                Long startTime = estimatedStartTime;
                Long endTime = startTime + avgServiceDurationMs;

                responseList.add(mapToQueueResponse(walkIn, startTime, endTime));
                estimatedStartTime = endTime;

                remainingWalkIn.remove(0);
            } else if (!added) {
                break;
            }
        }

        long waitingCount = responseList.stream().filter(p -> GlobalConstant.STATUS_WAITING.equalsIgnoreCase(p.getStatus())).count();
        long processCount = responseList.stream().filter(p -> GlobalConstant.STATUS_PROCESSING.equalsIgnoreCase(p.getStatus())).count();
        long holdCount = responseList.stream().filter(p -> "Hold".equalsIgnoreCase(p.getStatus())).count();
        long completedCount = responseList.stream().filter(p -> GlobalConstant.STATUS_COMPLETED.equalsIgnoreCase(p.getStatus())).count();
        long cancelledCount = responseList.stream().filter(p -> GlobalConstant.STATUS_CANCELED.equalsIgnoreCase(p.getStatus())).count();

        // ⏱️🆕 Calculate waiting time for new patient
        int activeCounter = Math.max(1, queue.getActiveCounter());
        List<Long> slotAvailableAt = new ArrayList<>(Collections.nCopies(activeCounter, currentTime));

        for (PatientDetailsResponse response : responseList) {
            if (GlobalConstant.STATUS_WAITING.equalsIgnoreCase(response.getStatus())
                    || GlobalConstant.STATUS_PROCESSING.equalsIgnoreCase(response.getStatus())
                        || GlobalConstant.STATUS_ON_HOLD.equalsIgnoreCase(response.getStatus())
            ) {
                Long endTime = response.getEndTime();
                int minIndex = slotAvailableAt.indexOf(Collections.min(slotAvailableAt));
                slotAvailableAt.set(minIndex, endTime != null ? endTime : currentTime);
            }
        }

        long earliestFreeSlot = Collections.min(slotAvailableAt);
        long waitingTimeMin = Math.max(0, (earliestFreeSlot - currentTime) / (60 * 1000)); // in minutes

        List<PatientDetailsResponse> filteredList = new ArrayList<>();

        if (status == null || "All".equalsIgnoreCase(status)) {
            filteredList.addAll(responseList);

            // 📥 Include today's completed from dump
            LocalDate today = LocalDate.now();
            Long startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            Long endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;

            List<PatientQueueDump> completedDumpList = patientQueueDumpRepository
                    .findByQueueIdAndStatusAndStartTimeBetween(queue.getId(), GlobalConstant.STATUS_COMPLETED, startOfDay, endOfDay);

            for (PatientQueueDump patient : completedDumpList) {
                Long startTime = patient.getStartTime();
                Long endTime = patient.getEndTime() != null
                        ? patient.getEndTime()
                        : startTime + queue.getAvgServiceDuration() * 60 * 1000;

                filteredList.add(mapToQueueDumpResponse(patient, startTime, endTime));
            }

        } else if ("Waiting".equalsIgnoreCase(status)) {
            // Return both waiting and hold
            filteredList = responseList.stream()
                    .filter(p -> GlobalConstant.STATUS_WAITING.equalsIgnoreCase(p.getStatus()) ||
                            GlobalConstant.STATUS_ON_HOLD.equalsIgnoreCase(p.getStatus()))
                    .collect(Collectors.toList());

        } else if (GlobalConstant.STATUS_PROCESSING.equalsIgnoreCase(status)) {
            filteredList = responseList.stream()
                    .filter(p -> GlobalConstant.STATUS_PROCESSING.equalsIgnoreCase(p.getStatus()))
                    .collect(Collectors.toList());

        } else if (GlobalConstant.STATUS_COMPLETED.equalsIgnoreCase(status) ||
                GlobalConstant.STATUS_CANCELED.equalsIgnoreCase(status)) {

            // Fetch only from dump
            LocalDate today = LocalDate.now();
            Long startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            Long endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;

            List<PatientQueueDump> dumpList = patientQueueDumpRepository
                    .findByQueueIdAndStatusAndStartTimeBetween(queue.getId(), status, startOfDay, endOfDay);

            for (PatientQueueDump patient : dumpList) {
                Long startTime = patient.getStartTime();
                Long endTime = patient.getEndTime() != null
                        ? patient.getEndTime()
                        : startTime + queue.getAvgServiceDuration() * 60 * 1000;

                filteredList.add(mapToQueueDumpResponse(patient, startTime, endTime));
            }
        } else {
            filteredList = new ArrayList<>();
        }

        PatientQueueResponse queueResponse = new PatientQueueResponse();
        queueResponse.setWaiting(waitingCount);
        queueResponse.setProcess(processCount);
        queueResponse.setHold(holdCount);
        queueResponse.setCompleted(completedCount);
        queueResponse.setCancelled(cancelledCount);
        queueResponse.setWaitingTime(waitingTimeMin); // ⏱️ Add waiting time in minutes
        queueResponse.setList(filteredList);

        return queueResponse;
    }

    @Override
    public String startAppointment(StartAppointmentPayload startAppointmentPayload) {
        PatientQueue patientQueue = getPatientQueueOrThrow(startAppointmentPayload.getQueueId(), startAppointmentPayload.getTokenId());

        Optional<Queue> optionalQueue = queueRepository.findById(startAppointmentPayload.getQueueId());
        if (optionalQueue.isEmpty()) {
            throw new RuntimeException("Queue not found");
        }

        Queue queue = optionalQueue.get();

        long processingCount = patientQueueRepository.countByQueueIdAndStatus(startAppointmentPayload.getQueueId(), GlobalConstant.STATUS_PROCESSING);

        if (processingCount >= queue.getActiveCounter()) {
            return "You cannot serve more than " + queue.getActiveCounter() + " patient(s) at a time.";
        }

        long currentTime = System.currentTimeMillis();
        patientQueue.setStartTime(currentTime);
        patientQueue.setAnnouncement(1L);
        patientQueue.setExpServiceTime(startAppointmentPayload.getServiceTime());
        patientQueue.setStatus(GlobalConstant.STATUS_PROCESSING);

        patientQueueRepository.save(patientQueue);

        return "Appointment started at " + currentTime;
    }

    @Override
    public String cancelAppointment(Long queueId, Long tokenId) {
        PatientQueue patientQueue = getPatientQueueOrThrow(queueId, tokenId);

        patientQueue.setStatus(GlobalConstant.STATUS_CANCELED);
        long currentTimeMillis = System.currentTimeMillis();
        patientQueue.setEndTime(currentTimeMillis);

        if (patientQueue.getStartTime() != null) {
            long totalTime = currentTimeMillis - patientQueue.getStartTime();
            patientQueue.setTotalServiceTime(totalTime);
        } else {
            patientQueue.setTotalServiceTime(0L);
        }

        PatientQueueDump dump = new PatientQueueDump();
        BeanUtils.copyProperties(patientQueue, dump);
        dump.setId(null);
        patientQueueDumpRepository.save(dump);

        patientQueueRepository.delete(patientQueue);

        return "Appointment cancelled.";
    }

    @Override
    public String holdAppointment(Long queueId, Long tokenId) {
        PatientQueue patientQueue = getPatientQueueOrThrow(queueId, tokenId);

        patientQueue.setStatus(GlobalConstant.STATUS_ON_HOLD);
        patientQueue.setExpServiceTime(0L);
        patientQueueRepository.save(patientQueue);

        return "Appointment put on hold.";
    }

    @Override
    public String completeAppointment(Long queueId, Long tokenId) {
        PatientQueue patientQueue = getPatientQueueOrThrow(queueId, tokenId);

        patientQueue.setStatus(GlobalConstant.STATUS_COMPLETED);
        long currentTimeMillis = System.currentTimeMillis();
        patientQueue.setEndTime(currentTimeMillis);

        if (patientQueue.getStartTime() != null) {
            long totalTime = currentTimeMillis - patientQueue.getStartTime();
            patientQueue.setTotalServiceTime(totalTime);
        } else {
            patientQueue.setTotalServiceTime(0L);
        }

        PatientQueueDump dump = new PatientQueueDump();
        BeanUtils.copyProperties(patientQueue, dump, "id");

        patientQueueDumpRepository.save(dump);

        patientQueueRepository.delete(patientQueue);

        return "Appointment completed.";
    }

    @Override
    public String addTime(AddTimePayload addTimePayload) {
        PatientQueue patientQueue = getPatientQueueOrThrow(addTimePayload.getQueueId(), addTimePayload.getTokenId());

        Optional<Queue> optionalQueue = queueRepository.findById(addTimePayload.getQueueId());
        if (optionalQueue.isEmpty()) {
            throw new RuntimeException("Queue not found");
        }

        if (!patientQueue.getStatus().equals(GlobalConstant.STATUS_PROCESSING)){
            throw new RuntimeException("Appointment is not in process.");
        }

        patientQueue.setExpServiceTime(patientQueue.getExpServiceTime() + addTimePayload.getServiceTime());
        patientQueueRepository.save(patientQueue);

        return "Appointment service time extended.";
    }

    @Override
    public String tokenAnnouncement(Long queueId, Long tokenId) {
        PatientQueue patientQueue = getPatientQueueOrThrow(queueId, tokenId);

        Optional<Queue> optionalQueue = queueRepository.findById(queueId);
        if (optionalQueue.isEmpty()) {
            throw new RuntimeException("Queue not found");
        }

        if (!patientQueue.getStatus().equals(GlobalConstant.STATUS_PROCESSING)){
            throw new RuntimeException("Appointment is not in process.");
        }

        patientQueue.setAnnouncement(1L);
        patientQueueRepository.save(patientQueue);

        return "Appointment Added.";
    }

    @Override
    @Transactional
    public String clearQueue(Long queueId) {
        // 1. Fetch all patient queue records for the given queueId
        List<PatientQueue> patientQueueList = patientQueueRepository.findByQueueId(queueId);

        if (patientQueueList.isEmpty()) {
            return "No appointments found for the given queue.";
        }

        // 2. Convert each PatientQueue to PatientQueueDump
        long currentTimeMillis = System.currentTimeMillis();

        List<PatientQueueDump> dumpList = patientQueueList.stream().map(p -> {
            PatientQueueDump dump = new PatientQueueDump();
            dump.setToken(p.getToken());
            dump.setQueueId(p.getQueueId());
            dump.setFirstName(p.getFirstName());
            dump.setLastName(p.getLastName());
            dump.setMobileNumber(p.getMobileNumber());
            dump.setEmail(p.getEmail());
            dump.setNotes(p.getNotes());
            dump.setAnnouncement(0L);
            dump.setAppointmentBookingDate(p.getAppointmentBookingDate());
            dump.setAppointmentType(p.getAppointmentType());
            dump.setStatus(p.getStatus());
            dump.setOnlineBookingSlot(p.getOnlineBookingSlot());

            Long startTime = p.getStartTime() != null ? p.getStartTime() : currentTimeMillis;
            dump.setStartTime(startTime);

            Long endTime = p.getEndTime() != null ? p.getEndTime() : currentTimeMillis;
            dump.setEndTime(endTime);

            dump.setDelayTime(p.getDelayTime());

            dump.setExpServiceTime(p.getExpServiceTime());

            if (p.getTotalServiceTime() != null) {
                dump.setTotalServiceTime(p.getTotalServiceTime());
            } else {
                dump.setTotalServiceTime(endTime - startTime);
            }

            dump.setCreatedDate(p.getCreatedDate());
            dump.setUpdatedDate(p.getUpdatedDate());

            return dump;
        }).collect(Collectors.toList());

        // 3. Save all to dump table
        patientQueueDumpRepository.saveAll(dumpList);

        // 4. Delete all from original patient queue
        patientQueueRepository.deleteAll(patientQueueList);

        return "Patient Queue cleared successfully.";
    }

    private PatientQueue getPatientQueueOrThrow(Long queueId, Long tokenId) {
        if (queueId == null || tokenId == null) {
            throw new IllegalArgumentException("QueueId and TokenId must not be null");
        }

        return patientQueueRepository
                .findByIdAndQueueId(queueId, tokenId)
                .orElseThrow(() -> new RuntimeException("Patient not found with provided queueId and tokenId"));
    }

    public void generateTokenOnly(PatientQueue patientQueue, Queue queue) {
        // Generate next token
        Long newToken = queue.getLastToken() + 1;

        // Update queue
        queue.setLastToken(newToken);
        queueRepository.save(queue);

        // Update patientQueue
        patientQueue.setToken(newToken);
        patientQueueRepository.save(patientQueue);
    }

    private PatientDetailsResponse mapToQueueResponse(PatientQueue p, Long startTime, Long endTime) {
        PatientDetailsResponse response = new PatientDetailsResponse();
        response.setId(p.getId());
        response.setToken(p.getToken());
        response.setQueueId(p.getQueueId());
        response.setFirstName(p.getFirstName());
        response.setLastName(p.getLastName());
        response.setMobileNumber(p.getMobileNumber());
        response.setEmail(p.getEmail());
        response.setNotes(p.getNotes());
        response.setAnnouncement(p.getAnnouncement());
        response.setAppointmentBookingDate(p.getAppointmentBookingDate());
        response.setAppointmentType(p.getAppointmentType());
        response.setStatus(p.getStatus());
        response.setOnlineBookingSlot(p.getOnlineBookingSlot());

        // Set dynamic values
        response.setStartTime(startTime);
        response.setEndTime(endTime);

        response.setDelayTime(p.getDelayTime());
        response.setExpServiceTime(p.getExpServiceTime());
        response.setTotalServiceTime(p.getTotalServiceTime());

        response.setCreatedDate(p.getCreatedDate());
        response.setUpdatedDate(p.getUpdatedDate());

        return response;
    }

    private PatientDetailsResponse mapToQueueDumpResponse(PatientQueueDump p, Long startTime, Long endTime) {
        PatientDetailsResponse response = new PatientDetailsResponse();
        response.setId(p.getId());
        response.setToken(p.getToken());
        response.setQueueId(p.getQueueId());
        response.setFirstName(p.getFirstName());
        response.setLastName(p.getLastName());
        response.setMobileNumber(p.getMobileNumber());
        response.setEmail(p.getEmail());
        response.setNotes(p.getNotes());
        response.setAnnouncement(p.getAnnouncement());
        response.setAppointmentBookingDate(p.getAppointmentBookingDate());
        response.setAppointmentType(p.getAppointmentType());
        response.setStatus(p.getStatus());
        response.setOnlineBookingSlot(p.getOnlineBookingSlot());

        // Set dynamic values
        response.setStartTime(startTime);
        response.setEndTime(endTime);

        response.setDelayTime(p.getDelayTime());
        response.setExpServiceTime(p.getExpServiceTime());
        response.setTotalServiceTime(p.getTotalServiceTime());

        response.setCreatedDate(p.getCreatedDate());
        response.setUpdatedDate(p.getUpdatedDate());

        return response;
    }

}
