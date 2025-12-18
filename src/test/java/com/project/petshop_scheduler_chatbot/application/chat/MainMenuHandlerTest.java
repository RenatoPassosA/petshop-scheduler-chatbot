package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.CancelScheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.MainMenuHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RegisterPetHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RescheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.ScheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.ServicesFormatedList;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;

@ExtendWith(MockitoExtension.class)
public class MainMenuHandlerTest {

    @Mock private ScheduleHandler scheduleHandler;
    @Mock private RescheduleHandler rescheduleHandler;
    @Mock private CancelScheduleHandler cancelScheduleHandler;
    @Mock private RegisterPetHandler registerPetHandler;
    @Mock private ServicesFormatedList servicesFormatedList;

    private MainMenuHandler handler;

    @BeforeEach
    void setUp() {
        handler = new MainMenuHandler(
            scheduleHandler,
            rescheduleHandler,
            cancelScheduleHandler,
            registerPetHandler,
            servicesFormatedList
        );
    }

    private ProcessIncomingMessageCommand cmdWithButton(String buttonId) {
        return new ProcessIncomingMessageCommand("5521988398302", null, buttonId, "950730164782242");
    }

    @Test
    void mainMenu_buttonNull_shouldReturnInteractiveError_andKeepStateMainMenu() {
        ConversationSession session = new ConversationSession("5521988398302");
        session.setRegisteredTutorName("Renato");

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmdWithButton(null));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result).isNotNull();
        assertThat(result.getType().name()).isEqualTo("INTERACTIVE");
        assertThat(result.getText()).contains("⚠️ Opa! Não entendi");

        verifyNoInteractions(scheduleHandler, rescheduleHandler, cancelScheduleHandler, registerPetHandler, servicesFormatedList);
    }

    @Test
    void mainMenu_buttonInvalid_shouldReturnInteractiveError() {
        ConversationSession session = new ConversationSession("5521988398302");
        session.setRegisteredTutorName("Renato");

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmdWithButton("INVALID"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType().name()).isEqualTo("INTERACTIVE");

        verifyNoInteractions(scheduleHandler, rescheduleHandler, cancelScheduleHandler, registerPetHandler, servicesFormatedList);
    }

    @Test
    void mainMenu_schedule_shouldSetStateAndDelegateToScheduleHandler() {
        ConversationSession session = new ConversationSession("5521988398302");
        ProcessIncomingMessageCommand cmd = cmdWithButton("SCHEDULE");

        ProcessIncomingMessageResult expected = ProcessIncomingMessageResult.text("ok schedule");
        when(scheduleHandler.handle_STATE_SCHEDULE_START(eq(session), eq(cmd))).thenReturn(expected);

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmd);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_START);
        assertThat(result).isSameAs(expected);

        verify(scheduleHandler).handle_STATE_SCHEDULE_START(eq(session), eq(cmd));
        verifyNoInteractions(rescheduleHandler, cancelScheduleHandler, registerPetHandler, servicesFormatedList);
    }

    @Test
    void mainMenu_reschedule_shouldSetStateAndDelegate() {
        ConversationSession session = new ConversationSession("5521988398302");
        ProcessIncomingMessageCommand cmd = cmdWithButton("RESCHEDULE");

        ProcessIncomingMessageResult expected = ProcessIncomingMessageResult.text("ok reschedule");
        when(rescheduleHandler.handle_STATE_RESCHEDULE_START(eq(session), eq(cmd))).thenReturn(expected);

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmd);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_RESCHEDULE_START);
        assertThat(result).isSameAs(expected);

        verify(rescheduleHandler).handle_STATE_RESCHEDULE_START(eq(session), eq(cmd));
        verifyNoInteractions(scheduleHandler, cancelScheduleHandler, registerPetHandler, servicesFormatedList);
    }

    @Test
    void mainMenu_cancelSchedule_shouldSetStateAndDelegate() {
        ConversationSession session = new ConversationSession("5521988398302");
        ProcessIncomingMessageCommand cmd = cmdWithButton("CANCEL_SCHEDULE");

        ProcessIncomingMessageResult expected = ProcessIncomingMessageResult.text("ok cancel");
        when(cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_START(eq(session), eq(cmd))).thenReturn(expected);

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmd);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_CANCEL_SCHEDULE_START);
        assertThat(result).isSameAs(expected);

        verify(cancelScheduleHandler).handle_STATE_CANCEL_SCHEDULE_START(eq(session), eq(cmd));
        verifyNoInteractions(scheduleHandler, rescheduleHandler, registerPetHandler, servicesFormatedList);
    }

    @Test
    void mainMenu_registerPet_shouldSetStateAndDelegate() {
        ConversationSession session = new ConversationSession("5521988398302");
        ProcessIncomingMessageCommand cmd = cmdWithButton("REGISTER_PET");

        ProcessIncomingMessageResult expected = ProcessIncomingMessageResult.text("ok pet");
        when(registerPetHandler.handle_STATE_REGISTER_PET_START(eq(session), eq(cmd))).thenReturn(expected);

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmd);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_START);
        assertThat(result).isSameAs(expected);

        verify(registerPetHandler).handle_STATE_REGISTER_PET_START(eq(session), eq(cmd));
        verifyNoInteractions(scheduleHandler, rescheduleHandler, cancelScheduleHandler, servicesFormatedList);
    }

    @Test
    void mainMenu_checkServices_shouldSetStateAndReturnText() {
        ConversationSession session = new ConversationSession("5521988398302");
        ProcessIncomingMessageCommand cmd = cmdWithButton("CHECK_SERVICES");

        when(servicesFormatedList.getAllServicesFormated()).thenReturn("lista...");

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmd);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_CHECK_SERVICES);
        assertThat(result.getType().name()).isEqualTo("TEXT");
        assertThat(result.getText()).isEqualTo("lista...");

        verify(servicesFormatedList).getAllServicesFormated();
        verifyNoInteractions(scheduleHandler, rescheduleHandler, cancelScheduleHandler, registerPetHandler);
    }

    @Test
    void mainMenu_talkToHuman_shouldSetFlagAndReturnText() {
        ConversationSession session = new ConversationSession("5521988398302");
        ProcessIncomingMessageCommand cmd = cmdWithButton("TALK_TO_HUMAN");

        ProcessIncomingMessageResult result = handler.STATE_MAIN_MENU_handler(session, cmd);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_CHAT_WITH_HUMAN);
        assertThat(session.isChatWithHuman()).isTrue();
        assertThat(result.getType().name()).isEqualTo("TEXT");
        assertThat(result.getText()).contains("Aguarde um instante");

        verifyNoInteractions(scheduleHandler, rescheduleHandler, cancelScheduleHandler, registerPetHandler, servicesFormatedList);
    }
}