package com.example.mytoken.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPayload {

    @NotBlank(message = "Type is required")
    private String type;

    private String organizationName;
    private String bio;
    private String contactCode;
    private String phoneNumber;
    private String whatsappNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String location;
    private String facebookLink;
    private String instagramLink;
    private String websiteLink;
    private String twitterLink;
    private String linkedinLink;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String pincode;

    @JsonProperty("is_admin")
    private Boolean isAdmin = false;

    @JsonProperty("is_verified")
    private Boolean isVerified = false;

    @JsonProperty("is_active")
    private Boolean isActive = false;
}
