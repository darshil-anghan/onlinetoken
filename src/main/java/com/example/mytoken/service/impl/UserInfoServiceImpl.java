package com.example.mytoken.service.impl;

import com.example.mytoken.exception.InvalidRequestException;
import com.example.mytoken.model.Subscription;
import com.example.mytoken.model.UserInfo;
import com.example.mytoken.model.UserSubscription;
import com.example.mytoken.payload.QueuePayload;
import com.example.mytoken.payload.ResetPasswordWithOtpPayload;
import com.example.mytoken.payload.UserPayload;
import com.example.mytoken.repository.SubscriptionRepository;
import com.example.mytoken.repository.UserInfoRepository;
import com.example.mytoken.repository.UserSubscriptionRepository;
import com.example.mytoken.service.OtpTokenService;
import com.example.mytoken.service.QueueService;
import com.example.mytoken.service.UserInfoService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpTokenService otpTokenService;
    private final QueueService queueService;

    public UserInfoServiceImpl(
            UserSubscriptionRepository userSubscriptionRepository,
            SubscriptionRepository subscriptionRepository,
            UserInfoRepository userInfoRepository,
            PasswordEncoder passwordEncoder,
            OtpTokenService otpTokenService,
            QueueService queueService) {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpTokenService = otpTokenService;
        this.queueService = queueService;
    }

    @Override
    public UserInfo signup(UserPayload userPayload) {
        Optional<UserInfo> existingUser = userInfoRepository.findByEmail(userPayload.getEmail());

        if (existingUser.isPresent()) {
            log.error("User with email {} already exists", userPayload.getEmail());
            throw new InvalidRequestException("User with " + userPayload.getEmail() + " already exists");
        }

        if (!userPayload.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new InvalidRequestException("Password must contain at least one letter, one number, and be 8 characters long");
        }

        UserInfo user = new UserInfo();
        BeanUtils.copyProperties(userPayload, user, "password");
        user.setPassword(passwordEncoder.encode(userPayload.getPassword()));

        user = userInfoRepository.save(user);

        otpTokenService.createAndSendOtp(user.getId(), user.getEmail());

        if(user.isAdmin()){
            QueuePayload queuePayload = new QueuePayload();
            queuePayload.setName("General");
            queuePayload.setPrefix("G");
            queuePayload.setDescription("This queue is generated after sign in.");
            queuePayload.setNoOfCustomer(4);
            queuePayload.setAvgServiceDuration(15);
            queuePayload.setServiceCenter(1);
            queuePayload.setActive(true);

            queueService.saveQueue(user.getId(), queuePayload);
        }

        Subscription subscription = subscriptionRepository.findById(1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Default subscription not found"));

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUserId(user.getId());
        userSubscription.setSubscriptionId(1L);

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryTime = currentTime.plusDays(subscription.getDuration());

        userSubscription.setStartDate(currentTime);
        userSubscription.setEndDate(expiryTime);
        userSubscription.setActive(true);
        userSubscription = userSubscriptionRepository.save(userSubscription);

        user.setSubscriptionId(userSubscription.getId());
        userInfoRepository.save(user);

        return user;
    }

    @Override
    public void markUserVerified(Long userId) {
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setActive(true);
        userInfoRepository.save(user);
    }

    @Override
    public void resetPassword(String email, String oldPassword, String newPassword) {
        UserInfo user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User with email " + email + " not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidRequestException("Old password is incorrect");
        }

        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new InvalidRequestException("Password must contain at least one letter, one number, and be 8 characters long");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userInfoRepository.save(user);
    }

    @Override
    public void forgotPassword(String email) {
        UserInfo user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User with email " + email + " not found"));

        // Generate OTP for the user
        otpTokenService.createAndSendOtp(user.getId(), user.getEmail());
    }

    @Override
    public void resetPasswordWithOtp(ResetPasswordWithOtpPayload request) {
        UserInfo user = userInfoRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        boolean isOtpValid = otpTokenService.verifyOtp(request.getUserId(), request.getOtpId(), request.getOtp());
        if (!isOtpValid) {
            throw new InvalidRequestException("Invalid or expired OTP");
        }

        if (!request.getNewPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new InvalidRequestException("Password must contain at least one letter, one number, and be 8 characters long");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userInfoRepository.save(user);
    }

    @Override
    public UserInfo getById(Long id) {
        return userInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserInfo getUserInfoByEmail(String email) {
        // Fetch user info from the repository using the email
        return userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserInfo updateUserInfoByEmail(UserPayload userPayload) {
        // Fetch the user from the repository using email (assuming the userPayload has email)
        UserInfo existingUser = userInfoRepository.findByEmail(userPayload.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update all fields except email and password
        if (userPayload.getType() != null) existingUser.setType(userPayload.getType());
        if (userPayload.getOrganizationName() != null) existingUser.setOrganizationName(userPayload.getOrganizationName());
        if (userPayload.getBio() != null) existingUser.setBio(userPayload.getBio());
        if (userPayload.getContactCode() != null) existingUser.setContactCode(userPayload.getContactCode());
        if (userPayload.getPhoneNumber() != null) existingUser.setPhoneNumber(userPayload.getPhoneNumber());
        if (userPayload.getWhatsappNumber() != null) existingUser.setWhatsappNumber(userPayload.getWhatsappNumber());
        if (userPayload.getLocation() != null) existingUser.setLocation(userPayload.getLocation());
        if (userPayload.getFacebookLink() != null) existingUser.setFacebookLink(userPayload.getFacebookLink());
        if (userPayload.getInstagramLink() != null) existingUser.setInstagramLink(userPayload.getInstagramLink());
        if (userPayload.getWebsiteLink() != null) existingUser.setWebsiteLink(userPayload.getWebsiteLink());
        if (userPayload.getTwitterLink() != null) existingUser.setTwitterLink(userPayload.getTwitterLink());
        if (userPayload.getLinkedinLink() != null) existingUser.setLinkedinLink(userPayload.getLinkedinLink());
        if (userPayload.getAddress1() != null) existingUser.setAddress1(userPayload.getAddress1());
        if (userPayload.getAddress2() != null) existingUser.setAddress2(userPayload.getAddress2());
        if (userPayload.getCity() != null) existingUser.setCity(userPayload.getCity());
        if (userPayload.getState() != null) existingUser.setState(userPayload.getState());
        if (userPayload.getCountry() != null) existingUser.setCountry(userPayload.getCountry());
        if (userPayload.getPincode() != null) existingUser.setPincode(userPayload.getPincode());

        return userInfoRepository.save(existingUser);
    }

    @Override
    @Transactional
    public String addUser(Long adminId, UserPayload userPayload) {
        Optional<UserInfo> existingUser = userInfoRepository.findByEmail(userPayload.getEmail());

        if (existingUser.isPresent()) {
            log.error("User with email {} already exists", userPayload.getEmail());
            throw new InvalidRequestException("User with " + userPayload.getEmail() + " already exists");
        }

        if (!userPayload.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new InvalidRequestException("Password must contain at least one letter, one number, and be at least 8 characters long");
        }

        Optional<UserInfo> adminUserInfo = userInfoRepository.findById(adminId);
        if (adminUserInfo.isEmpty() || !adminUserInfo.get().isActive()) {
            throw new IllegalArgumentException("Admin not found or inactive");
        }

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userPayload, userInfo, "password");
        userInfo.setPassword(passwordEncoder.encode(userPayload.getPassword()));
        userInfo.setAdmin(false);
        userInfo.setAdminId(adminId);
        userInfo.setSubscriptionId(adminUserInfo.get().getSubscriptionId());

        userInfo = userInfoRepository.save(userInfo);

        otpTokenService.createAndSendOtp(userInfo.getId(), userInfo.getEmail());

        return "User created successfully with email: " + userInfo.getEmail();
    }
}
