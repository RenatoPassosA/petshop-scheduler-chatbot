package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.LinkedHashSet;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.appointment.AvailableSlots;
import com.project.petshop_scheduler_chatbot.application.appointment.ListAvailableSlotsUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.DateTimeFormatterHelper;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.SlotKeyHelper;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.BusinessTime;

@Component
public class RescheduleHandler {

    private final StartMenuHandler startMenuHandler;
    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final PetServiceRepository petServiceRepository;
    private final ListAvailableSlotsUseCase listAvailableSlotsUseCase;

    private final RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    public RescheduleHandler(AppointmentRepository appointmentRepository, StartMenuHandler startMenuHandler, PetRepository petRepository, PetServiceRepository petServiceRepository, ListAvailableSlotsUseCase listAvailableSlotsUseCase, RescheduleAppointmentUseCase rescheduleAppointmentUseCase) {
        this.appointmentRepository = appointmentRepository;
        this.startMenuHandler = startMenuHandler;
        this.petRepository = petRepository;
        this.petServiceRepository = petServiceRepository;
        this.listAvailableSlotsUseCase = listAvailableSlotsUseCase;
        this.rescheduleAppointmentUseCase = rescheduleAppointmentUseCase;
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_RESCHEDULE_START(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return startMenuHandler.STATE_START_handler(conversationSession);
        }
        return generateAppointmentButtons(conversationSession, false);
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(conversationSession, messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);
            return generateAppointmentButtons(conversationSession, true);
        }

        Long apptId = Long.valueOf(messageCommand.getButtonId());
        conversationSession.setChosenAppointmentId(apptId);

        Appointment appt = appointmentRepository.findById(apptId).orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + apptId));
        conversationSession.setChosenServiceId(appt.getServiceId());
        conversationSession.setPetId(appt.getPetId());
        conversationSession.setTutorId(appt.getTutorId());

        return generateDaysButtons(conversationSession, false); // ✅ antes era generateSlotsButtons
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_CHOOSE_DAY(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String dayId = messageCommand.getButtonId();
        if (dayId == null || dayId.isBlank()) {
            return generateDaysButtons(conversationSession, true);
        }

        LocalDate chosenDay;
        try {
            chosenDay = LocalDate.parse(dayId);
        } catch (Exception e) {
            return generateDaysButtons(conversationSession, true);
        }
        return generateSlotsButtonsForDay(conversationSession, chosenDay, false);
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        ProcessIncomingMessageResult error = checkError_STATE_RESCHEDULE_CHOOSE_SLOT(conversationSession, messageCommand);
        if (error != null)
            return error;

        String slotKey = messageCommand.getButtonId();
        var parsed = SlotKeyHelper.parse(slotKey);
        if (parsed == null) {
            return generateDaysButtons(conversationSession, true);
        }

        Long serviceId = conversationSession.getChosenServiceId();
        if (serviceId == null) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "⚠️ Perdi o contexto do reagendamento. Voltando ao menu.\n\n",
                MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
            );
        }

        List<AvailableSlots> allSlots = listAvailableSlotsUseCase.listSlots(serviceId, conversationSession.getLastInteraction());
        AvailableSlots chosen = null;
        for (AvailableSlots s : allSlots) {
            String k = SlotKeyHelper.toKey(s.getStartAt(), s.getProfessionalId());
            if (k.equals(slotKey)) {
                chosen = s;
                break;
            }
        }

        if (chosen == null)
            return generateDaysButtons(conversationSession, true);

        conversationSession.setChosenSlot(chosen);
        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Podemos reagendar?\n\n" + generateConfirmationMessage(conversationSession, false),
                                                                                List.of(new ButtonOption("YES", "SIM"), new ButtonOption("NO", "NÃO")))
        );
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_CONFIRM(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_RESCHEDULE_CONFIRM(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage(generateConfirmationMessage(conversationSession, true),
                                                                                                            List.of(new ButtonOption("YES", "SIM"),
                                                                                                                    new ButtonOption("NO", "NÃO")))); 
        }
        
        if ("NO".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(conversationSession.getChosenAppointmentId(),
                                                                                conversationSession.getChosenSlot().getStartAt());
        try {
            rescheduleAppointmentUseCase.execute(command);
        } catch (WorkingHoursOutsideException e) {
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_DAY);
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Esse horário ficou fora do expediente. Escolha outro dia/horário.\n\n",
                                                                    generateDaysButtons(conversationSession, false).getInteractive());
        }
        conversationSession.resetFlowData();
        conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
        return ProcessIncomingMessageResult.interactiveWithMessage("Agradecemos a preferencia!\nEstamos aguardando o seu pet!\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
    }


    private boolean checkError_STATE_RESCHEDULE_START(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || !"RESCHEDULE".equals(id))
            return true;
        return false;
    }

    private boolean checkError_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null) return true;

        Long appointmentId;
        try {
            appointmentId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return true;
        }

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty())
            return true;

        Long tutorId = conversationSession.getTutorId();
        if (tutorId == null)
            return true;

        if (!appointmentRepository.existsOwnership(tutorId, appointmentId))
            return true;

        return false;
    }


    private ProcessIncomingMessageResult checkError_STATE_RESCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || id.isBlank()) {
            return generateDaysButtons(conversationSession, true);
        }
        return null;
    }

    private boolean checkError_STATE_RESCHEDULE_CONFIRM(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"YES".equals(id) &&!"NO".equals(id)))  
            return true;
        return false;
    }

    private ProcessIncomingMessageResult generateAppointmentButtons(ConversationSession conversationSession, boolean withError) {
        List<Appointment> appointments = appointmentRepository.findByTutorId(conversationSession.getTutorId());
        if (appointments.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return ProcessIncomingMessageResult.interactiveWithMessage("Você não tem nenhum serviço agendado.\n\nO que deseja fazer?\n\n",
                                                                        MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        List<ButtonOption> rows = new ArrayList<>();

        for (Appointment appt : appointments) {
            String id = appt.getId().toString();
            String serviceName = petServiceRepository.findById(appt.getServiceId()).map(PetService::getName).orElse("Serviço");
            String startAt = DateTimeFormatterHelper.formatDateTime(appt.getStartAt());
            String title = truncate(startAt + " " + serviceName, 24);
            rows.add(new ButtonOption(id, title));
        }

        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);

        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";

        return ProcessIncomingMessageResult.interactiveWithMessage(prefix, InteractiveMessage.list("Qual serviço deseja reagendar?\n",
                                                                                                "Escolher serviço",
                                                                                                "Agendamentos",
                                                                                                rows));
    }

    private ProcessIncomingMessageResult generateDaysButtons(ConversationSession conversationSession, boolean withError) {
        Long serviceId = conversationSession.getChosenServiceId();
        if (serviceId == null) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não consegui identificar o serviço. Voltei ao menu.\n\n",
                                                                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        List<AvailableSlots> allSlots = listAvailableSlotsUseCase.listSlots(serviceId, conversationSession.getLastInteraction());

        if (allSlots == null || allSlots.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Não encontrei horários disponíveis nos próximos dias.\n\n",
                                                                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        LinkedHashSet<LocalDate> days = new LinkedHashSet<>();
        for (AvailableSlots s : allSlots) {
            days.add(BusinessTime.toBusinessDate(s.getStartAt()));
        }

        List<ButtonOption> rows = new ArrayList<>();
        for (LocalDate d : days) {
            String id = d.toString();
            String title = truncate(formatDayTitle(d), 24);
            rows.add(new ButtonOption(id, title));
            if (rows.size() >= 10)
                break;
        }

        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_DAY);

        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";
        return ProcessIncomingMessageResult.interactiveWithMessage(prefix, InteractiveMessage.list("Escolha o dia:\n",
                                                                                                "Escolher dia",
                                                                                                "Dias disponíveis",
                                                                                                rows));
    }

    private String formatDayTitle(LocalDate d) {
        TextStyle style = TextStyle.SHORT;
        Locale ptBR = new Locale("pt", "BR");
        String dow = d.getDayOfWeek().getDisplayName(style, ptBR); // "sáb."
        dow = dow.replace(".", ""); // "sáb"
        String ddmm = String.format("%02d/%02d", d.getDayOfMonth(), d.getMonthValue());
        return capitalize(dow) + " (" + ddmm + ")";
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private ProcessIncomingMessageResult generateSlotsButtonsForDay(ConversationSession conversationSession, LocalDate chosenDay, boolean withError) {
        Long serviceId = conversationSession.getChosenServiceId();
        if (serviceId == null) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "⚠️ Perdi o contexto do serviço. Voltando ao menu.\n\n",
                MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
            );
        }

        List<AvailableSlots> allSlots = listAvailableSlotsUseCase.listSlots(serviceId, conversationSession.getLastInteraction());
        if (allSlots == null || allSlots.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "Não encontrei horários disponíveis nos próximos dias.\n\n",
                MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
            );
        }

        List<AvailableSlots> daySlots = new ArrayList<>();
        for (AvailableSlots s : allSlots) {
            LocalDate d = BusinessTime.toBusinessDate(s.getStartAt());
            if (d.equals(chosenDay)) daySlots.add(s);
        }

        if (daySlots.isEmpty()) {
            return generateDaysButtons(conversationSession, true);
        }

        List<ButtonOption> rows = new ArrayList<>();
        for (AvailableSlots slot : daySlots) {
            String id = SlotKeyHelper.toKey(slot.getStartAt(), slot.getProfessionalId());
            String hhmm = slot.getStartAt().atZoneSameInstant(BusinessTime.BUSINESS_ZONE).toLocalTime().toString().substring(0, 5);
            String title = truncate(hhmm + " - " + slot.getProfessionalName(), 24);
            rows.add(new ButtonOption(id, title));
            if (rows.size() >= 10) break;
        }

        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);

        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";
        return ProcessIncomingMessageResult.interactiveWithMessage(prefix, InteractiveMessage.list("Agora escolha o horário:\n",
                                                                                                "Escolher horário",
                                                                                                "Horários",
                                                                                                rows));
    }

    private String generateConfirmationMessage(ConversationSession conversationSession, boolean withError) {
        AvailableSlots slot = conversationSession.getChosenSlot();

        Long petId = conversationSession.getPetId();
        Long serviceId = conversationSession.getChosenServiceId();

        if (petId == null || serviceId == null) {
            Long apptId = conversationSession.getChosenAppointmentId();
            if (apptId != null) {
                appointmentRepository.findById(apptId).ifPresent(appt -> {
                    if (conversationSession.getPetId() == null) {
                        conversationSession.setPetId(appt.getPetId());
                    }
                    if (conversationSession.getChosenServiceId() == null) {
                        conversationSession.setChosenServiceId(appt.getServiceId());
                    }
                });
                petId = conversationSession.getPetId();
                serviceId = conversationSession.getChosenServiceId();
            }
        }

        String petName = (petId != null)
            ? petRepository.findById(petId).map(Pet::getName).orElse("Pet não encontrado")
            : "Pet não encontrado";

        String serviceName = (serviceId != null)
            ? petServiceRepository.findById(serviceId).map(PetService::getName).orElse("Serviço não encontrado")
            : "Serviço não encontrado";

        String professionalName = (slot != null && slot.getProfessionalName() != null && !slot.getProfessionalName().isBlank())
            ? slot.getProfessionalName()
            : "Profissional selecionado automaticamente";

        String header = withError ? "⚠️ Não entendi, selecione SIM ou NÃO.\n" : "";

        String when = (slot != null)
            ? DateTimeFormatterHelper.formatDateTime(slot.getStartAt())
            : "Horário não encontrado";

        return header +
            "Dia: " + when +
            "\nPet: " + petName +
            "\nServiço: " + serviceName +
            "\nProfissional: " + professionalName;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        String t = s.trim();
        if (t.length() <= max) return t;
        return t.substring(0, Math.max(0, max - 1)) + "…";
    }

}

