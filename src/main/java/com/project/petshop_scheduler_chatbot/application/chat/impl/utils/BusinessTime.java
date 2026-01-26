package com.project.petshop_scheduler_chatbot.application.chat.impl.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class BusinessTime {

    private BusinessTime() {}

    public static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Sao_Paulo");

    public static LocalDate toBusinessDate(OffsetDateTime dt) {
        return dt.atZoneSameInstant(BUSINESS_ZONE).toLocalDate();
    }
}
