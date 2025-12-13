package com.project.petshop_scheduler_chatbot.application.chat.impl.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateTimeFormatterHelper {

    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE_PT_BR);

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", LOCALE_PT_BR);

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", LOCALE_PT_BR);

    private DateTimeFormatterHelper() {
    }

    public static String formatDate(OffsetDateTime dateTime) {
        return dateTime
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(DATE_FORMAT);
    }

    public static String formatTime(OffsetDateTime dateTime) {
        return dateTime
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(TIME_FORMAT);
    }

    public static String formatDateTime(OffsetDateTime dateTime) {
        return dateTime
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(DATE_TIME_FORMAT);
    }
}
