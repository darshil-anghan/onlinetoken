package com.example.mytoken.service.impl;

import com.example.mytoken.model.BookingPortal;
import com.example.mytoken.model.Queue;
import com.example.mytoken.model.QueueServiceSetting;
import com.example.mytoken.model.UserInfo;
import com.example.mytoken.payload.QueuePayload;
import com.example.mytoken.repository.BookingPortalRepository;
import com.example.mytoken.repository.QueueRepository;
import com.example.mytoken.repository.QueueServiceSettingRepository;
import com.example.mytoken.repository.UserInfoRepository;
import com.example.mytoken.service.QueueService;
import com.example.mytoken.util.Utility;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueueServiceImpl implements QueueService {

    private final UserInfoRepository userInfoRepository;
    private final QueueRepository queueRepository;
    private final BookingPortalRepository bookingPortalRepository;
    private final QueueServiceSettingRepository queueServiceSettingRepository;

    @Autowired
    public QueueServiceImpl(
            UserInfoRepository userInfoRepository,
            QueueRepository queueRepository,
            BookingPortalRepository bookingPortalRepository,
            QueueServiceSettingRepository queueServiceSettingRepository
    ) {
        this.userInfoRepository = userInfoRepository;
        this.queueRepository = queueRepository;
        this.bookingPortalRepository = bookingPortalRepository;
        this.queueServiceSettingRepository = queueServiceSettingRepository;
    }

    @Override
    public void saveQueue(Long adminId, QueuePayload payload) {
        if(adminId == null || payload == null) {
            throw new IllegalArgumentException("Admin ID and Queue Payload must not be null");
        }

        Queue queue = Queue.builder()
                .name(payload.getName())
                .prefix(payload.getPrefix())
                .description(payload.getDescription())
                .noOfCustomer(payload.getNoOfCustomer())
                .avgServiceDuration(payload.getAvgServiceDuration())
                .serviceCenter(payload.getServiceCenter())
                .isActive(payload.isActive())
                .adminId(adminId)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        queue = queueRepository.save(queue); // Save to get ID

        // Auto-create QueueServiceSetting with default values
        QueueServiceSetting defaultSetting = QueueServiceSetting.builder()
                .onlineCheckIn(false)
                .onlineWaitTimeLow(0)
                .onlineWaitTimeHigh(0)
                .onlineOtpVerification(false)
                .onlineNotifyUser(false)
                .onlineCheckInMobile(false)
                .onlineCheckInEmail(false)

                .appointmentEnable(false)
                .appointmentMaxFutureDays(0)
                .appointmentNotifyUsers(false)
                .appointmentOtpVerification(false)
                .appointmentAptReminder(false)
                .appointmentSms(false)
                .appointmentMail(false)

                .advanceMarketplace(false)
                .advanceDynamicCapacity(false)
                .advanceBuildCustomerData(false)
                .advanceBusinessNotification(false)
                .advanceBreakHour(false)
                .build();

        BookingPortal portal = BookingPortal.builder()
                .us("")
                .disclaimer("")
                .websiteUrl("")
                .showWaitlist(false)
                .portalUrl("")
                .queue(queue)
                .build();

        portal = bookingPortalRepository.save(portal);

        defaultSetting = queueServiceSettingRepository.save(defaultSetting);

        // Link the setting to the queue
        queue.setSettingService(defaultSetting.getId());
        queue.setUpdatedDate(LocalDateTime.now());
        queue.setBookingPortalId(portal.getId());
        queueRepository.save(queue);

    }

    @Override
    public void updateQueue(Long adminId, QueuePayload payload) {
        if (payload.getId() == null) {
            throw new IllegalArgumentException("Queue Id not found");
        }

        Optional<Queue> existingQueue = queueRepository.findById(payload.getId());

        if (existingQueue.isEmpty()) {
            throw new IllegalArgumentException("Queue not found for the given Id");
        }

        Queue queue = existingQueue.get();
        queue.setName(payload.getName());
        queue.setPrefix(payload.getPrefix());
        queue.setDescription(payload.getDescription());
        queue.setNoOfCustomer(payload.getNoOfCustomer());
        queue.setAvgServiceDuration(payload.getAvgServiceDuration());
        queue.setServiceCenter(payload.getServiceCenter());
        queue.setActive(payload.isActive());
        queue.setAdminId(adminId);
        queue.setUpdatedDate(LocalDateTime.now());

        queueRepository.save(queue);
    }

    @Override
    public void assignQueueToUser(Long userId, Long queueId, Long adminId) {
        Optional<Queue> queueOptional = queueRepository.findById(queueId);

        if (queueOptional.isPresent()) {
            Queue queue = queueOptional.get();

            if (!queue.getAdminId().equals(adminId)) {
                throw new IllegalArgumentException("Only the admin who created the queue can assign it.");
            }

            queue.setUserId(userId);
            queue.setUpdatedDate(LocalDateTime.now());
            queueRepository.save(queue);
        } else {
            throw new IllegalArgumentException("Queue not found.");
        }
    }

    @Override
    public List<Queue> getAdminAllQueue(Long adminId) {
        if (adminId == null){
            throw new IllegalArgumentException("User not found...");
        }

        return queueRepository.findByAdminId(adminId);
    }

    @Override
    public List<Queue> getUserAllQueue(Long userId) {
        if (userId == null){
            throw new IllegalArgumentException("User not found...");
        }

        return queueRepository.findByUserId(userId);
    }

    @Override
    public void updateQueueActiveStatus(Long queueId, boolean isActive) {
        Optional<Queue> optionalQueue = queueRepository.findById(queueId);
        if (optionalQueue.isEmpty()) {
            throw new IllegalArgumentException("Queue not found.");
        }

        Queue queue = optionalQueue.get();
        queue.setActive(isActive);
        queue.setUpdatedDate(LocalDateTime.now());
        queueRepository.save(queue);
    }

    @Override
    public void updateQueueActiveCounter(Long queueId, int noOfCounter) {
        Optional<Queue> optionalQueue = queueRepository.findById(queueId);
        if (optionalQueue.isEmpty()) {
            throw new IllegalArgumentException("Queue not found.");
        }

        Queue queue = optionalQueue.get();
        queue.setActiveCounter(noOfCounter);
        queue.setUpdatedDate(LocalDateTime.now());
        queueRepository.save(queue);
    }

    @Override
    public String generateToken(Long queueId) {
        Optional<Queue> optionalQueue = queueRepository.findById(queueId);
        if (optionalQueue.isEmpty()) {
            throw new IllegalArgumentException("Queue not found.");
        }

        if (optionalQueue.get().getQueueLink() == null || optionalQueue.get().getQueueLink().isEmpty()) {
            optionalQueue.get().setQueueLink(Utility.generateQueueLink());

            // Check given token not available in the database
            while (queueRepository.existsByQueueLink(optionalQueue.get().getQueueLink())) {
                optionalQueue.get().setQueueLink(Utility.generateQueueLink());
            }

            try {
                queueRepository.save(optionalQueue.get());
            } catch (Exception e) {
                throw new RuntimeException("Failed to save the queue link: " + e.getMessage());
            }
        }

        return optionalQueue.get().getQueueLink();
    }

    @Override
    public Queue getQueueByToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty.");
        }

        Queue optionalQueue = queueRepository.findByQueueLink(token);
        if (optionalQueue == null) {
            throw new IllegalArgumentException("Queue not found for the given token.");
        }

        return optionalQueue;
    }
}
