package com.example.mytoken.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user_info")
@EntityListeners(AuditingEntityListener.class)
public class UserInfo implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false) // e.g. Organization, ThirdParty
    private String type;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "contact_code")
    private String contactCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "location")
    private String location;

    @Column(name = "facebook_link")
    private String facebookLink;

    @Column(name = "instagram_link")
    private String instagramLink;

    @Column(name = "website_link")
    private String websiteLink;

    @Column(name = "twitter_link")
    private String twitterLink;

    @Column(name = "linkedin_link")
    private String linkedinLink;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> isAdmin ? "ROLE_ADMIN" : "ROLE_USER");
    }

    @Override
    public String getUsername() {
        return String.valueOf(email);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
