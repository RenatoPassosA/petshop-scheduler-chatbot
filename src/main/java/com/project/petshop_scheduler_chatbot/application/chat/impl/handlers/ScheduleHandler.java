package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashSet;
import java.util.Locale;

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
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.SlotKeyHelper;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.BusinessTime;

@Component
public class ScheduleHandler {

    private final PetRepository petRepository;
    private final PetServiceRepository petServiceRepository;
    private final ListAvailableSlotsUseCase listAvailableSlotsUseCase; 
    private final ScheduleAppointmentUseCase scheduleAppointmentUseCase;
    private final StartMenuHandler startMenuHandler;
    private final ProfessionalRepository professionalRepository;

    public ScheduleHandler(PetRepository petRepository, PetServiceRepository petServiceRepository, ListAvailableSlotsUseCase listAvailableSlotsUseCase, ScheduleAppointmentUseCase scheduleAppointmentUseCase, StartMenuHandler startMenuHandler, ProfessionalRepository professionalRepository) {
        this.petRepository = petRepository;
        this.petServiceRepository = petServiceRepository;
        this.listAvailableSlotsUseCase = listAvailableSlotsUseCase;
        this.scheduleAppointmentUseCase = scheduleAppointmentUseCase;
        this.startMenuHandler = startMenuHandler;
        this.professionalRepository = professionalRepository;
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
        return generateDaysButtons(conversationSession, false);
    }


    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_DAY(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String dayId = messageCommand.getButtonId(); // "2026-01-24"
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


    public ProcessIncomingMessageResult handle_STATE_SCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        ProcessIncomingMessageResult error = checkError_STATE_SCHEDULE_CHOOSE_SLOT(conversationSession, messageCommand);
        if (error != null) return error;

        String slotKey = messageCommand.getButtonId();
        var parsed = SlotKeyHelper.parse(slotKey);
        if (parsed == null) {
            return generateDaysButtons(conversationSession, true);
        }

        Long serviceId = conversationSession.getChosenServiceId();
        if (serviceId == null) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "⚠️ Perdi o contexto do agendamento. Vamos voltar ao menu.\n\n",
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

        if (chosen == null) {
            return generateDaysButtons(conversationSession, true);
        }

        conversationSession.setChosenSlot(chosen);
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
        conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
        return ProcessIncomingMessageResult.interactiveWithMessage("Agradecemos a preferencia!\nEstamos aguardando o seu pet!\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
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

    private ProcessIncomingMessageResult checkError_STATE_SCHEDULE_CHOOSE_SLOT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || id.isBlank()) {
            return generateDaysButtons(conversationSession, true);
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

        if (withError) 
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Não entendi.\n", new InteractiveMessage( "Para qual pet deseja agendar?\n",petButtons));

        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Para qual pet deseja agendar?\n", petButtons));
    }

    private ProcessIncomingMessageResult generateServiceButtons(ConversationSession conversationSession, boolean withError) {
        List<PetService> petServices = petServiceRepository.getAll();

        List<ButtonOption> rows = new ArrayList<>();
        for (PetService s : petServices) {
            String id = s.getId().toString();
            String title = truncate(s.getName(), 24);
            rows.add(new ButtonOption(id, title));
        }

        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);

        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";
        return ProcessIncomingMessageResult.interactiveWithMessage(
            prefix,
            InteractiveMessage.list(
                "Qual serviço deseja agendar?\n",
                "Escolher serviço",
                "Serviços",
                rows
            )
        );
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
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "Não encontrei horários disponíveis nos próximos dias.\n\n",
                MenuMessages.mainMenu(conversationSession.getRegisteredTutorName())
            );
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
            if (rows.size() >= 10) break;
        }

        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_DAY);
        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";
        return ProcessIncomingMessageResult.interactiveWithMessage(
            prefix,
            InteractiveMessage.list(
                "Escolha o dia:\n",
                "Escolher dia",
                "Dias disponíveis",
                rows
            )
        );
    }

    private ProcessIncomingMessageResult generateSlotsButtonsForDay(ConversationSession conversationSession, LocalDate chosenDay, boolean withError) {
        Long serviceId = conversationSession.getChosenServiceId();
        if (serviceId == null) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage(
                "⚠️ Perdi o contexto do serviço. Voltando ao menu.\n\n",
                MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
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

        if (daySlots.isEmpty())
            return generateDaysButtons(conversationSession, true);

        List<ButtonOption> rows = new ArrayList<>();
        for (AvailableSlots slot : daySlots) {
            String id = SlotKeyHelper.toKey(slot.getStartAt(), slot.getProfessionalId());    
            String hhmm = slot.getStartAt().atZoneSameInstant(BusinessTime.BUSINESS_ZONE).toLocalTime().toString().substring(0, 5);
            String profName = (slot.getProfessionalName() == null || slot.getProfessionalName().isBlank()) ? "Profissional" : slot.getProfessionalName();
            String title = truncate(hhmm + " - " + profName, 24);
            rows.add(new ButtonOption(id, title));
            if (rows.size() >= 10) break;
        }

        conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);
        String prefix = withError ? "⚠️ Não entendi.\n\n" : "";
        return ProcessIncomingMessageResult.interactiveWithMessage(prefix, InteractiveMessage.list("Agora escolha o horário:\n",
                                                                                                    "Escolher horário",
                                                                                                    "Horários",
                                                                                                    rows));
    }

    private String generateConfirmationMessage(ConversationSession conversationSession, boolean withError) {
        AvailableSlots slot = conversationSession.getChosenSlot();
        String petName = petRepository.findById(conversationSession.getPetId()).map(Pet::getName).orElse("Pet não encontrado");
        String serviceName = petServiceRepository.findById(conversationSession.getChosenServiceId()).map(PetService::getName).orElse("Serviço não encontrado");
        String professionalName = resolveProfessionalName(slot);
        String header = withError ? "⚠️ Não entendi, selecione SIM ou NÃO.\n" : "";

        return header +
            "Dia: " + DateTimeFormatterHelper.formatDateTime(slot.getStartAt()) +
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

    private String formatDayTitle(LocalDate d) {
        TextStyle style = TextStyle.SHORT;
        Locale ptBR = new Locale("pt", "BR");
        String dow = d.getDayOfWeek().getDisplayName(style, ptBR);
        dow = dow.replace(".", "");
        String ddmm = String.format("%02d/%02d", d.getDayOfMonth(), d.getMonthValue());
        return capitalize(dow) + " (" + ddmm + ")";
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank())
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String resolveProfessionalName(AvailableSlots slot) {
        if (slot == null)
            return "Profissional";

        if (slot.getProfessionalName() != null && !slot.getProfessionalName().isBlank())
            return slot.getProfessionalName();

        Long profId = slot.getProfessionalId();

        if (profId == null)
            return "Profissional";
        return professionalRepository.findById(profId).map(Professional::getName).orElse("Profissional");
    }
}

