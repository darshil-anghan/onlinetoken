package com.example.mytoken.service;

import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${mail}")
    private String fromEmail;

    @Value("${magic.link.base-url}")
    private String baseUrl;

    @Value("${magic.link.path}")
    private String path;

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends a formatted OTP email with a magic link to the user
     *
     * @param recipientEmail The recipient's email address
     * @param otp            The OTP to be sent
     * @param otpId          The ID of the OTP token
     * @param userId         The user's ID
     */
    @SneakyThrows
    public void sendOtpEmail(String recipientEmail, String otp, Long otpId, Long userId) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setFrom(fromEmail);
        messageHelper.setTo(recipientEmail);
        messageHelper.setSubject("OnlineToken | Verification link");

        // Construct the magic link
        String magicLink = String.format("%s%s?userId=%d&otp=%s&otpId=%d",
                baseUrl, path, userId, otp, otpId);

        String htmlContent = """
            <p><strong># Verification code</strong></p>
            <p>Please use the verification code below to sign in.</p>
            <p><strong style="font-size: 130%%">%s</strong></p>
            <p>Or simply click the link below to verify and login:</p>
            <p><a href="%s">%s</a></p>
            <p>If you didn’t request this, you can ignore this email.</p>
            <p>Thanks,<br/>The OnlineToken Team</p>
        """.formatted(otp, magicLink, magicLink);

        messageHelper.setText(htmlContent, true);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

    /**
     * Sends a password reset email with a reset token and userId.
     *
     * @param recipientEmail The recipient's email address
     * @param resetToken The password reset token
     * @param userId The user ID for the password reset
     */
    @SneakyThrows
    public void sendPasswordResetEmail(String recipientEmail, String resetToken, Long userId) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setFrom(fromEmail);
        messageHelper.setTo(recipientEmail);
        messageHelper.setSubject("OnlineToken | Password Reset Request");

        String htmlContent = """
            <p><strong># Password Reset Request</strong></p>
            <p>Please use the following token to reset your password.</p>
            <p><strong>Reset Token: %s</strong></p>
            <p><strong>User ID: %s</strong></p>
            <p>The token will expire in 5 minutes. Please make sure to reset your password within this time frame.</p>
            <p>If you didn’t request this, please ignore this email.</p>
            <p>Thanks,<br/>The OnlineToken Team</p>
        """.formatted(resetToken, userId);

        messageHelper.setText(htmlContent, true);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

    // Send appointent confirmation mail for otp
    @SneakyThrows
    public void sendAppointmentOtpEmail(String recipientEmail, String otp, Long otpId, Long patientId) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setFrom(fromEmail);
        messageHelper.setTo(recipientEmail);
        messageHelper.setSubject("OnlineToken | Appointment Verification");

        String magicLink = String.format("%s%s?patientId=%d&otp=%s&otpId=%d",
                baseUrl, path, patientId, otp, otpId);

        String htmlContent = """
        <p><strong>Appointment Verification</strong></p>
        <p>Dear patient,</p>
        <p>Please verify your appointment using the OTP below:</p>
        <p><strong style="font-size: 130%%">%s</strong></p>
        <p>Alternatively, click the link below to verify automatically:</p>
        <p><a href="%s">%s</a></p>
        <p>This OTP is valid for 5 minutes.</p>
        <p>If you didn’t request an appointment, please ignore this email.</p>
        <p>Thank you,<br/>The OnlineToken Team</p>
    """.formatted(otp, magicLink, magicLink);

        messageHelper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }

}