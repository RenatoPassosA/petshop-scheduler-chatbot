package com.project.petshop_scheduler_chatbot.application.professional;

import java.time.OffsetDateTime;

public class TimeOffListResult {
    private Long id;
    private String reason;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;

    public TimeOffListResult(Long id, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        this.id = id;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(OffsetDateTime endAt) {
        this.endAt = endAt;
    }
}
