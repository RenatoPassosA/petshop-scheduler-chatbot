package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        return generateSlotsButtons(conversationSession, false);
    }


    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_CHOOSE_SLOT(
        ConversationSession conversationSession,
        ProcessIncomingMessageCommand messageCommand
    ) {
        ProcessIncomingMessageResult error = checkError_STATE_RESCHEDULE_CHOOSE_SLOT(conversationSession, messageCommand);
        if (error != null) {
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);
            return error;
        }

        int index = Integer.parseInt(messageCommand.getButtonId());
        AvailableSlots chosenSlot = conversationSession.getSlots().get(index);

        conversationSession.setChosenSlot(chosenSlot);
        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);

        return ProcessIncomingMessageResult.interactive(
            new InteractiveMessage(
                "Podemos reagendar?\n\n" + generateConfirmationMessage(conversationSession, false),
                List.of(new ButtonOption("YES", "SIM"), new ButtonOption("NO", "NÃO"))
            )
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
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "⚠️ Esse horário ficou fora do expediente. Escolha outro horário.\n\n",
                generateSlotsButtons(conversationSession, false).getInteractive()
            );
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

        if (!appointmentRepository.existsOwnership(tutorId, appointmentId)) return true;

        return false;
    }


    private ProcessIncomingMessageResult checkError_STATE_RESCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null)
            return generateSlotsButtons(conversationSession, true);

        int index;
        try {
            index = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return generateSlotsButtons(conversationSession, true);
        }

        List<AvailableSlots> slots = conversationSession.getSlots();

        if (slots == null || slots.isEmpty()) {

            Long apptId = conversationSession.getChosenAppointmentId();
            if (apptId == null) {
                conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
                return ProcessIncomingMessageResult.interactiveWithMessage(
                    "⚠️ Perdi o contexto do reagendamento. Vamos começar de novo.\n\n",
                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
                );
            }

            Optional<Appointment> apptOpt = appointmentRepository.findById(apptId);
            if (apptOpt.isEmpty()) {
                conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
                return ProcessIncomingMessageResult.interactiveWithMessage(
                    "⚠️ Não encontrei esse agendamento. Vamos voltar ao menu.\n\n",
                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
                );
            }

            Long serviceId = apptOpt.get().getServiceId();

            slots = listAvailableSlotsUseCase.listSlots(serviceId, conversationSession.getLastInteraction());

            if (slots == null || slots.isEmpty()) {
                conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
                return ProcessIncomingMessageResult.interactiveWithMessage(
                    "Não encontrei horários disponíveis nos próximos dias.\n\n",
                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
                );
            }

            conversationSession.setSlots(slots);
        }

        if (index < 0 || index >= slots.size())
            return generateSlotsButtons(conversationSession, true);

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
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "Você não tem nenhum serviço agendado.\n\nO que deseja fazer?\n\n",
                MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
            );
        }

        List<ButtonOption> rows = new ArrayList<>();

        for (Appointment appt : appointments) {
            String id = appt.getId().toString();

            String petName = petRepository.findById(appt.getPetId()).map(Pet::getName).orElse("Pet");
            String serviceName = petServiceRepository.findById(appt.getServiceId()).map(PetService::getName).orElse("Serviço");
            String startAt = DateTimeFormatterHelper.formatDateTime(appt.getStartAt());
            String title = truncate(serviceName + " - " + petName + " (" + startAt + ")", 24);

            rows.add(new ButtonOption(id, title));
        }

        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);

        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";

        return ProcessIncomingMessageResult.interactiveWithMessage(
            prefix,
            InteractiveMessage.list(
                "Qual serviço deseja reagendar?\n",
                "Escolher serviço",
                "Agendamentos",
                rows
            )
        );
    }

    private ProcessIncomingMessageResult generateSlotsButtons(ConversationSession conversationSession, boolean withError) {
        Long serviceId = conversationSession.getChosenServiceId();

        if (serviceId == null) {
            Long apptId = conversationSession.getChosenAppointmentId();
            if (apptId == null) {
                conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
                return ProcessIncomingMessageResult.interactiveWithMessage(
                    "Não consegui identificar qual agendamento você quer reagendar.\n\n",
                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
                );
            }

            Appointment appt = appointmentRepository.findById(apptId).orElse(null);
            if (appt == null) {
                conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
                return ProcessIncomingMessageResult.interactiveWithMessage(
                    "Esse agendamento não existe mais.\n\n",
                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
                );
            }

            serviceId = appt.getServiceId();
            conversationSession.setChosenServiceId(serviceId);
            conversationSession.setPetId(appt.getPetId());
        }

        List<AvailableSlots> availableSlots =
            listAvailableSlotsUseCase.listSlots(serviceId, conversationSession.getLastInteraction());

        if (availableSlots == null || availableSlots.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "Não encontrei horários disponíveis nos próximos dias.\n\n",
                MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
            );
        }

        List<ButtonOption> rows = new ArrayList<>();
        for (int i = 0; i < availableSlots.size(); i++) {
            AvailableSlots slot = availableSlots.get(i);
            String title = truncate(
                DateTimeFormatterHelper.formatDateTime(slot.getStartAt()) + " - " + slot.getProfessionalName(),
                24
            );
            rows.add(new ButtonOption(String.valueOf(i), title));
        }

        conversationSession.setSlots(availableSlots);
        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);

        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";
        return ProcessIncomingMessageResult.interactiveWithMessage(
            prefix,
            InteractiveMessage.list(
                "Para qual horário deseja reagendar?\n",
                "Escolher horário",
                "Horários disponíveis",
                rows
            )
        );
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

