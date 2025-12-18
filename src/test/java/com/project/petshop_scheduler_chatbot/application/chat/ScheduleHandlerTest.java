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

@ExtendWith(MockitoExtension.class)
public class ScheduleHandlerTest {
   @Mock PetRepository petRepository;
   @Mock PetServiceRepository petServiceRepository;
   @Mock ListAvailableSlotsUseCase listAvailableSlotsUseCase; 
   @Mock ScheduleAppointmentUseCase scheduleAppointmentUseCase;
   @Mock StartMenuHandler startMenuHandler;

   private ScheduleHandler scheduleHandler;

    @BeforeEach
    void setUp() {
        scheduleHandler = new ScheduleHandler(petRepository,
                                        petServiceRepository,
                                        listAvailableSlotsUseCase,
                                        scheduleAppointmentUseCase,
                                        startMenuHandler);
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

    private AvailableSlots slots(String name) {
        return new AvailableSlots(OffsetDateTime.now(), 1L, name);
    }

    @Test
    void start_InvalidButton_ShouldGoToStart_AndDelegateStartMenu() {
        ProcessIncomingMessageCommand command = generateCommand(null, "AAAA");
        ConversationSession session = new ConversationSession(command.getWaId());

        ProcessIncomingMessageResult expect = ProcessIncomingMessageResult.text("expected");

        when(startMenuHandler.STATE_START_handler(session)).thenReturn(expect);

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result).isEqualTo(expect);
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

        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_PET);

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_PET(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_PET);
        assertThat(session.getAllTutorsPets()).contains(1L, 2L);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Não entendi");
        assertThat(result.getText()).contains("Para qual pet deseja agendar?");
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

        when(petServiceRepository.getAll()).thenReturn(List.of(service(1L, "tosa"), service(2L, "banho")));
        
        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_PET(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);
        assertThat(session.getAllTutorsPets()).contains(1L, 2L);
        assertThat(session.getPetId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Qual serviço deseja agendar?\n");
        verify(petServiceRepository, times(1)).getAll();
        verifyNoInteractions(petRepository, listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseService_InvalidButton_ShouldGeneratePetServiceButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);

        when(petServiceRepository.getAll()).thenReturn(List.of(service(1L, "tosa"), service(2L, "banho")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SERVICE(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Não entendi.");
        verify(petServiceRepository, times(1)).getAll();
        verifyNoInteractions(petRepository, listAvailableSlotsUseCase, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseService_ValidButton_ShouldGenerateSlotButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        ConversationSession session = new ConversationSession(command.getWaId());
        OffsetDateTime fixed = OffsetDateTime.parse("2100-01-01T10:00:00-03:00");
        session.setTutorId(1L);
        session.setPetId(1L);
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setLastInteraction(fixed);
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SERVICE);

        when(petServiceRepository.getAll()).thenReturn(List.of(service(1L, "tosa"), service(2L, "banho")));
        when(listAvailableSlotsUseCase.listSlots(1L, fixed)).thenReturn(List.of(slots("Renato"), slots("Amanda")));

        
        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SERVICE(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);
        assertThat(session.getChosenServiceId()).isEqualTo(1);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Para qual horário deseja agendar?");

        verify(listAvailableSlotsUseCase, times(1)).listSlots(session.getChosenServiceId(), fixed);
        verifyNoInteractions(petRepository, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseSlot_InvalidButton_ShouldGenerateSlotButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        ConversationSession session = new ConversationSession(command.getWaId());
        OffsetDateTime fixed = OffsetDateTime.parse("2100-01-01T10:00:00-03:00");
        session.setTutorId(1L);
        session.setPetId(Long.valueOf(1L));
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setLastInteraction(fixed);
        session.setChosenServiceId(1l);
        session.setSlots(List.of(slots("Renato"), slots("Amanda")));
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);

        when(listAvailableSlotsUseCase.listSlots(1L, fixed)).thenReturn(List.of(slots("Renato"), slots("Amanda")));

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SLOT(session, command);        

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("⚠️ Não entendi.");
        verify(listAvailableSlotsUseCase, times(1)).listSlots(1L, fixed);
        verifyNoInteractions(petRepository, petServiceRepository, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void chooseSlot_EmptySlotList_ShouldMessageAndReturnToMenu() {
        ProcessIncomingMessageCommand command = generateCommand(null, "1");
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setPetId(Long.valueOf(1L));
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setChosenServiceId(Long.valueOf(command.getButtonId()));
        session.setSlots(List.of());
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SLOT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(session.getChosenServiceId()).isEqualTo(1);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).contains("Não encontrei horários disponíveis nos próximos dias.");
        verifyNoInteractions(petRepository, petServiceRepository, scheduleAppointmentUseCase, startMenuHandler,listAvailableSlotsUseCase);
    }

    @Test
    void chooseSlot_ValidButton_ShouldGenerateSlotButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, "0");
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setPetId(2L);
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setChosenServiceId(3L);
        AvailableSlots slot1 = slots("Renato");
        AvailableSlots slot2 = slots("Amanda");
        session.setSlots(List.of(slot1, slot2));
        session.setChosenSlot(slot1);
        session.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_SLOT);

        ProcessIncomingMessageResult result = scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SLOT(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_SCHEDULE_OBS);

        assertThat(session.getChosenSlot()).isEqualTo(slot1);
        assertThat(result.getType()).isEqualTo(Kind.TEXT);
        assertThat(result.getText()).contains("Alguma observação importante para essa consulta?");
        verifyNoInteractions(listAvailableSlotsUseCase, petRepository, petServiceRepository, scheduleAppointmentUseCase, startMenuHandler);
    }

    @Test
    void obs_BlankOrNull_ShouldSetDefaultObsAndGenerateConfirmButtons() {
        ProcessIncomingMessageCommand command = generateCommand(null, null);
        ConversationSession session = new ConversationSession(command.getWaId());
        session.setTutorId(1L);
        session.setPetId(1L);
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setChosenServiceId(1L);
        AvailableSlots slot1 = slots("Renato");
        AvailableSlots slot2 = slots("Amanda");
        session.setSlots(List.of(slot1, slot2));
        session.setChosenSlot(slot1);
        session.setObservations("sem observações");
        session.setCurrentState(ConversationState.STATE_SCHEDULE_OBS);

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
        session.setAllTutorsPets(List.of(1L, 2L));
        session.setChosenServiceId(3L);
        session.setSlots(List.of(slots("Renato"), slots("Amanda")));
        session.setChosenSlot(slots("renato"));
        session.setObservations("ouvido inflamado");
        session.setCurrentState(ConversationState.STATE_SCHEDULE_OBS);

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


