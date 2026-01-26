package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.appointment.AvailableSlots;
import com.project.petshop_scheduler_chatbot.application.appointment.ListAvailableSlotsUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult.Kind;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.ScheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.StartMenuHandler;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;

@ExtendWith(MockitoExtension.class)
public class ScheduleHandlerTest {
   @Mock PetRepository petRepository;
   @Mock PetServiceRepository petServiceRepository;
   @Mock ListAvailableSlotsUseCase listAvailableSlotsUseCase; 
   @Mock ScheduleAppointmentUseCase scheduleAppointmentUseCase;
   @Mock StartMenuHandler startMenuHandler;
   @Mock ProfessionalRepository professionalRepository;

   private ScheduleHandler scheduleHandler;

    @BeforeEach
    void setUp() {
        scheduleHandler = new ScheduleHandler(
            petRepository,
            petServiceRepository,
            listAvailableSlotsUseCase,
            scheduleAppointmentUseCase,
            startMenuHandler,
            professionalRepository
        );
    }

    private ProcessIncomingMessageCommand generateCommand(String text, String buttonId) {
        return new ProcessIncomingMessageCommand("21988398302", text, buttonId, "950730164782242");
    }

    private Pet pet(long id, String name) {
        Pet pet = new Pet(name, Gender.F, PetSize.SMALL, "VIRA-LATA", 1L, "nenhuma", OffsetDateTime.now(), OffsetDateTime.now());
        return pet.withPersistenceId(id);
    }

    private PetService service(Long id, String name) {
        PetService service = new PetService(name, new BigDecimal(100), 180, Office.AUX, OffsetDateTime.now(), OffsetDateTime.now());
        return service.withPersistenceId(id);
    }

    private AvailableSlots slots(OffsetDateTime startAt, long professionalId, String name) {
        return new AvailableSlots(startAt, professionalId, name);
    }

    @Test
    void start_InvalidButton_ShouldGoToStart_AndDelegateStartMenu() {
        ProcessIncomingMessageCommand command = generateCommand(null, "AAAA");
        ConversationSession session = new ConversationSession(command.getWaId());

        when(startMenuHandler.STATE_START_handler(session)).thenReturn(ProcessIncomingMessageResult.text("expected"));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result.getType()).isEqualTo(Kind.TEXT);
        assertThat(result.getText()).isEqualTo("expected");

        verify(startMenuHandler, times(1)).STATE_START_handler(session);
        verifyNoInteractions(petRepository, petServiceRepository, listAvailableSlotsUseCase, scheduleAppointmentUseCase);
    }

    @Test
    void start_ValidButton_ListPetEmptyShouldReturnMessageAndDelegateToMainMenu() {
        ProcessIncomingMessageCommand command = generateCommand(null, "SCHEDULE");
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setCurrentState(ConversationState.STATE_START);
        session.setRegisteredTutorName("Renato");

        when(petRepository.listByTutor(session.getTutorId())).thenReturn(List.of());

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Você não tem nenhum pet cadastrado");

        verify(petRepository, times(1)).listByTutor(session.getTutorId());
        verifyNoInteractions(petServiceRepository, listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void start_ValidButton_ShouldGeneratePetButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, "SCHEDULE");
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setCurrentState(ConversationState.STATE_START);

        when(petRepository.listByTutor(1L)).thenReturn(List.of(pet(1L,"flor"), pet(2L, "kiwi")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_PET);
        assertThat(session.getAllTutorsPets()).contains(1L, 2L);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Para qual pet deseja agendar?");

        verify(petRepository, times(1)).listByTutor(session.getTutorId());
        verifyNoInteractions(petServiceRepository, listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void choosePet_InvalidButton_ShouldGeneratePetButtonsAgain() {
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_PET);

        when(petRepository.listByTutor(1L)).thenReturn(List.of(pet(1L, "flor"), pet(2L, "kiwi")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_PET(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_PET);
        assertThat(session.getAllTutorsPets()).contains(1L, 2L);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);

        // ⚠️ agora: erro no text, pergunta no body
        assertThat(result.getText()).contains("⚠️ Não entendi");
        assertThat(result.getInteractive().body()).contains("Para qual pet deseja agendar?");

        verify(petRepository, times(1)).listByTutor(session.getTutorId());
        verifyNoInteractions(petServiceRepository, listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void choosePet_ValidButton_ShouldGeneratePetServices() {
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_PET);

        // ✅ necessário: validação do petId depende disso
        when(petRepository.listByTutor(1L)).thenReturn(List.of(pet(1L,"flor"), pet(2L,"kiwi")));
        when(petServiceRepository.getAll()).thenReturn(List.of(service(1L, "tosa"), service(2L, "banho")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_PET(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);
        assertThat(session.getPetId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Qual serviço deseja agendar?");

        verify(petRepository, times(1)).listByTutor(1L);
        verify(petServiceRepository, times(1)).getAll();
        verifyNoInteractions(listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseService_InvalidButton_ShouldGeneratePetServiceButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);

        when(petServiceRepository.getAll()).thenReturn(List.of(service(1L, "tosa"), service(2L, "banho")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SERVICE(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Não entendi");

        verify(petServiceRepository, times(1)).getAll();
        verifyNoInteractions(petRepository, listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseService_ValidButton_ShouldGenerateDaysButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        ConversationSession session = new ConversationSession(command.getWaId());
        OffsetDateTime fixed = OffsetDateTime.parse("2100-01-01T10:00:00-03:00");
        session.setTutorId(1L);
        session.setPetId(1L);
        session.setLastInteraction(fixed);
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);

        when(petServiceRepository.getAll()).thenReturn(List.of(service(1L, "tosa"), service(2L, "banho")));

        // ✅ agora o fluxo pede DIAS, então listSlots deve retornar slots em dias diferentes (ou iguais)
        OffsetDateTime s1 = OffsetDateTime.parse("2100-01-02T09:00:00-03:00");
        OffsetDateTime s2 = OffsetDateTime.parse("2100-01-03T09:00:00-03:00");
        when(listAvailableSlotsUseCase.listSlots(1L, fixed))
            .thenReturn(List.of(
                slots(s1, 10L, "Renato"),
                slots(s2, 11L, "Amanda")
            ));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SERVICE(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_DAY);
        assertThat(session.getChosenServiceId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Escolha o dia");

        verify(listAvailableSlotsUseCase, times(1)).listSlots(1L, fixed);
        verifyNoInteractions(petRepository, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseSlot_InvalidButton_ShouldReturnToChooseDay() {
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        ConversationSession session = new ConversationSession(command.getWaId());
        OffsetDateTime fixed = OffsetDateTime.parse("2100-01-01T10:00:00-03:00");
        session.setTutorId(1L);
        session.setPetId(1L);
        session.setLastInteraction(fixed);
        session.setChosenServiceId(1L);
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);

        // ✅ generateDaysButtons chama listSlots
        OffsetDateTime s1 = OffsetDateTime.parse("2100-01-02T09:00:00-03:00");
        when(listAvailableSlotsUseCase.listSlots(1L, fixed)).thenReturn(List.of(slots(s1, 10L, "Renato")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SLOT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_DAY);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Não entendi");

        verify(listAvailableSlotsUseCase, times(1)).listSlots(1L, fixed);
        verifyNoInteractions(petRepository, petServiceRepository, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseSlot_EmptyOrInvalidSlotKey_ShouldReturnToChooseDay_WithError() {
        // aqui o buttonId não é SlotKey -> parse falha -> cai em generateDaysButtons
        ProcessIncomingMessageCommand command = generateCommand(null, "1");

        ConversationSession session = new ConversationSession(command.getWaId());
        OffsetDateTime fixed = OffsetDateTime.parse("2100-01-01T10:00:00-03:00");
        session.setTutorId(1L);
        session.setPetId(1L);
        session.setLastInteraction(fixed);
        session.setChosenServiceId(1L);
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);

        OffsetDateTime s1 = OffsetDateTime.parse("2100-01-02T09:00:00-03:00");
        when(listAvailableSlotsUseCase.listSlots(1L, fixed)).thenReturn(List.of(slots(s1, 10L, "Renato")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SLOT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_DAY);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️");

        verify(listAvailableSlotsUseCase, times(1)).listSlots(1L, fixed);
        verifyNoInteractions(petRepository, petServiceRepository, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseSlot_ValidSlotKey_ShouldGoToObs() {
        OffsetDateTime fixed = OffsetDateTime.parse("2100-01-01T10:00:00-03:00");

        OffsetDateTime slotStart = OffsetDateTime.parse("2100-01-02T09:00:00-03:00");
        long professionalId = 10L;
        AvailableSlots slot = slots(slotStart, professionalId, "Renato");

        ConversationSession session = new ConversationSession("21988398302");
        session.setTutorId(1L);
        session.setPetId(2L);
        session.setChosenServiceId(3L);
        session.setLastInteraction(fixed);
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);

        // ✅ o handler recalcula e encontra pelo key
        when(listAvailableSlotsUseCase.listSlots(3L, fixed)).thenReturn(List.of(slot));

        String slotKey = com.project.petshop_scheduler_chatbot.application.chat.impl.utils.SlotKeyHelper
            .toKey(slotStart, professionalId);

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SLOT(session, generateCommand(null, slotKey));

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_OBS);
        assertThat(session.getChosenSlot()).isNotNull();
        assertThat(result.getType()).isEqualTo(Kind.TEXT);
        assertThat(result.getText()).contains("Alguma observação importante");

        verify(listAvailableSlotsUseCase, times(1)).listSlots(3L, fixed);
        verifyNoInteractions(petRepository, petServiceRepository, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void obs_BlankOrNull_ShouldSetDefaultObsAndGenerateConfirmButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setPetId(1L);
        session.setChosenServiceId(1L);
        session.setChosenSlot(slots(OffsetDateTime.now().plusDays(1), 10L, "Renato"));
        session.setCurrentState(ConversationState.STATE_SCHEDULE_OBS);

        when(petRepository.findById(anyLong())).thenReturn(java.util.Optional.of(pet(1L,"flor")));
        when(petServiceRepository.findById(anyLong())).thenReturn(java.util.Optional.of(service(1L,"tosa")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_OBS(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CONFIRM);
        assertThat(session.getObservations()).contains("sem observações");
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Podemos confirmar o agendamento?");

        verify(petRepository, times(1)).findById(anyLong());
        verify(petServiceRepository, times(1)).findById(anyLong());
        verifyNoInteractions(listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void obs_ValidText_ShouldSetObsAndGenerateConfirmButtons() {
        ProcessIncomingMessageCommand command = generateCommand("ouvido inflamado", null);
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setPetId(1L);
        session.setChosenServiceId(3L);
        session.setChosenSlot(slots(OffsetDateTime.now().plusDays(1), 10L, "Renato"));
        session.setCurrentState(ConversationState.STATE_SCHEDULE_OBS);

        when(petRepository.findById(anyLong())).thenReturn(java.util.Optional.of(pet(1L,"flor")));
        when(petServiceRepository.findById(anyLong())).thenReturn(java.util.Optional.of(service(3L,"banho")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_OBS(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CONFIRM);
        assertThat(session.getObservations()).contains("ouvido inflamado");
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Podemos confirmar o agendamento?");

        verify(petRepository, times(1)).findById(anyLong());
        verify(petServiceRepository, times(1)).findById(anyLong());
        verifyNoInteractions(listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }
}
