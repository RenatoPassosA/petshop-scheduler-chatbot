package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;

@Component
public class ListAvailableSlotsUseCase {

    private final BusinessHoursPolicy businessHoursPolicy;
    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final PetServiceUseCase petServiceUseCase;

    public ListAvailableSlotsUseCase (BusinessHoursPolicy businessHoursPolicy, PetServiceUseCase petServiceUseCase, AppointmentRepository appointmentRepository, ProfessionalRepository professionalRepository) {
        this.businessHoursPolicy = businessHoursPolicy;
        this.petServiceUseCase = petServiceUseCase;
        this.appointmentRepository = appointmentRepository;
        this.professionalRepository = professionalRepository;
    }

    public List<AvailableSlots> listSlots(Long serviceId, OffsetDateTime now) {
        long daysAhead = 7L;
        int step = 30;

        OffsetDateTime from = now.plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime to = from.plusDays(daysAhead);

        PetService service = petServiceUseCase.getPetService(serviceId);
        int serviceDuration = service.getDuration();

        List<Professional> professionals = professionalRepository.getAll();
        List<AvailableSlots> listSlots = new ArrayList<>();

        for (Professional professionalI : professionals) {
            if (!service.getCanDo().equals(professionalI.getFunction())) {
                continue;
            }

            long limit = 10L;
            OffsetDateTime cursor = from;

            List<Appointment> appointmentsFromProfessional =
                appointmentRepository.listByProfessionalBetween(professionalI.getId(), from, to);

            while (cursor.isBefore(to) && limit != 0) {
                OffsetDateTime start = cursor;
                OffsetDateTime end = cursor.plusMinutes(serviceDuration);
                boolean invalidCandidate = false;

                if (!businessHoursPolicy.fits(start, end)) {
                    invalidCandidate = true;
                } else {
                    for (Appointment appointment : appointmentsFromProfessional) {
                        OffsetDateTime apptEnd = appointment.getStartAt().plusMinutes(appointment.getServiceDuration());
                        if (start.isBefore(apptEnd) && end.isAfter(appointment.getStartAt())) {
                            invalidCandidate = true;
                            break;
                        }
                    }
                }
                if (!invalidCandidate) {
                    listSlots.add(new AvailableSlots(start, professionalI.getId(), professionalI.getName()));
                    limit--;
                }
                cursor = cursor.plusMinutes(step);
            }
        }

        return filterByStartAt(listSlots);
    }


    private List<AvailableSlots> filterByStartAt(List<AvailableSlots> listSlots) {
        if (listSlots.isEmpty())
            return listSlots;

        listSlots.sort(Comparator.comparing(AvailableSlots::getStartAt));

        return listSlots.stream().limit(10).toList();
    }
}


