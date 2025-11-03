package com.example.mytoken.controller;

import com.example.mytoken.model.UserInfo;
import com.example.mytoken.payload.LoginPayload;
import com.example.mytoken.model.AuthToken;
import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.UserPayload;
import com.example.mytoken.projection.PublicUserProjection;
import com.example.mytoken.projection.UserProjection;
import com.example.mytoken.service.AuthService;
import com.example.mytoken.service.JwtService;
import com.example.mytoken.service.UserInfoService;
import com.example.mytoken.util.GlobalConstant;
import com.example.mytoken.util.Utility;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private final AuthService authService;
    private final UserInfoService userInfoService;
    private final ProjectionFactory projectionFactory;
    private final JwtService jwtService;

    public AuthController(AuthService authService, UserInfoService userInfoService, JwtService jwtService, ProjectionFactory projectionFactory) {
        this.authService = authService;
        this.userInfoService = userInfoService;
        this.jwtService = jwtService;
        this.projectionFactory =projectionFactory;
    }

    /**
     *  This function use for SignIn or Generate Auth token
     */
    @PostMapping("/generate-token")
    public ResponseEntity<GlobalResponse> authenticate(@RequestBody LoginPayload loginPayload) {
        AuthToken authToken = authService.authenticate(loginPayload);
        return ok(authToken, GlobalConstant.TOKEN_GENERATED);
    }

    /**
     *  This function use for fetch user profile information
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/user")
    public ResponseEntity<GlobalResponse> fetchUserInfo(@RequestHeader("Authorization") String token) {
        String email = Utility.getEmailFromToken(token, jwtService);

        UserInfo userInfo = userInfoService.getUserInfoByEmail(email);
        PublicUserProjection userProjection = projectionFactory.createProjection(PublicUserProjection.class, userInfo);

        return ok(userProjection, GlobalConstant.MSG_USER_DATA_FETCH_SUCCESS);
    }

    /**
     * This function use for update user profile information
     */
    @PutMapping(value = "/user")
    public ResponseEntity<GlobalResponse> setUserInfo(@RequestBody UserPayload userPayload) {
        try{
            UserInfo userInfo = userInfoService.updateUserInfoByEmail(userPayload);
            PublicUserProjection userProjection = projectionFactory.createProjection(PublicUserProjection.class, userInfo);

            return ok(userProjection, GlobalConstant.MSG_USER_DATA_UPDATE_SUCCESS);
        } catch (Exception e){
            return ok(null, "Something went wrong");
        }
    }

    /**
     * This function use for create user for an organization
     */
    @PostMapping(value = "create_user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse> createUser(@RequestHeader("Authorization") String token,
                                                     @RequestBody UserPayload userPayload) {
        try{
            String email = Utility.getEmailFromToken(token, jwtService);
            UserInfo userInfo = userInfoService.getUserInfoByEmail(email);

            String userProjection = userInfoService.addUser(userInfo.getId(), userPayload);

            return ok(userProjection, GlobalConstant.MSG_USER_DATA_UPDATE_SUCCESS);
        } catch (Exception e){
            return ok(null, "Something went wrong");
        }
    }
}
