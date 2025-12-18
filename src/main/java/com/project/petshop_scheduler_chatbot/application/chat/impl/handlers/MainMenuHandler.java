package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.ServicesFormatedList;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;

@Component
public class MainMenuHandler {

    private final ScheduleHandler scheduleHandler;
    private final RescheduleHandler rescheduleHandler;
    private final CancelScheduleHandler cancelScheduleHandler;
    private final RegisterPetHandler registerPetHandler;

    private final ServicesFormatedList servicesFormatedList;


    public MainMenuHandler(ScheduleHandler scheduleHandler, RescheduleHandler rescheduleHandler, CancelScheduleHandler cancelScheduleHandler, RegisterPetHandler registerPetHandler, ServicesFormatedList servicesFormatedList) {
        this.scheduleHandler = scheduleHandler;
        this.rescheduleHandler = rescheduleHandler;
        this.cancelScheduleHandler = cancelScheduleHandler;
        this.registerPetHandler = registerPetHandler;
        this.servicesFormatedList = servicesFormatedList;
    }

    public ProcessIncomingMessageResult STATE_MAIN_MENU_handler(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Opa! Não entendi sua escolha.\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        
        if ("SCHEDULE".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_START);
            return scheduleHandler.handle_STATE_SCHEDULE_START(conversationSession, messageCommand);
        }
        else if ("RESCHEDULE".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_START);
            return rescheduleHandler.handle_STATE_RESCHEDULE_START(conversationSession, messageCommand);
        }
        else if ("CANCEL_SCHEDULE".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_START);
            return cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_START(conversationSession, messageCommand);
        }
        else if ("REGISTER_PET".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_START);
            return registerPetHandler.handle_STATE_REGISTER_PET_START(conversationSession, messageCommand);
        }
        else if ("CHECK_SERVICES".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_CHECK_SERVICES);
            String servicesList = servicesFormatedList.getAllServicesFormated();
            return ProcessIncomingMessageResult.text(servicesList);
        } else {
            conversationSession.setCurrentState(ConversationState.STATE_CHAT_WITH_HUMAN);
            conversationSession.setChatWithHuman(true);
            return ProcessIncomingMessageResult.text("Aguarde um instante, você já será atendido.");
        }
    }

    private boolean checkError(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || 
        (!"SCHEDULE".equals(id) &&  
        !"RESCHEDULE".equals(id) && 
        !"CANCEL_SCHEDULE".equals(id) &&  
        !"REGISTER_PET".equals(id) &&  
        !"CHECK_SERVICES".equals(id)&&  
        !"TALK_TO_HUMAN".equals(id)))
            return true;
        return false;
    }
}

