package com.project.petshop_scheduler_chatbot.core.domain.policy;

import java.time.OffsetDateTime;

public interface BusinessHoursPolicy {
    boolean fits(OffsetDateTime start, OffsetDateTime end);
}
