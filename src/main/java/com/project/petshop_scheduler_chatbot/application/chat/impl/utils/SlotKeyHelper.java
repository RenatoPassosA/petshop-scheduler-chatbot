package com.project.petshop_scheduler_chatbot.application.chat.impl.utils;

import java.time.Instant;
import java.time.OffsetDateTime;

public final class SlotKeyHelper {

    private SlotKeyHelper() {}

    public static String toKey(OffsetDateTime startAt, Long professionalId) {
        long ms = startAt.toInstant().toEpochMilli();
        return ms + "-" + professionalId;
    }

    public static ParsedKey parse(String key) {
        if (key == null) return null;
        String[] parts = key.split("-");
        if (parts.length != 2) return null;
        try {
            long ms = Long.parseLong(parts[0]);
            long professionalId = Long.parseLong(parts[1]);
            return new ParsedKey(Instant.ofEpochMilli(ms), professionalId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public record ParsedKey(Instant instant, long professionalId) {}
}
