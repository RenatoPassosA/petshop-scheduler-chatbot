package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.StartMenuHandler;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
class StartMenuHandlerTest {

    @Mock
    private TutorRepository tutorRepository;

    private StartMenuHandler startMenuHandler;

    @BeforeEach
    void setUp() {
        startMenuHandler = new StartMenuHandler(tutorRepository);
    }

    @Test
    void start_WhenTutorExists_ShouldSetMainMenuAndTutorData_AndReturnInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_START);

        Tutor tutor = mock(Tutor.class);
        when(tutor.getName()).thenReturn("Renato");
        when(tutor.getId()).thenReturn(10L);

        when(tutorRepository.existsByPhone(any(PhoneNumber.class))).thenReturn(true);
        when(tutorRepository.findByPhone(any(PhoneNumber.class))).thenReturn(Optional.of(tutor));

        ProcessIncomingMessageResult result = startMenuHandler.STATE_START_handler(session);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(session.getRegisteredTutorName()).isEqualTo("Renato");
        assertThat(session.getTutorId()).isEqualTo(10L);
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verify(tutorRepository, times(1)).existsByPhone(any(PhoneNumber.class));
        verify(tutorRepository, atLeastOnce()).findByPhone(any(PhoneNumber.class));
    }

    @Test
    void start_WhenTutorDoesNotExist_ShouldSetNoRegisteredMenu_AndReturnInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_START);

        when(tutorRepository.existsByPhone(any(PhoneNumber.class))).thenReturn(false);

        ProcessIncomingMessageResult result = startMenuHandler.STATE_START_handler(session);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_NO_REGISTERED_MENU);
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verify(tutorRepository, times(1)).existsByPhone(any(PhoneNumber.class));
        verify(tutorRepository, never()).findByPhone(any());
    }
}
