package com.project.petshop_scheduler_chatbot.infrastructure.time;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;

@Component
public class SystemTimeProvider implements TimeProvider{
    @Override
    public final OffsetDateTime nowInUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
