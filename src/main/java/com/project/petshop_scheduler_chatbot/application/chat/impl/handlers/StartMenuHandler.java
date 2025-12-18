package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@Component
public class StartMenuHandler {

    private final TutorRepository tutorRepository;

    public StartMenuHandler(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    public ProcessIncomingMessageResult STATE_START_handler(ConversationSession conversationSession) {
        PhoneNumber phone = new PhoneNumber(conversationSession.getWaId());
        if (tutorRepository.existsByPhone(phone)) {
            String tutorName = tutorRepository.findByPhone(phone).get().getName();
            Long tutorId = tutorRepository.findByPhone(phone).get().getId();
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            conversationSession.setRegisteredTutorName(tutorName);
            conversationSession.setTutorId(tutorId);
            return ProcessIncomingMessageResult.interactive(MenuMessages.mainMenu(tutorName));
        }
        else {
            conversationSession.setCurrentState(ConversationState.STATE_NO_REGISTERED_MENU);
            return ProcessIncomingMessageResult.interactive(MenuMessages.noRegisteredMenu());
        }
    }
}
