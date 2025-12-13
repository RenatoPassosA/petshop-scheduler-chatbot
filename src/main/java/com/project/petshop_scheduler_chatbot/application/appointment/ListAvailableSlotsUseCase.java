package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;

public class ListAvailableSlotsUseCase {

    private final BusinessHoursPolicy businessHoursPolicy;
    private final AppointmentRepository appointmentRepository;
    private final PetServiceUseCase petServiceUseCase;

    public ListAvailableSlotsUseCase (BusinessHoursPolicy businessHoursPolicy, PetServiceUseCase petServiceUseCase, AppointmentRepository appointmentRepository) {
        this.businessHoursPolicy = businessHoursPolicy;
        this.petServiceUseCase = petServiceUseCase;
        this.appointmentRepository = appointmentRepository;
    }

    public List<OffsetDateTime> listSlots(Long serviceId, Long professionalId) {
        Long limit = 10L;
        Long daysAhead = 7L;
        int step = 30;
        OffsetDateTime from = OffsetDateTime.now().plusDays(1).withHour(8).withMinute(0);
        OffsetDateTime to = from.plusDays(daysAhead);
        OffsetDateTime cursor = from;
        int serviceDuration = petServiceUseCase.getPetService(serviceId).getDuration();

        List<Appointment> appointmentsFromProfessional = appointmentRepository.listByProfessionalBetween(professionalId, from, to);
        List<OffsetDateTime> listSlots = new ArrayList<>();

        boolean invalidCandidate = false;
        while (cursor.isBefore(to) && limit != 0) {
            OffsetDateTime start = cursor;
            OffsetDateTime end = cursor.plusMinutes(Long.valueOf(serviceDuration));
            if (!businessHoursPolicy.fits(start, end)) {
                cursor = cursor.plusMinutes(step);
                continue;
            }
            for (Appointment appointment : appointmentsFromProfessional) {
                if (start.isBefore(appointment.getStartAt().plusMinutes(appointment.getServiceDuration())) &&
                    end.isAfter(appointment.getStartAt())) {
                        invalidCandidate = true;
                        break;
                    }  
            }
            if(invalidCandidate) {
                invalidCandidate = false;
                cursor = cursor.plusMinutes(step);
                continue;
            }

            listSlots.add(start);
            cursor = cursor.plusMinutes(step);
            limit--;
        }
        return listSlots;
    }
}
