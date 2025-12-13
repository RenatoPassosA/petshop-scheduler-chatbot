package com.project.petshop_scheduler_chatbot.application.chat.impl;

import java.util.ArrayList;
import java.util.List;

import com.project.petshop_scheduler_chatbot.application.appointment.ListAvailableSlotsUseCase;
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

public class ScheduleHandler {

    private final PetRepository petRepository;
    private final PetServiceRepository petServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final ListAvailableSlotsUseCase listAvailableSlotsUseCase; 


    public ScheduleHandler(PetRepository petRepository, PetServiceRepository petServiceRepository, AppointmentRepository appointmentRepository, ListAvailableSlotsUseCase listAvailableSlotsUseCase) {
        this.petRepository = petRepository;
        this.petServiceRepository = petServiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.listAvailableSlotsUseCase = listAvailableSlotsUseCase;
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_SCHEDULE_START(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return ProcessIncomingMessageResult.interactive(MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        return generatePetButtons(conversationSession, false);
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_PET(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_SCHEDULE_CHOOSE_PET(conversationSession, messageCommand)) {
                conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_START);
                return generatePetButtons(conversationSession, true);
        }
        conversationSession.setPetId(Long.valueOf(messageCommand.getButtonId()));
        return generateServiceButtons(conversationSession, false);
    }


    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_SERVICE(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_SCHEDULE_CHOOSE_SERVICE(conversationSession, messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return generateServiceButtons(conversationSession, true);
        }
        conversationSession.setChoosenServiceId(Long.valueOf(messageCommand.getButtonId()));




        //LÓGICA capturar proximos dias disponiveis SLOTs




        List<ButtonOption> petButtons = new ArrayList<>();
        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual horário deseja agendar?\n", petButtons));
    }

    private ProcessIncomingMessageResult generateSlots(ConversationSession conversationSession, boolean withError) {
        listAvailableSlotsUseCase.listSlots(null, null)
    
    }


    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
            conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CONFIRM);
            String petName = petRepository.findById(conversationSession.getPetId()).get().getName();
            String serviceName = petServiceRepository.findById(conversationSession.getChoosenServiceId()).get().getName();
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Podemos confirmar o agendamento?\nPet:" + petName + "\nServiço:" + serviceName + "\nHorário:",
                List.of(new ButtonOption("YES", "SIM"),
                    new ButtonOption("NO", "NÃO"))));   
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CONFIRM(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if ("NO".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getTempTutorName()));
        }
        else {
            conversationSession.setCurrentState(ConversationState.STATE_FINISHED);
            return ProcessIncomingMessageResult.text("Agradecemos a preferencia!\nEstamos aguardando o seu pet!");
        }
    }


    private boolean checkError_STATE_SCHEDULE_START(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || !"SCHEDULE".equals(id))
            return true;
        return false;
    }

    private boolean checkError_STATE_SCHEDULE_CHOOSE_PET(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null)
            return true;

        int petId;
        try {
            petId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return true;
        }

        List<Long> petIdsList = conversationSession.getAllTutorsPets();
        if (petIdsList == null || petIdsList.isEmpty())
            return true;

        boolean existsId = false;

        for (Long itens : petIdsList) {
            if (itens.equals(Long.valueOf(petId)))
                existsId = true;
        }
        if (existsId)
            return false; 
        return true;
    }

    private boolean checkError_STATE_SCHEDULE_CHOOSE_SERVICE(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null)
            return true;

        int serviceId;
        try {
            serviceId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return true;
        }

        List<PetService> petServices = petServiceRepository.getAll();
        if (petServices == null || petServices.isEmpty())
            return true;

        boolean existsId = false;

        for (PetService services : petServices) {
            if (services.getId().equals(Long.valueOf(serviceId)))
                existsId = true;
        }
        if (existsId)
            return false; 
        return true;
    }

    private ProcessIncomingMessageResult generatePetButtons(ConversationSession conversationSession, boolean withError) {
        List<Pet> pets = petRepository.listByTutor(conversationSession.getTutorId());
        if (pets.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Você não tem nenhum pet cadastrado.\n\n O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getTempTutorName()));
        }
        List<ButtonOption> petButtons = new ArrayList<>();
        List<Long> petIds = new ArrayList<>();
        for (Pet petList : pets) {
            String stringIndex = petList.getId().toString();
            petButtons.add(new ButtonOption(stringIndex, petList.getName()));
            petIds.add(petList.getId());
        }
        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_PET);
        conversationSession.setAllTutorsPets(petIds);
        if (withError) {
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\nPara qual pet deseja agendar?\n", new InteractiveMessage( "Para qual pet deseja agendar?\n",petButtons));
        }
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Para qual pet deseja agendar?\n", petButtons));
    }

    private ProcessIncomingMessageResult generateServiceButtons(ConversationSession conversationSession, boolean withError) {
        List<PetService> petServices = petServiceRepository.getAll();
        List<ButtonOption> serviceButtons = new ArrayList<>();
        for (PetService petServiceList : petServices) {
            String serviceId = petServiceList.getId().toString();
            serviceButtons.add(new ButtonOption(serviceId, petServiceList.getName()));
        }
        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);
        if (withError)
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\nQual serviço deseja agendar?\n", new InteractiveMessage( "Para qual pet deseja agendar?\n",serviceButtons));
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual serviço deseja agendar?\n", serviceButtons));
    }

    



    


    







}
