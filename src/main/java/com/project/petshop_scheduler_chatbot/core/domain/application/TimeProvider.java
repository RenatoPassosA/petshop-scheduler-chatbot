package com.project.petshop_scheduler_chatbot.core.domain.application;

import java.time.OffsetDateTime;

public interface TimeProvider {
    OffsetDateTime nowInUTC();      
}
