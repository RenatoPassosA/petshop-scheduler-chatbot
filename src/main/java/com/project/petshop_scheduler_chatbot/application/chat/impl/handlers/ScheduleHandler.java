package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.appointment.AvailableSlots;
import com.project.petshop_scheduler_chatbot.application.appointment.ListAvailableSlotsUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.DateTimeFormatterHelper;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Component
public class ScheduleHandler {

    private final PetRepository petRepository;
    private final PetServiceRepository petServiceRepository;
    private final ListAvailableSlotsUseCase listAvailableSlotsUseCase; 
    private final ScheduleAppointmentUseCase scheduleAppointmentUseCase;
    private final StartMenuHandler startMenuHandler;


    public ScheduleHandler(PetRepository petRepository, PetServiceRepository petServiceRepository, ListAvailableSlotsUseCase listAvailableSlotsUseCase, ScheduleAppointmentUseCase scheduleAppointmentUseCase, StartMenuHandler startMenuHandler) {
        this.petRepository = petRepository;
        this.petServiceRepository = petServiceRepository;
        this.listAvailableSlotsUseCase = listAvailableSlotsUseCase;
        this.scheduleAppointmentUseCase = scheduleAppointmentUseCase;
        this.startMenuHandler = startMenuHandler;
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_SCHEDULE_START(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return startMenuHandler.STATE_START_handler(conversationSession);
        }
        return generatePetButtons(conversationSession, false);
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_PET(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_SCHEDULE_CHOOSE_PET(conversationSession, messageCommand)) {
            return generatePetButtons(conversationSession, true);
        }
        conversationSession.setPetId(Long.parseLong(messageCommand.getButtonId()));
        return generateServiceButtons(conversationSession, false);
    }


    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_SERVICE(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_SCHEDULE_CHOOSE_SERVICE(conversationSession, messageCommand)) {
            return generateServiceButtons(conversationSession, true);
        }
        conversationSession.setChosenServiceId(Long.valueOf(messageCommand.getButtonId()));
        return generateSlotsButtons(conversationSession, false);
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        ProcessIncomingMessageResult error = checkError_STATE_SCHEDULE_CHOOSE_SLOT(conversationSession, messageCommand);
        if (error != null) {
            return error;
        }
        int index = Integer.parseInt(messageCommand.getButtonId());
        AvailableSlots chosenSlot = conversationSession.getSlots().get(index);
        conversationSession.setChosenSlot(chosenSlot);
        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_OBS);
        return ProcessIncomingMessageResult.text("Alguma observação importante para essa consulta?");        
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_OBS(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_TextNullOrBlank(messageCommand)) {
            conversationSession.setObservations("sem observações");
        }
        else
            conversationSession.setObservations(messageCommand.getText());
        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CONFIRM);
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Podemos confirmar o agendamento?\n\n" + generateConfirmationMessage(conversationSession, false),
                                                        List.of(new ButtonOption("YES", "SIM"),
                                                            new ButtonOption("NO", "NÃO"))));
    }

    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CONFIRM(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_SCHEDULE_CONFIRM(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CONFIRM);
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage(generateConfirmationMessage(conversationSession, true),
                                                                                                            List.of(new ButtonOption("YES", "SIM"),
                                                                                                                    new ButtonOption("NO", "NÃO")))); 
        }
        
        if ("NO".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(conversationSession.getPetId(),
                                                                            conversationSession.getTutorId(),
                                                                            conversationSession.getChosenSlot().getProfessionalId(),
                                                                            conversationSession.getChosenServiceId(),
                                                                            conversationSession.getChosenSlot().getStartAt(),
                                                                            conversationSession.getObservations());
        scheduleAppointmentUseCase.execute(command);
        conversationSession.setCurrentState(ConversationState.STATE_START);
        return ProcessIncomingMessageResult.text("Agradecemos a preferencia!\nEstamos aguardando o seu pet!"); 
    }

    private boolean checkError_STATE_SCHEDULE_START(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || !"SCHEDULE".equals(id))
            return true;
        return false;
    }

    private boolean checkError_STATE_SCHEDULE_CHOOSE_PET(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null) {
            System.out.println("Button ID is null in STATE_SCHEDULE_CHOOSE_PET");
            return true;
        }
        long  petId;
        try {
            petId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            System.out.println("Button ID is not a valid number in STATE_SCHEDULE_CHOOSE_PET");
            return true;
        }

        List<Pet> pets = petRepository.listByTutor(conversationSession.getTutorId());
        return pets.stream().noneMatch(p -> p.getId().equals(petId));

        // List<Long> petIdsList = conversationSession.getAllTutorsPets();
        // if (petIdsList == null || petIdsList.isEmpty()) {
        //     System.out.println("Pet IDs list is null or empty in STATE_SCHEDULE_CHOOSE_PET");
        //     return true;
        // }

        // boolean existsId = false;

        // for (Long itens : petIdsList) {
        //     if (itens.equals(Long.valueOf(petId))) {
        //         existsId = true;
        //         break;
        //     }
        // }
        // if (existsId) {
        //     System.out.println("existe o pet na lista");
        //     return false;
        //     }
        // else {
        //     System.out.println("nao existe o pet na lista");
        //     return true;
        // }
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
            if (services.getId().equals(Long.valueOf(serviceId))) {
                existsId = true;
                break;
            }
        }
        if (existsId)
            return false; 
        return true;
    }

    private ProcessIncomingMessageResult  checkError_STATE_SCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null)
            return generateSlotsButtons(conversationSession, true);

        int index;
        try {
            index = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return generateSlotsButtons(conversationSession, true);
        }

        List<AvailableSlots> availableSlots  = conversationSession.getSlots();
        if (availableSlots  == null || availableSlots.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Não encontrei horários disponíveis nos próximos dias.\n\n",
                                                                    MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        if (index < 0 || index >= availableSlots.size()) {
            return generateSlotsButtons(conversationSession, true);
        }
        return null;
    }

    private boolean checkError_TextNullOrBlank(ProcessIncomingMessageCommand messageCommand) {
        String text = messageCommand.getText();
        if (text == null)
            return true;
        text = text.trim();
        if (text.isBlank())
            return true;
        return false;
    }

    private boolean checkError_STATE_SCHEDULE_CONFIRM(ProcessIncomingMessageCommand messageCommand) {
            String id = messageCommand.getButtonId();
            if (id == null || (!"YES".equals(id) &&!"NO".equals(id)))  
                return true;
            return false;
        }

    private ProcessIncomingMessageResult generatePetButtons(ConversationSession conversationSession, boolean withError) {
        List<Pet> pets = petRepository.listByTutor(conversationSession.getTutorId());
        if (pets.isEmpty()) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("Você não tem nenhum pet cadastrado.\n\n O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
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
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\n", new InteractiveMessage( "Para qual pet deseja agendar?\n",petButtons));
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
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\n\n", new InteractiveMessage( "Qual serviço deseja agendar?\n",serviceButtons));
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual serviço deseja agendar?\n", serviceButtons));
    }

    private ProcessIncomingMessageResult generateSlotsButtons(ConversationSession conversationSession, boolean withError) {
        List<AvailableSlots> availableSlots = listAvailableSlotsUseCase.listSlots(conversationSession.getChosenServiceId(), conversationSession.getLastInteraction());

        if (availableSlots.isEmpty()) {
            return ProcessIncomingMessageResult.interactiveWithMessage( "Não encontrei horários disponíveis nos próximos dias.\n\n",
                                                                        MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        List<ButtonOption> slotButtons = new ArrayList<>();
        for (int i = 0; i < availableSlots.size(); i++) {
            AvailableSlots slot = availableSlots.get(i);
            String slotText = "Dia: " + DateTimeFormatterHelper.formatDateTime(slot.getStartAt()) +
                            " Profissional: " + slot.getProfessionalName();
            slotButtons.add(new ButtonOption(String.valueOf(i), slotText));
        }

        conversationSession.setSlots(availableSlots);
        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);

        if (withError) {
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\n\n", new InteractiveMessage("Para qual horário deseja agendar?\n", slotButtons));
        }
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Para qual horário deseja agendar?\n", slotButtons));

    }

    private String generateConfirmationMessage(ConversationSession conversationSession, boolean withError) {
        AvailableSlots slot = conversationSession.getChosenSlot();

        String petName = petRepository.findById(conversationSession.getPetId()).map(Pet::getName).orElse("Pet não encontrado");

        String serviceName = petServiceRepository.findById(conversationSession.getChosenServiceId()).map(PetService::getName).orElse("Serviço não encontrado");

        String professionalName = slot.getProfessionalName() != null ? slot.getProfessionalName() : "Profissional selecionado automaticamente";

        String header = withError ? "⚠️ Não entendi, selecione SIM ou NÃO.\n" : "";

        return header +
            "Dia: " + DateTimeFormatterHelper.formatDateTime(slot.getStartAt()) +
            "\nPet: " + petName +
            "\nServiço: " + serviceName +
            "\nProfissional: " + professionalName;
    }

}

