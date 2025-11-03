package com.example.mytoken.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_portal")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingPortal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "us", nullable = false)
    private String us;

    @Column(name = "disclaimer", nullable = false)
    private String disclaimer;

    @Column(name = "website_url", nullable = false)
    private String websiteUrl;

    @Column(name = "show_waitlist", nullable = false)
    private boolean showWaitlist = false;

    @Column(name = "portal_url", nullable = false, unique = true)
    private String portalUrl;

    @ManyToOne
    @JoinColumn(name = "queue_id", referencedColumnName = "id")
    private Queue queue;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
