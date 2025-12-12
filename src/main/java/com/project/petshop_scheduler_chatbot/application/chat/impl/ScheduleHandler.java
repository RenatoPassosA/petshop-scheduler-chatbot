package com.project.petshop_scheduler_chatbot.application.chat.impl;

import java.util.ArrayList;
import java.util.List;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

public class ScheduleHandler {

    private final PetRepository petRepository;
    private final PetServiceRepository petServiceRepository;
    private final AppointmentRepository appointmentRepository;


    public ScheduleHandler(PetRepository petRepository, PetServiceRepository petServiceRepository, AppointmentRepository appointmentRepository) {
        this.petRepository = petRepository;
        this.petServiceRepository = petServiceRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_START(ConversationSession session, ProcessIncomingMessageCommand command) {
        List<Pet> pets = petRepository.listByTutor(session.getTutorId());
            if (pets.isEmpty()) {
                session.setCurrentState(ConversationState.STATE_MAIN_MENU);
                return ProcessIncomingMessageResult.interactiveWithMessage("Você não tem nenhum pet cadastrado.\n\n O que deseja fazer?\n\n", MenuMessages.mainMenu(session.getTempTutorName()));
            }
            else {
                List<ButtonOption> petButtons = new ArrayList<>();
                for (Pet petList : pets) {
                    String stringIndex = petList.getId().toString();
                    petButtons.add(new ButtonOption(stringIndex, petList.getName()));
                }
                session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_PET);
                return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Para qual pet deseja agendar?\n", petButtons));
            }
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_PET(ConversationSession session, ProcessIncomingMessageCommand command) {
        session.setPetId(Long.valueOf(command.getButtonId()));
        List<PetService> petServices = petServiceRepository.getAll();
        List<ButtonOption> petButtons = new ArrayList<>();
            for (PetService petServiceList : petServices) {
                String stringIndex = petServiceList.getId().toString();
                petButtons.add(new ButtonOption(stringIndex, petServiceList.getName()));
            }
            session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual serviço deseja agendar?\n", petButtons));
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_SERVICE(ConversationSession session, ProcessIncomingMessageCommand command) {
        session.setChoosenServiceId(Long.valueOf(command.getButtonId()));
        //LÓGICA capturar proximos dias disponiveis SLOTs
        List<ButtonOption> petButtons = new ArrayList<>();
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual horário deseja agendar?\n", petButtons));
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_SLOT(ConversationSession session, ProcessIncomingMessageCommand command) {
            session.setCurrentState(ConversationState.STATE_SCHEDULE_CONFIRM);
            String petName = petRepository.findById(session.getPetId()).get().getName();
            String serviceName = petServiceRepository.findById(session.getChoosenServiceId()).get().getName();
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Podemos confirmar o agendamento?\nPet:" + petName + "\nServiço:" + serviceName + "\nHorário:",
                List.of(new ButtonOption("YES", "SIM"),
                    new ButtonOption("NO", "NÃO"))));   
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CONFIRM(ConversationSession session, ProcessIncomingMessageCommand command) {
        if ("NO".equals(command.getButtonId())) {
            session.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("O que deseja fazer?\n\n", MenuMessages.mainMenu(session.getTempTutorName()));
        }
        else {
            session.setCurrentState(ConversationState.STATE_FINISHED);
            return ProcessIncomingMessageResult.text("Agradecemos a preferencia!\nEstamos aguardando o seu pet!");
        }
    }
}
