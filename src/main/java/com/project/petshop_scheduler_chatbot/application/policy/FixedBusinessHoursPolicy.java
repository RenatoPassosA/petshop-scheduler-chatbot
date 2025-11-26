package com.project.petshop_scheduler_chatbot.application.policy;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;

@Component
public class FixedBusinessHoursPolicy implements BusinessHoursPolicy{
    
    @Override
    public boolean fits(OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null || !end.isAfter(start))
            return (false);
        if (!start.toLocalDate().equals(end.toLocalDate()))
            return (false);

        DayOfWeek day = start.getDayOfWeek();
        LocalTime initService = start.toLocalTime();
        LocalTime endService = end.toLocalTime();
        
        if (day == DayOfWeek.SUNDAY)
            return (false);

        LocalTime startWindow;
        LocalTime endWindow;

        if (day ==DayOfWeek.SATURDAY) {
            startWindow = LocalTime.of(8, 0);
            endWindow = LocalTime.of(14, 0);
        }
        else {
            startWindow = LocalTime.of(9, 0);
            endWindow = LocalTime.of(18, 0);
        }
        return (!initService.isBefore(startWindow) && !endService.isAfter(endWindow));
    }
}
