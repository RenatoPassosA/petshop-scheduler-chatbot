package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
class RescheduleHandlerTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private StartMenuHandler startMenuHandler;
    @Mock private PetRepository petRepository;
    @Mock private PetServiceRepository petServiceRepository;
    @Mock private TutorRepository tutorRepository;
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
                rescheduleAppointmentUseCase,
                tutorRepository
        );
    }

    private ProcessIncomingMessageCommand command(String text, String buttonId) {
        return new ProcessIncomingMessageCommand("21988398302", text, buttonId, "21988398302");
    }

    private AvailableSlots slots(String name) {
        return new AvailableSlots(OffsetDateTime.now(), 1L, name);
    }

    @Test
    void start_InvalidButton_ShouldGoToStart_AndDelegateStartMenu() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_START);

        ProcessIncomingMessageResult expected = ProcessIncomingMessageResult.text("expected");
        when(startMenuHandler.STATE_START_handler(session)).thenReturn(expected);

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_START(session, command(null, "INVALID"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result).isEqualTo(expected);
        verify(startMenuHandler, times(1)).STATE_START_handler(session);
        verifyNoInteractions(rescheduleAppointmentUseCase, listAvailableSlotsUseCase);
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
    void chooseAppointment_InvalidButton_ShouldStayAndReturnErrorInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);

        Appointment ap = mock(Appointment.class);
        when(ap.getId()).thenReturn(1L);
        when(ap.getPetId()).thenReturn(2L);
        when(ap.getServiceId()).thenReturn(3L);
        when(ap.getStartAt()).thenReturn(OffsetDateTime.now().plusDays(2));
        when(appointmentRepository.findByTutorId(10L)).thenReturn(List.of(ap));

        when(petRepository.findById(2L)).thenReturn(Optional.of(mock(com.project.petshop_scheduler_chatbot.core.domain.Pet.class)));
        when(petRepository.findById(2L).get().getName()).thenReturn("Rex");

        when(petServiceRepository.findById(3L)).thenReturn(Optional.of(mock(com.project.petshop_scheduler_chatbot.core.domain.PetService.class)));
        when(petServiceRepository.findById(3L).get().getName()).thenReturn("Banho");

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(session, command(null, "ABC"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Não entendi");
        verifyNoInteractions(rescheduleAppointmentUseCase, listAvailableSlotsUseCase);
    }

    @Test
    void chooseAppointment_Valid_ShouldStoreChosenAppointment_AndAskSlot() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setTutorId(10L);
        session.setLastInteraction(OffsetDateTime.now());
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);

        Appointment ap = mock(Appointment.class);
        when(ap.getId()).thenReturn(1L);
        when(ap.getServiceId()).thenReturn(3L);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(ap));

        Tutor tutor = mock(Tutor.class);
        when(tutor.getId()).thenReturn(10L);

        when(tutorRepository.findByPhone(any(PhoneNumber.class))).thenReturn(Optional.of(tutor));
        when(appointmentRepository.existsOwnership(10L, 1L)).thenReturn(true);

        AvailableSlots slot = mock(AvailableSlots.class);
        when(slot.getStartAt()).thenReturn(OffsetDateTime.now().plusDays(4));
        when(slot.getProfessionalName()).thenReturn("Ana");
        when(listAvailableSlotsUseCase.listSlots(eq(3L), any())).thenReturn(List.of(slot));

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(session, command(null, "1"));

        assertThat(session.getChosenAppointmentId()).isEqualTo(1L);
        assertThat(session.getChosenAppointment()).isNotNull();
        assertThat(session.getSlots()).isNotEmpty();
        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Para qual horário deseja reagendar?");
        verifyNoInteractions(rescheduleAppointmentUseCase);
    }

    @Test
    void chooseSlot_InvalidIndex_ShouldStayInChooseSlot_AndReturnErrorInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        OffsetDateTime now = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime startAt = OffsetDateTime.parse("2100-12-10T10:00:00Z");
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.SCHEDULED, "nenhuma", now, now);

        session.setChosenAppointment(appointment.withPersistenceId(1L));
        session.setLastInteraction(now);
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);

        AvailableSlots slot1 = slots("Renato");

        when(listAvailableSlotsUseCase.listSlots(4L, now)).thenReturn(List.of(slot1));

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CHOOSE_SLOT(session, command(null, "9"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Não entendi");
        assertThat(result.getInteractive().body()).contains("Para qual horário deseja reagendar?");
    }

    @Test
    void chooseSlot_Valid_ShouldStoreChosenSlot_AndGoToConfirm() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_SLOT);

        AvailableSlots slot = mock(AvailableSlots.class);
        when(slot.getStartAt()).thenReturn(OffsetDateTime.now().plusDays(3));
        when(slot.getProfessionalName()).thenReturn("Ana");
        session.setSlots(List.of(slot));

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CHOOSE_SLOT(session, command(null, "0"));

        assertThat(session.getChosenSlot()).isNotNull();
        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_RESCHEDULE_CONFIRM);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Podemos confirmar o reagendamento?");
    }


    @Test
    void confirm_InvalidButton_ShouldStayInConfirm_AndReturnErrorInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setChosenSlot(slots("Renato"));
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CONFIRM(session, command(null, "MAYBE"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_RESCHEDULE_CONFIRM);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
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
    void confirm_YES_ShouldCallUseCase_ResetFlow_AndGoToStart() {
        ConversationSession session = new ConversationSession("21988398302");
        session.setChosenAppointmentId(1L);
        session.setCurrentState(ConversationState.STATE_RESCHEDULE_CONFIRM);

        AvailableSlots chosen = mock(AvailableSlots.class);
        when(chosen.getStartAt()).thenReturn(OffsetDateTime.now().plusDays(5));
        session.setChosenSlot(chosen);

        ProcessIncomingMessageResult result = rescheduleHandler.handle_STATE_RESCHEDULE_CONFIRM(session, command(null, "YES"));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);
        assertThat(result.getText()).contains("Agradecemos a preferencia");
        verify(rescheduleAppointmentUseCase, times(1)).execute(any(RescheduleAppointmentCommand.class));
    }
}
