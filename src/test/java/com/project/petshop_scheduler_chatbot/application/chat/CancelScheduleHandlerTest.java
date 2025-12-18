package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.CancelScheduleHandler;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@ExtendWith(MockitoExtension.class)
class CancelScheduleHandlerTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PetServiceRepository petServiceRepository;
    @Mock private PetRepository petRepository;
    @Mock private CancelAppointmentUseCase cancelAppointmentUseCase;

    private CancelScheduleHandler cancelScheduleHandler;

    @BeforeEach
    void setUp() {
        cancelScheduleHandler = new CancelScheduleHandler(
                appointmentRepository,
                petServiceRepository,
                petRepository,
                cancelAppointmentUseCase
        );
    }

    private Appointment appointment(Long id) {
        OffsetDateTime startAt = OffsetDateTime.parse("2100-12-09T10:00:00Z");
        OffsetDateTime now = OffsetDateTime.now();
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.SCHEDULED, "ouvido inflamado", now, now);
        return appointment.withPersistenceId(id);
    }

    private ProcessIncomingMessageCommand generateCommand(String text, String buttonId) {
        return new ProcessIncomingMessageCommand("21988398302", text, buttonId, "950730164782242");
    }

    private Pet pet(long id, String name) {
        Pet pet = new Pet(name, Gender.F, PetSize.SMALL, "VIRA-LATA", 1L, "nenhuma", OffsetDateTime.now(), OffsetDateTime.now());
        return pet.withPersistenceId(id);

    }

    private PetService service(Long id, String name) {
        PetService service = new PetService (name, new BigDecimal(100), 180, Office.AUX, OffsetDateTime.now(), OffsetDateTime.now());
        return service.withPersistenceId(id);
    }

    @Test
    void start_InvalidButton_ShouldGoToMainMenu_AndReturnInteractive() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_START);

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Opa! Não entendi");
        verifyNoInteractions(appointmentRepository, petServiceRepository, petRepository, cancelAppointmentUseCase);
    }

    @Test
    void start_NoAppointments_ShouldGoToMainMenu_AndReturnInteractiveMessage() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "CANCEL_SCHEDULE");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_START);

        when(appointmentRepository.findByTutorId(10L)).thenReturn(List.of());

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Você não tem nenhum serviço agendado");
        verify(appointmentRepository, times(1)).findByTutorId(10L);
        verifyNoInteractions(petServiceRepository, petRepository, cancelAppointmentUseCase);
    }

    @Test
    void start_WithAppointmentsButNoValidPetOrService_ShouldGoToMainMenu() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "CANCEL_SCHEDULE");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_START);

        Appointment appointment = appointment(1L);
        appointment.setStartAt(appointment.getStartAt().plusDays(2));

        when(appointmentRepository.findByTutorId(10L)).thenReturn(List.of(appointment));
        when(petServiceRepository.findById(4L)).thenReturn(Optional.empty());
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Não encontrei agendamentos válidos");
        verifyNoInteractions(cancelAppointmentUseCase);
    }

    @Test
    void start_WithValidAppointments_ShouldGoToChooseAppointment_AndReturnButtons() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "CANCEL_SCHEDULE");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_START);

        Appointment appointment = appointment(1L);
        appointment.setStartAt(appointment.getStartAt().plusDays(2));

        when(appointmentRepository.findByTutorId(10L)).thenReturn(List.of(appointment));
        when(petServiceRepository.findById(4L)).thenReturn(Optional.of(service(4L, "Banho")));
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet(1L, "Rex")));

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Qual serviço deseja cancelar?");
        verifyNoInteractions(cancelAppointmentUseCase);
    }


    @Test
    void chooseAppointment_NonNumeric_ShouldGoToMainMenuInvalid() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT);

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Consulta inválida");
        verifyNoInteractions(cancelAppointmentUseCase);
    }

    @Test
    void chooseAppointment_NotFound_ShouldGoToMainMenuInvalid() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Consulta inválida");
        verifyNoInteractions(cancelAppointmentUseCase);
    }

    @Test
    void chooseAppointment_NotBelongsToTutor_ShouldGoToMainMenuInvalid() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");

        Appointment appointment = appointment(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Consulta inválida");
        verifyNoInteractions(cancelAppointmentUseCase);
    }

    @Test
    void chooseAppointment_LessThan24h_ShouldGoToMainMenuAndReturnCannotCancel() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");

        Appointment appointment = appointment(1L);
        appointment.setTutorId(10L);
        appointment.setStartAt(OffsetDateTime.now().plusHours(2));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(petServiceRepository.findById(4L)).thenReturn(Optional.of(service(4L, "Banho")));
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet(1L, "Rex")));

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("não é possível cancelar");
        verifyNoInteractions(cancelAppointmentUseCase);
    }

    @Test
    void chooseAppointment_ValidAndMoreThan24h_ShouldSetAppointmentId_AndGoToConfirm() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        session.setTutorId(10L);
        session.setRegisteredTutorName("Renato");
        session.setAppointmentId(1L);

        Appointment appointment = appointment(1L);
        appointment.setTutorId(10L);
        appointment.setStartAt(OffsetDateTime.now().plusDays(3));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(petServiceRepository.findById(4L)).thenReturn(Optional.of(service(4L, "Banho")));
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet(1L, "Rex")));

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_CANCEL_SCHEDULE_CONFIRM);
        assertThat(session.getAppointmentId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Podemos confirmar o cancelamento?");
        verifyNoInteractions(cancelAppointmentUseCase);
    }


    @Test
    void confirm_InvalidButton_ShouldGoToMainMenuWithError() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "INVALID");
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CONFIRM);

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Opa! Não entendi");

        verifyNoInteractions(cancelAppointmentUseCase);
    }

    @Test
    void confirm_NO_ShouldGoToMainMenu_AndNotCallUseCase() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        session.setRegisteredTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CONFIRM);

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(cancelAppointmentUseCase);
    }

    @Test
    void confirm_YES_ShouldCallUseCase_ResetFlow_AndFinish() {
        ConversationSession session = new ConversationSession("21988398302");
        ProcessIncomingMessageCommand command = generateCommand(null, "YES");
        session.setAppointmentId(1L);
        session.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CONFIRM);

        ProcessIncomingMessageResult result = cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_FINISHED);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);
        assertThat(result.getText()).contains("Agradecemos a preferencia");
        verify(cancelAppointmentUseCase, times(1)).execute(any(CancelAppointmentCommand.class));
    }
}
