package com.example.mytoken.payload;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingPortalPayload {
    private String us;
    private String disclaimer;
    private String websiteUrl;
    private boolean showWaitlist;
    private String portalUrl;
    private Long queueId;
}
