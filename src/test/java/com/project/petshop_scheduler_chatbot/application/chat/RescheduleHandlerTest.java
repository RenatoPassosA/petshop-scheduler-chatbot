package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.appointment.AvailableSlots;
import com.project.petshop_scheduler_chatbot.application.appointment.ListAvailableSlotsUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RescheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.StartMenuHandler;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@ExtendWith(MockitoExtension.class)
class RescheduleHandlerTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private StartMenuHandler startMenuHandler;
    @Mock private PetRepository petRepository;
    @Mock private PetServiceRepository petServiceRepository;
    @Mock private ListAvailableSlotsUseCase listAvailableSlotsUseCase;
    @Mock private RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    private RescheduleHandler rescheduleHandler;

    @BeforeEach
    void setUp() {
        rescheduleHandler = new RescheduleHandler(
                appointmentRepository,
                startMenuHandler,
                petRepository,
                petServiceRepository,
                listAvailableSlotsUseCase,
                rescheduleAppointmentUseCase
        );
    }

    private ProcessIncomingMessageCommand command(String text, String buttonId) {
        return new ProcessIncomingMessageCommand("21988398302", text, buttonId, "21988398302");
    }

    private AvailableSlots slot(OffsetDateTime startAt, long professionalId, String name) {
        return new AvailableSlots(startAt, professionalId, name);
    }

    @Test
    void start_NoAppointments_ShouldGoToStartAndReturnMainMenuInteractiveWithMessage() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_START);

        when(appointmentRepository.findByTutorId(10L)).thenReturn(List.of());

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_START(session, command(null, "RESCHEDULE"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Você não tem nenhum serviço agendado");
        verifyNoInteractions(rescheduleAppointmentUseCase, listAvailableSlotsUseCase);
    }

    @Test
    void chooseSlot_InvalidButton_ShouldGoBackToMainMenu_AndReturnErrorInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setRegisteredTutorName("Renato");

        OffsetDateTime now = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime startAt = OffsetDateTime.parse("2100-12-10T10:00:00Z");

        Appointment appointment = new Appointment(
                1L, 2L, 3L, 4L, startAt, 180,
                AppointmentStatus.SCHEDULED, "nenhuma", now, now
        );

        session.setChosenAppointment(appointment.withPersistenceId(1L));
        session.setLastInteraction(now);
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);

        AvailableSlots s1 = slot(now.plusDays(3), 55L, "Ana");
        session.setSlots(List.of(s1));

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CHOOSE_SLOT(session, command(null, "INVALID"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️");
        verifyNoInteractions(rescheduleAppointmentUseCase);
    }

    @Test
    void confirm_InvalidButton_ShouldStayInConfirm_AndReturnErrorInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setChosenSlot(slot(OffsetDateTime.now().plusDays(3), 1L, "Renato"));
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CONFIRM(session, command(null, "MAYBE"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_RESCHEDULE_CONFIRM);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getInteractive()).isNotNull();
        assertThat(result.getInteractive().body()).contains("⚠️");
        verifyNoInteractions(rescheduleAppointmentUseCase);
    }

    @Test
    void confirm_NO_ShouldGoToMainMenu_AndNotCallUseCase() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CONFIRM(session, command(null, "NO"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(rescheduleAppointmentUseCase);
    }

    @Test
    void confirm_YES_ShouldCallUseCase_AndGoToMainMenu() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setRegisteredTutorName("Renato");
        session.setTutorId(10L);
        session.setChosenAppointmentId(1L);
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);

        AvailableSlots chosen = slot(OffsetDateTime.now().plusDays(5), 88L, "Ana");
        session.setChosenSlot(chosen);

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CONFIRM(session, command(null, "YES"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verify(rescheduleAppointmentUseCase, times(1)).execute(any(RescheduleAppointmentCommand.class));
    }
}
