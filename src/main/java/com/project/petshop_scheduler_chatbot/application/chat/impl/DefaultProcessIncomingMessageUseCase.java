package com.project.petshop_scheduler_chatbot.application.chat.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.ServicesFormatedList;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ConversationSessionRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@Service
public class DefaultProcessIncomingMessageUseCase implements ProcessIncomingMessageUseCase{

    private final ConversationSessionRepository conversationSessionRepository;
    private final TimeProvider timeProvider;
    private final StartMenuHandler startMenuHandler;
    private final NoRegisteredMenuHandler noRegisteredMenuHandler;
    private final MainMenuHandler mainMenuHandler;
    private final RegisterTutorHandler registerTutorHandler;
    private final RegisterPetHandler registerPetHandler;
    private final ScheduleHandler scheduleHandler;
    private final RescheduleHandler rescheduleHandler;
    private final CancelScheduleHandler cancelScheduleHandler;

    private final ServicesFormatedList servicesFormatedList;


    public DefaultProcessIncomingMessageUseCase (ConversationSessionRepository conversationSessionRepository,
                                                AppointmentRepository appointmentRepository,
                                                PetServiceRepository petServiceRepository,
                                                ProfessionalTimeOffRepository professionalTimeOffRepository,
                                                PetRepository petRepository,
                                                TutorRepository tutorRepository,
                                                ProfessionalRepository professionalRepository,
                                                BusinessHoursPolicy businessHoursPolicy,
                                                TimeProvider timeProvider,
                                            StartMenuHandler startMenuHandler,
                                        NoRegisteredMenuHandler noRegisteredMenuHandler,
                                        MainMenuHandler mainMenuHandler,
                                        RegisterTutorHandler registerTutorHandler,
                                        RegisterPetHandler registerPetHandler,
                                        ScheduleHandler scheduleHandler,
                                        RescheduleHandler rescheduleHandler,
                                        CancelScheduleHandler cancelScheduleHandler,
                                    ServicesFormatedList servicesFormatedList) {
        this.conversationSessionRepository = conversationSessionRepository;
        this.timeProvider = timeProvider;
        this.startMenuHandler = startMenuHandler;
        this.noRegisteredMenuHandler = noRegisteredMenuHandler;
        this.mainMenuHandler = mainMenuHandler;
        this.registerTutorHandler = registerTutorHandler;
        this.registerPetHandler = registerPetHandler;
        this.scheduleHandler = scheduleHandler;
        this.rescheduleHandler = rescheduleHandler;
        this.cancelScheduleHandler = cancelScheduleHandler;
        this.servicesFormatedList = servicesFormatedList;
    }

    @Override
    public ProcessIncomingMessageResult execute(ProcessIncomingMessageCommand messageCommand) {
        validations(messageCommand);
        ConversationSession conversationSession = getConversationSession(messageCommand.getWaId());
        conversationSession.setLastInteraction(timeProvider.nowInUTC());

        ProcessIncomingMessageResult state = processState(conversationSession, messageCommand);
        conversationSessionRepository.save(conversationSession);
        return state;
    }

    private void validations(ProcessIncomingMessageCommand messageCommand) {
        if (messageCommand == null)
            throw new DomainValidationException("Dados inválidos");
        if (messageCommand.getText() == null && messageCommand.getButtonId() == null)
            throw new DomainValidationException("Mensagem inválida");
        if (messageCommand.getPhoneNumberId() == null || messageCommand.getPhoneNumberId().isBlank())
            throw new DomainValidationException("Numero de telefone inválido");
    }

    private ConversationSession getConversationSession(String waId) {
        Optional<ConversationSession> conversationSession = conversationSessionRepository.findByWaId(waId);
        if (conversationSession.isEmpty())
            return new ConversationSession(waId);
        return conversationSession.get();

    }

    private ProcessIncomingMessageResult processState(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (conversationSession.isChatWithHuman())
            return ProcessIncomingMessageResult.text("Você será atendido por um humano");
        return checkConversationState(conversationSession, messageCommand);

    }

    private ProcessIncomingMessageResult checkConversationState(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        switch (conversationSession.getCurrentState()) {
            case STATE_START:
                return startMenuHandler.STATE_START_handler(conversationSession);

            case STATE_NO_REGISTERED_MENU:
                return noRegisteredMenuHandler.STATE_NO_REGISTERED_MENU_handler(conversationSession, messageCommand);

            case STATE_MAIN_MENU:
                return mainMenuHandler.STATE_MAIN_MENU_handler(conversationSession, messageCommand);

            case STATE_REGISTER_TUTOR_START:
                return registerTutorHandler.handle_STATE_REGISTER_TUTOR_START(conversationSession, messageCommand);

            case STATE_REGISTER_TUTOR_NAME:
                return registerTutorHandler.handle_STATE_REGISTER_TUTOR_NAME(conversationSession, messageCommand);

            case STATE_REGISTER_TUTOR_ADDRESS:
                return registerTutorHandler.handle_STATE_REGISTER_TUTOR_ADDRESS(conversationSession, messageCommand);
            
            case STATE_REGISTER_TUTOR_CONFIRM:
                return registerTutorHandler.handle_STATE_REGISTER_TUTOR_CONFIRM(conversationSession, messageCommand);

            case STATE_REGISTER_PET_START:
                return registerPetHandler.handle_STATE_REGISTER_PET_START(conversationSession, messageCommand);

            case STATE_REGISTER_PET_NAME:
                return registerPetHandler.handle_STATE_REGISTER_PET_NAME(conversationSession, messageCommand);

            case STATE_REGISTER_PET_GENDER:
                return registerPetHandler.handle_STATE_REGISTER_PET_GENDER(conversationSession, messageCommand);

            case STATE_REGISTER_PET_SIZE:
                return registerPetHandler.handle_STATE_REGISTER_PET_SIZE(conversationSession, messageCommand);

            case STATE_REGISTER_PET_BREED:
                return registerPetHandler.handle_STATE_REGISTER_PET_BREED(conversationSession, messageCommand);

            case STATE_REGISTER_PET_OBS:
                return registerPetHandler.handle_STATE_REGISTER_PET_OBS(conversationSession, messageCommand);

            case STATE_REGISTER_PET_CONFIRM:
                return registerPetHandler.handle_STATE_REGISTER_PET_CONFIRM(conversationSession, messageCommand);

            case STATE_CHECK_SERVICES:
                return servicesFormatedList.sendServicesList();

            case STATE_SCHEDULE_START:
                return scheduleHandler.handle_STATE_SCHEDULE_START(conversationSession, messageCommand);
                
            case STATE_SCHEDULE_CHOOSE_PET:
                return scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_PET(conversationSession, messageCommand);

            case STATE_SCHEDULE_CHOOSE_SERVICE:
                return scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SERVICE(conversationSession, messageCommand);

            case STATE_SCHEDULE_CHOOSE_SLOT:
                return scheduleHandler.handle_STATE_SCHEDULE_CHOOSE_SLOT(conversationSession, messageCommand);

            case STATE_SCHEDULE_OBS:
                return scheduleHandler.handle_STATE_SCHEDULE_OBS(conversationSession, messageCommand);

            case STATE_SCHEDULE_CONFIRM:
                return scheduleHandler.handle_STATE_SCHEDULE_CONFIRM(conversationSession, messageCommand);

            case STATE_RESCHEDULE_START:
                return rescheduleHandler.handle_STATE_RESCHEDULE_START(conversationSession, messageCommand);
            
            case STATE_RESCHEDULE_CHOOSE_APPOINTMENT:
                return rescheduleHandler.handle_STATE_RESCHEDULE_CHOOSE_APPOINTMENT(conversationSession, messageCommand);

            case STATE_RESCHEDULE_CHOOSE_SLOT:
                return rescheduleHandler.handle_STATE_RESCHEDULE_CHOOSE_SLOT(conversationSession, messageCommand);

            case STATE_RESCHEDULE_CONFIRM:
                return rescheduleHandler.handle_STATE_RESCHEDULE_CONFIRM(conversationSession, messageCommand);

            case STATE_CANCEL_SCHEDULE_START:
                return cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_START(conversationSession, messageCommand);

            case STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT:
                 return cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(conversationSession, messageCommand);

            case STATE_CANCEL_SCHEDULE_CONFIRM:
                return cancelScheduleHandler.handle_STATE_CANCEL_SCHEDULE_CONFIRM(conversationSession, messageCommand);

            default:
                return startMenuHandler.STATE_START_handler(conversationSession);
        }
    }
 
}
