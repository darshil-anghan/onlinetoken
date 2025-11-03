package com.example.mytoken.projection;

public interface PublicUserProjection extends UserProjection{
    String getEmail();
    String getType();
    String getOrganizationName();
    String getBio();
    String getContactCode();
    String getPhoneNumber();
    String getWhatsappNumber();
    String getLocation();
    String getFacebookLink();
    String getInstagramLink();
    String getWebsiteLink();
    String getTwitterLink();
    String getLinkedinLink();
    String getAddress1();
    String getAddress2();
    String getCity();
    String getState();
    String getCountry();
    String getPincode();
    Boolean getIsAdmin();
    Boolean getIsVerified();
    Boolean getIsActive();
}

