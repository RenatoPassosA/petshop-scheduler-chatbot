package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.DateTimeFormatterHelper;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Component
public class CancelScheduleHandler {

    private final AppointmentRepository appointmentRepository;
    private final PetServiceRepository petServiceRepository;
    private final PetRepository petRepository;

    private final CancelAppointmentUseCase cancelAppointmentUseCase;

    public CancelScheduleHandler (AppointmentRepository appointmentRepository,
                                PetServiceRepository petServiceRepository,
                                PetRepository petRepository,
                                CancelAppointmentUseCase cancelAppointmentUseCase) {
        this.appointmentRepository = appointmentRepository;
        this.petServiceRepository = petServiceRepository;
        this.petRepository = petRepository;
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
    }

    public ProcessIncomingMessageResult handle_STATE_CANCEL_SCHEDULE_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_CANCEL_SCHEDULE_START(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Opa! Não entendi sua escolha.\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        List<Appointment> appointments = appointmentRepository.findByTutorId(conversationSession.getTutorId());
        if (appointments.isEmpty()){
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Você não tem nenhum serviço agendado.\n\n O que deseja fazer?\n\n",
                                                            MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        List<ButtonOption> appointmentButtons = new ArrayList<>();
        for (Appointment appointmentList : appointments) {
            Optional<PetService> petService = petServiceRepository.findById(appointmentList.getServiceId());
            Optional<Pet> pet = petRepository.findById(appointmentList.getPetId());

            if (petService.isEmpty() || pet.isEmpty()) {
                continue;
            }
            String buttonIndex = appointmentList.getId().toString();
            String serviceName = petService.get().getName();
            String petName = pet.get().getName();
            String serviceDate =  DateTimeFormatterHelper.formatDateTime(appointmentList.getStartAt());
            String appointmentInfo = serviceName + " para " + petName + " dia: " + serviceDate;
            appointmentButtons.add(new ButtonOption(buttonIndex, appointmentInfo));
        }

        if (appointmentButtons.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Não encontrei agendamentos válidos no momento.\n\n O que deseja fazer?\n\n",
                                                            MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        conversationSession.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT);
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual serviço deseja cancelar?\n", appointmentButtons));
    }

    public ProcessIncomingMessageResult handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        Long appointmentId = checkIfButtonIdIsNumeric(messageCommand.getButtonId());

        if (appointmentId == null) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Consulta inválida. O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);

        if (appointment.isEmpty() || !appointmentBelongsToTutor(appointment.get(), conversationSession)) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Consulta inválida. O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        Optional<PetService> petService = petServiceRepository.findById(appointment.get().getServiceId());
        Optional<Pet> pet = petRepository.findById(appointment.get().getPetId());

        if (petService.isEmpty() || pet.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Consulta inválida. O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        Appointment confirmedAppointmentSession = appointment.get();
        
        if (!canCancelAppointment(confirmedAppointmentSession)) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("🚫 Infelizmente não é possível cancelar agendamente com menos de 24h de antecedência.\n\n", 
                                                                        MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        String serviceName = petService.get().getName();
        String petName = pet.get().getName();
        String serviceDate = DateTimeFormatterHelper.formatDateTime(confirmedAppointmentSession.getStartAt());
        String cancelAppointmentInfo = serviceName + " para " + petName + " dia: " + serviceDate;

        conversationSession.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CONFIRM);
        conversationSession.setAppointmentId(appointmentId);

        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Podemos confirmar o cancelamento?\n" + cancelAppointmentInfo,
                List.of(new ButtonOption("YES", "SIM"),
                    new ButtonOption("NO", "NÃO"))));  
    }

    public ProcessIncomingMessageResult handle_STATE_CANCEL_SCHEDULE_CONFIRM(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_CANCEL_SCHEDULE_CONFIRM(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Opa! Não entendi sua escolha.\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        if ("NO".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        Long appointmentId = conversationSession.getAppointmentId();
        CancelAppointmentCommand command = new CancelAppointmentCommand(appointmentId);
        cancelAppointmentUseCase.execute(command);
        conversationSession.resetFlowData();
        conversationSession.setCurrentState(ConversationState.STATE_FINISHED);
        return ProcessIncomingMessageResult.text("Agradecemos a preferencia!\n");
    }

    private boolean canCancelAppointment(Appointment appointment) {
        OffsetDateTime limit = appointment.getStartAt().minusHours(24);
        return OffsetDateTime.now().isBefore(limit);
    }

    private boolean appointmentBelongsToTutor(Appointment appointment, ConversationSession conversationSession) {
        Long tutorIdOnAppointment = appointment.getTutorId();
        Long tutorIdOnSession = conversationSession.getTutorId();
        return tutorIdOnAppointment != null && tutorIdOnAppointment.equals(tutorIdOnSession);
    }

    private boolean checkError_STATE_CANCEL_SCHEDULE_START(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || !"CANCEL_SCHEDULE".equals(id))
            return true;
        return false;
    }

    private boolean checkError_STATE_CANCEL_SCHEDULE_CONFIRM(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"YES".equals(id) &&!"NO".equals(id)))  
            return true;
        return false;
    }

    private Long checkIfButtonIdIsNumeric(String buttonId) {
        Long appointmentId;

        try {
            appointmentId = Long.valueOf(buttonId);
            return appointmentId;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}

