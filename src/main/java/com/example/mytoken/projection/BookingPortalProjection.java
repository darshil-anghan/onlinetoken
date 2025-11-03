package com.example.mytoken.projection;

public interface BookingPortalProjection {
    String getPortalUrl();
    Long getQueueId();

    String getUs();

    String getDisclaimer();

    String getWebsiteUrl();

    boolean isShowWaitlist();
}
