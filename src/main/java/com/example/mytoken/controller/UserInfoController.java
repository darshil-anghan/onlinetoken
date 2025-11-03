package com.example.mytoken.controller;

import com.example.mytoken.exception.InvalidRequestException;
import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.OtpVerificationPayload;
import com.example.mytoken.payload.ResetPasswordPayload;
import com.example.mytoken.payload.ResetPasswordWithOtpPayload;
import com.example.mytoken.payload.UserPayload;
import com.example.mytoken.projection.PublicUserProjection;
import com.example.mytoken.service.OtpTokenService;
import com.example.mytoken.service.UserInfoService;
import com.example.mytoken.util.GlobalConstant;
import jakarta.validation.Valid;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserInfoController extends BaseController {

    private final UserInfoService userService;
    private final ProjectionFactory projectionFactory;
    private final OtpTokenService otpTokenService;

    public UserInfoController(UserInfoService userService,
                              ProjectionFactory projectionFactory,
                              OtpTokenService otpTokenService) {
        this.userService = userService;
        this.projectionFactory = projectionFactory;
        this.otpTokenService = otpTokenService;
    }

    /**
     *  This function use for signUp
     */
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse> signup(
            @RequestPart("userPayload") @Valid UserPayload userPayload,
            BindingResult bindingResult) {

        try{
            if (bindingResult.hasErrors()) {
                throw new InvalidRequestException("Validation issues", bindingResult);
            }

            PublicUserProjection userProjection = projectionFactory.createProjection(
                    PublicUserProjection.class, userService.signup(userPayload));

            return ok(userProjection, GlobalConstant.OTP_SENT_TO_REGISTERED_EMAIL);
        } catch (Exception e){
            return ok(null, e.getMessage());
        }
    }

    /**
     * This function is used to verify OTP.
     */
    @PostMapping(value = "/otp_verification")
    public ResponseEntity<GlobalResponse> verifyOtp(@RequestBody OtpVerificationPayload request) {
        boolean isValid = otpTokenService.verifyOtp(request.getUserId(), request.getOtpId(), request.getOtp());

        if (!isValid) {
            throw new InvalidRequestException("Invalid or expired OTP");
        }

        userService.markUserVerified(request.getUserId());

        return ok("", GlobalConstant.OTP_VERIFICATION_DONE);
    }

    /**
     *  This function use for change password
     */
    @PostMapping(value = "/reset_password")
    public ResponseEntity<GlobalResponse> resetPassword(@RequestBody @Valid ResetPasswordPayload request) {
        userService.resetPassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());
        return ok("", GlobalConstant.PASSWORD_RESET_SUCCESS);
    }

    /**
     *  This function use for forgot password
     */
    @PostMapping(value = "/forgot_password")
    public ResponseEntity<GlobalResponse> forgotPassword(@RequestBody String email) {
        userService.forgotPassword(email);
        return ok("", GlobalConstant.OTP_SENT_TO_REGISTERED_EMAIL);
    }

    /**
     *  This function use for reset password
     */
    @PostMapping(value = "/reset_password_with_otp")
    public ResponseEntity<GlobalResponse> resetPasswordWithOtp(@RequestBody ResetPasswordWithOtpPayload request) {
        userService.resetPasswordWithOtp(request);
        return ok("", GlobalConstant.PASSWORD_RESET_SUCCESS);
    }
}
