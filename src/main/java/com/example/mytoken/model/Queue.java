package com.example.mytoken.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "queue")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "prefix", nullable = false)
    private String prefix;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "no_of_customer", nullable = false)
    private int noOfCustomer;

    @Column(name = "avg_service_duration", nullable = false)
    private int avgServiceDuration;

    @Column(name = "service_center", nullable = false)
    private int serviceCenter;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "active_counter", nullable = false)
    private int activeCounter = 1;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "setting_service")
    private Long settingService;

    @Column(name = "queue_link")
    private String queueLink;

    @JsonIgnore
    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QueueSession> sessions = new ArrayList<>();

    @Column(name = "last_token")
    private Long lastToken = 0L;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "booking_portal_id")
    private Long bookingPortalId;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
