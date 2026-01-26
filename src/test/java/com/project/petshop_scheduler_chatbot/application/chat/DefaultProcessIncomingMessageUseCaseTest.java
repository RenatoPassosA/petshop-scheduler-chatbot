package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult.Kind;
import com.project.petshop_scheduler_chatbot.application.chat.impl.DefaultProcessIncomingMessageUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.CancelScheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.MainMenuHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.NoRegisteredMenuHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RegisterPetHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RegisterTutorHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RescheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.ScheduleHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.StartMenuHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.ServicesFormatedList;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ConversationSessionRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
public class DefaultProcessIncomingMessageUseCaseTest {

    @Mock private ConversationSessionRepository conversationSessionRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PetServiceRepository petServiceRepository;
    @Mock private ProfessionalTimeOffRepository professionalTimeOffRepository;
    @Mock private PetRepository petRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private ProfessionalRepository professionalRepository;
    @Mock private BusinessHoursPolicy businessHoursPolicy;
    @Mock private TimeProvider timeProvider;
    @Mock private StartMenuHandler startMenuHandler;
    @Mock private NoRegisteredMenuHandler noRegisteredMenuHandler;
    @Mock private MainMenuHandler mainMenuHandler;
    @Mock private RegisterTutorHandler registerTutorHandler;
    @Mock private RegisterPetHandler registerPetHandler;
    @Mock private ScheduleHandler scheduleHandler;
    @Mock private RescheduleHandler rescheduleHandler;
    @Mock private CancelScheduleHandler cancelScheduleHandler;
    @Mock private ServicesFormatedList servicesFormatedList;

    private ProcessIncomingMessageUseCase processIncomingMessageUseCase;

    @BeforeEach
    void setUp() {
        processIncomingMessageUseCase = new DefaultProcessIncomingMessageUseCase(conversationSessionRepository,
                                                                                appointmentRepository,
                                                                                petServiceRepository,
                                                                                professionalTimeOffRepository,
                                                                                petRepository,
                                                                                tutorRepository,
                                                                                professionalRepository,
                                                                                businessHoursPolicy,
                                                                                timeProvider,
                                                                                startMenuHandler,
                                                                                noRegisteredMenuHandler,
                                                                                mainMenuHandler,
                                                                                registerTutorHandler,
                                                                                registerPetHandler,
                                                                                scheduleHandler,
                                                                                rescheduleHandler,
                                                                                cancelScheduleHandler,
                                                                                 servicesFormatedList);
    }

    private ProcessIncomingMessageCommand generateMessageCommand(String text, String buttonId) {
        return new ProcessIncomingMessageCommand("21988398302", text, buttonId, "21988398302" );
    }

    @Test
    void execute_CommandNull_Fail_DomainValidationException() {
        assertThrows(DomainValidationException.class, () -> {
                processIncomingMessageUseCase.execute(null);
            });

        verifyNoInteractions(conversationSessionRepository);
    }

    @Test
    void execute_TextAndButtonNull_Fail_DomainValidationException() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, null);
        assertThrows(DomainValidationException.class, () -> {
                processIncomingMessageUseCase.execute(command);
            });

        verifyNoInteractions(conversationSessionRepository);
    }

    @Test
    void execute_PhoneNumberId_Fail_DomainValidationException() {
        ProcessIncomingMessageCommand command = new ProcessIncomingMessageCommand("21988398302", "oi", null, null);
        assertThrows(DomainValidationException.class, () -> {
                processIncomingMessageUseCase.execute(command);
            });

        verifyNoInteractions(conversationSessionRepository);
    }

    @Test
    void execute_InexistentSession_ShouldCreateAndSaveNewSession() {
        ProcessIncomingMessageCommand command = generateMessageCommand("oi", null);
        String waId = command.getWaId();

        when(conversationSessionRepository.findByWaId(waId)).thenReturn(Optional.empty());
        when(timeProvider.nowInUTC()).thenReturn(OffsetDateTime.now());
        when(startMenuHandler.STATE_START_handler(any())).thenReturn(ProcessIncomingMessageResult.text("Olá"));

        ProcessIncomingMessageResult result = processIncomingMessageUseCase.execute(command);

        assertNotNull(result);

        verify(conversationSessionRepository).findByWaId(waId);
        verify(conversationSessionRepository).save(any(ConversationSession.class));
        verify(startMenuHandler).STATE_START_handler(any());
    }

    @Test
    void execute_WhenSessionExistsAndStateIsStart_ShouldReturnStartMenuText() {
        ProcessIncomingMessageCommand command = generateMessageCommand("oi", null);
        String waId = command.getWaId();
        ConversationSession session = new ConversationSession(waId);

        when(conversationSessionRepository.findByWaId(waId)).thenReturn(Optional.of(session));
        when(timeProvider.nowInUTC()).thenReturn(OffsetDateTime.parse("2025-12-15T00:00:00Z"));
        when(startMenuHandler.STATE_START_handler(any())).thenReturn(ProcessIncomingMessageResult.text("Olá"));

        ProcessIncomingMessageResult result = processIncomingMessageUseCase.execute(command);

        assertNotNull(result);
        assertThat(result.getText()).isEqualTo("Olá");
        assertThat(result.getType()).isEqualTo(Kind.TEXT);

        verify(conversationSessionRepository).findByWaId(waId);
        verify(conversationSessionRepository).save(any(ConversationSession.class));
        verify(startMenuHandler).STATE_START_handler(any());
    }

    @Test
    void execute_ChatWithHumanTrue_ShouldReturnTextMessage() {
        ProcessIncomingMessageCommand command = generateMessageCommand("oi", null);
        String waId = command.getWaId();
        ConversationSession session = new ConversationSession(waId);
        session.setChatWithHuman(true);

        when(conversationSessionRepository.findByWaId(waId)).thenReturn(Optional.of(session));
        when(timeProvider.nowInUTC()).thenReturn(OffsetDateTime.parse("2025-12-15T00:00:00Z"));

        ProcessIncomingMessageResult result = processIncomingMessageUseCase.execute(command);

        assertNotNull(result);
        assertThat(result.getText()).isEqualTo("Você será atendido por um humano");
        assertThat(result.getType()).isEqualTo(Kind.TEXT);

        verify(conversationSessionRepository).findByWaId(waId);
        verify(conversationSessionRepository).save(any(ConversationSession.class));
        verifyNoInteractions(startMenuHandler,
                            noRegisteredMenuHandler,
                            mainMenuHandler,
                            registerTutorHandler,
                            registerPetHandler,
                            scheduleHandler,
                            rescheduleHandler,
                            cancelScheduleHandler);
    }

    @Test
    void execute_STATE_MAIN_MENU_ShouldCallHandler() {
        ProcessIncomingMessageCommand command = generateMessageCommand("oi", null);
        String waId = command.getWaId();
        ConversationSession session = new ConversationSession(waId);
        session.setCurrentState(ConversationState.STATE_MAIN_MENU);

        when(conversationSessionRepository.findByWaId(waId)).thenReturn(Optional.of(session));
        when(timeProvider.nowInUTC()).thenReturn(OffsetDateTime.parse("2025-12-15T00:00:00Z"));
        when(mainMenuHandler.STATE_MAIN_MENU_handler(eq(session), eq(command))).thenReturn(ProcessIncomingMessageResult.text("Olá"));

        ProcessIncomingMessageResult result = processIncomingMessageUseCase.execute(command);

        assertNotNull(result);
        assertThat(result.getText()).isEqualTo("Olá");
        assertThat(result.getType()).isEqualTo(Kind.TEXT);

        verify(conversationSessionRepository).findByWaId(waId);
        verify(mainMenuHandler).STATE_MAIN_MENU_handler(eq(session), eq(command));
        verify(conversationSessionRepository).save(eq(session));
        verifyNoInteractions(startMenuHandler,
                            noRegisteredMenuHandler,
                            registerTutorHandler,
                            registerPetHandler,
                            scheduleHandler,
                            rescheduleHandler,
                            cancelScheduleHandler);
    }

    @Test
    void execute_STATE_CHECK_SERVICES_ShouldSendServicesList_AndReturnMainMenu() {
        ProcessIncomingMessageCommand command = generateMessageCommand("oi", null);
        String waId = command.getWaId();

        ConversationSession session = new ConversationSession(waId);
        session.setCurrentState(ConversationState.STATE_CHECK_SERVICES);
        session.setRegisteredTutorName("Renato");

        when(conversationSessionRepository.findByWaId(waId)).thenReturn(Optional.of(session));
        when(timeProvider.nowInUTC()).thenReturn(OffsetDateTime.parse("2025-12-15T00:00:00Z"));
        when(servicesFormatedList.getAllServicesFormated()).thenReturn("Lista de serviços");

        ProcessIncomingMessageResult result = processIncomingMessageUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getText()).isEqualTo("Lista de serviços");
        assertThat(result.getInteractive().body()).contains("O que você deseja fazer hoje, Renato?");
        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);

        verify(conversationSessionRepository).findByWaId(waId);
        verify(servicesFormatedList).getAllServicesFormated();
        verify(conversationSessionRepository).save(session);
    }
}