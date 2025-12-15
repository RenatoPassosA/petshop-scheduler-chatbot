package com.project.petshop_scheduler_chatbot.application.chat.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

public class RescheduleHandler {

    private final StartMenuHandler startMenuHandler;
    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final PetServiceRepository petServiceRepository;
    private final TutorRepository tutorRepository;
    private final ListAvailableSlotsUseCase listAvailableSlotsUseCase;

    private final RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    public RescheduleHandler(AppointmentRepository appointmentRepository, StartMenuHandler startMenuHandler, PetRepository petRepository, PetServiceRepository petServiceRepository, ListAvailableSlotsUseCase listAvailableSlotsUseCase, RescheduleAppointmentUseCase rescheduleAppointmentUseCase, TutorRepository tutorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.startMenuHandler = startMenuHandler;
        this.petRepository = petRepository;
        this.petServiceRepository = petServiceRepository;
        this.listAvailableSlotsUseCase = listAvailableSlotsUseCase;
        this.rescheduleAppointmentUseCase = rescheduleAppointmentUseCase;
        this.tutorRepository = tutorRepository;
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_RESCHEDULE_START(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return startMenuHandler.STATE_START_handler(conversationSession);
        }
        return generateAppointmentButtons(conversationSession, false);
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);
            return generateAppointmentButtons(conversationSession, true);
        }
        conversationSession.setChosenAppointmentId(Long.valueOf(messageCommand.getButtonId()));
        conversationSession.setChosenAppointment(appointmentRepository.findById(Long.valueOf(conversationSession.getChosenAppointmentId())).get());
        return generateSlotsButtons(conversationSession, false);
    }

    public ProcessIncomingMessageResult handle_STATE_RESCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_RESCHEDULE_CHOOSE_SLOT(conversationSession, messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);
            return generateSlotsButtons(conversationSession, true);
        }
        int index = Integer.parseInt(messageCommand.getButtonId());
        AvailableSlots chosenSlot = conversationSession.getSlots().get(index);
        conversationSession.setChosenSlot(chosenSlot);
        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Podemos confirmar o reagendamento?\n\n" + generateConfirmationMessage(conversationSession, false),
                                                        List.of(new ButtonOption("YES", "SIM"),
                                                            new ButtonOption("NO", "NÃO"))));
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
        rescheduleAppointmentUseCase.execute(command);
        conversationSession.setCurrentState(ConversationState.STATE_START);
        return ProcessIncomingMessageResult.text("Agradecemos a preferencia!\nEstamos aguardando o seu pet!"); 
    }


    private boolean checkError_STATE_RESCHEDULE_START(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || !"RESCHEDULE".equals(id))
            return true;
        return false;
    }

    private boolean checkError_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null)
            return true;

        Long appointmentId;
        try {
            appointmentId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return true;
        }

        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty())
            return true;

        PhoneNumber phone = new PhoneNumber(messageCommand.getPhoneNumberId());
        Optional<Tutor> tutor = tutorRepository.findByPhone(phone);
        if (tutor.isEmpty())
            return true;

        if (!appointmentRepository.existsOwnership(tutor.get().getId(), appointment.get().getId()))
            return true;

        return false;
    }

    private boolean checkError_STATE_RESCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null)
            return true;

        int index;
        try {
            index = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return true;
        }

        List<AvailableSlots> slotsList = conversationSession.getSlots();
        if (slotsList == null || slotsList.isEmpty())
            return true;

        return index < 0 || index >= slotsList.size();
    }

    private boolean checkError_STATE_RESCHEDULE_CONFIRM(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"YES".equals(id) &&!"NO".equals(id)))  
            return true;
        return false;
    }

    private ProcessIncomingMessageResult generateAppointmentButtons(ConversationSession conversationSession, boolean withError) {
        List <Appointment> appointments = appointmentRepository.findByTutorId(conversationSession.getTutorId());
        if (appointments.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return ProcessIncomingMessageResult.interactiveWithMessage("Você não tem nenhum serviço agendado.\n\n O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        List<ButtonOption> appointmentButtons = new ArrayList<>();
        for (Appointment appointmentList : appointments) {
            String appointmentIdString = appointmentList.getId().toString();
            String petName = petRepository.findById(appointmentList.getPetId()).get().getName();
            String serviceName = petServiceRepository.findById(appointmentList.getServiceId()).get().getName();
            String startAt = DateTimeFormatterHelper.formatDateTime(appointmentList.getStartAt());
            String message = serviceName + " para: " + petName + " dia " + startAt;
            appointmentButtons.add(new ButtonOption(appointmentIdString, message));
        }
        if (withError)
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\n\n", new InteractiveMessage( "Qual serviço deseja reagendar?\n", appointmentButtons));
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual serviço deseja reagendar?\n", appointmentButtons));
    }

    private ProcessIncomingMessageResult generateSlotsButtons(ConversationSession conversationSession, boolean withError) {
        List<AvailableSlots> availableSlots = listAvailableSlotsUseCase.listSlots(conversationSession.getChosenAppointment().getServiceId());
        List<ButtonOption> slotButtons = new ArrayList<>();

        for (int i = 0; i < availableSlots.size(); i++) {
            AvailableSlots slot = availableSlots.get(i);

            String slotText =
                "Dia: " + DateTimeFormatterHelper.formatDateTime(slot.getStartAt()) +
                " Profissional: " + slot.getProfessionalName();
            slotButtons.add(new ButtonOption(String.valueOf(i), slotText));
        }

        conversationSession.setSlots(availableSlots);
        conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);
        if (withError)
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\n\n", new InteractiveMessage( "Para qual horário deseja reagendar?\n", slotButtons));
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Para qual horário deseja reagendar?\n", slotButtons));
    }

    private String  generateConfirmationMessage(ConversationSession conversationSession, boolean withError) {
        AvailableSlots slot = conversationSession.getChosenSlot();
        String petName = petRepository.findById(conversationSession.getChosenAppointment().getPetId()).get().getName();
        String serviceName = petServiceRepository.findById(conversationSession.getChosenAppointment().getServiceId()).get().getName();
        String message;
        if (withError)
            message = "⚠️ Não entendi, selecione SIM ou NÃO.\n" +
                      "Dia: " + DateTimeFormatterHelper.formatDateTime(slot.getStartAt()) + 
                      "\nPet: " + petName +
                      "\nServiço: " + serviceName +
                      "\nProfissional: " + slot.getProfessionalName();
        else
            message = "Dia: " + DateTimeFormatterHelper.formatDateTime(slot.getStartAt()) + 
                      "\nPet: " + petName +
                      "\nServiço: " + serviceName +
                      "\nProfissional: " + slot.getProfessionalName();
        return message;
    }
}
