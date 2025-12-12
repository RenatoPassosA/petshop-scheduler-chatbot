// package com.project.petshop_scheduler_chatbot.application.chat.impl;

// import java.time.OffsetDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;

// import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
// import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
// import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageUseCase;
// import com.project.petshop_scheduler_chatbot.core.domain.PetService;
// import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
// import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
// import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
// import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
// import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
// import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.ConversationSessionRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

// @Service
// public class DefaultProcessIncomingMessageUseCase implements ProcessIncomingMessageUseCase{

//     private final ConversationSessionRepository conversationSessionRepository;
//     private final AppointmentRepository appointmentRepository;
//     private final PetServiceRepository petServiceRepository;
//     private final ProfessionalTimeOffRepository professionalTimeOffRepository;
//     private final PetRepository petRepository;
//     private final TutorRepository tutorRepository;
//     private final ProfessionalRepository professionalRepository;
//     private final BusinessHoursPolicy businessHoursPolicy;
//     private final TimeProvider timeProvider;

//     private final StartMenuHandler startMenuHandler;
//     private final NoRegisteredMenuHandler noRegisteredMenuHandler;

//     private final ServicesFormatedList servicesFormatedList;


//     public DefaultProcessIncomingMessageUseCase (ConversationSessionRepository conversationSessionRepository,
//                                                 AppointmentRepository appointmentRepository,
//                                                 PetServiceRepository petServiceRepository,
//                                                 ProfessionalTimeOffRepository professionalTimeOffRepository,
//                                                 PetRepository petRepository,
//                                                 TutorRepository tutorRepository,
//                                                 ProfessionalRepository professionalRepository,
//                                                 BusinessHoursPolicy businessHoursPolicy,
//                                                 TimeProvider timeProvider,
//                                             StartMenuHandler startMenuHandler,
//                                         NoRegisteredMenuHandler noRegisteredMenuHandler,
//                                     ServicesFormatedList servicesFormatedList) {
//         this.conversationSessionRepository = conversationSessionRepository;
//         this.appointmentRepository = appointmentRepository;
//         this.petServiceRepository = petServiceRepository;
//         this.professionalTimeOffRepository = professionalTimeOffRepository;
//         this.petRepository = petRepository;
//         this.tutorRepository = tutorRepository;
//         this.professionalRepository = professionalRepository;
//         this.businessHoursPolicy = businessHoursPolicy;
//         this.timeProvider = timeProvider;
//         this.startMenuHandler = startMenuHandler;
//         this.noRegisteredMenuHandler = noRegisteredMenuHandler;
//         this.servicesFormatedList = servicesFormatedList;
//     }

//     @Override
//     public ProcessIncomingMessageResult execute(ProcessIncomingMessageCommand messageCommand) {
//         validations(messageCommand);
//         ConversationSession conversationSession = getConversationSession(messageCommand.getWaId());
//         conversationSession.setLastInteraction(OffsetDateTime.now());

//         ProcessIncomingMessageResult state = processState(conversationSession, messageCommand);



//     }

//     private void validations(ProcessIncomingMessageCommand messageCommand) {
//         if (messageCommand == null)
//             throw new DomainValidationException("Dados inválidos");
//         if (messageCommand.getText() == null || messageCommand.getText().isBlank())
//             throw new DomainValidationException("Mensagem inválida");
//         if (messageCommand.getPhoneNumberId() == null || messageCommand.getPhoneNumberId().isBlank())
//             throw new DomainValidationException("Numero de telefone inválido");
//     }

//     private ConversationSession getConversationSession(String waId) {
//         Optional<ConversationSession> conversationSession = conversationSessionRepository.findByWaId(waId);
//         if (conversationSession.isEmpty())
//             return new ConversationSession(waId);
//         return conversationSession.get();

//     }
    

//     private ProcessIncomingMessageResult processState(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
//         if (conversationSession.isChatWithHuman())
//             return null;
//         return checkConversationState(conversationSession, messageCommand);

//     }

//     private ProcessIncomingMessageResult checkConversationState(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
//         switch (conversationSession.getCurrentState()) {
//             case STATE_START:
//                 return startMenuHandler.STATE_START_handler(conversationSession);

//             case STATE_NO_REGISTERED_MENU:
//                 return noRegisteredMenuHandler.STATE_NO_REGISTERED_MENU_handler(conversationSession, messageCommand);

//             case STATE_MAIN_MENU:
//                 break;

//             case STATE_REGISTER_TUTOR_NAME:
//                 break;

//             case STATE_REGISTER_TUTOR_ADDRESS:
//                 break;

//             case STATE_REGISTER_PET_NAME:
//                 break;

//             case STATE_REGISTER_PET_GENDER:
//                 break;

//             case STATE_REGISTER_PET_SIZE:
//                 break;

//             case STATE_REGISTER_PET_BREED:
//                 break;

//             case STATE_REGISTER_PET_OBS:
//                 break;

//             case STATE_CHECK_SERVICES:
//                 break;

//             case STATE_SCHEDULE_CHOOSE_PET:
//                 break;

//             case STATE_SCHEDULE_CHOOSE_SERVICE:
//                 break;

//             case STATE_SCHEDULE_CHOOSE_SLOT:
//                 break;

//             case STATE_SCHEDULE_CONFIRM:
//                 break;

//             case STATE_RESCHEDULE_CHOOSE_APPOINTMENT:
//                 break;

//             case STATE_RESCHEDULE_CHOOSE_SLOT:
//                 break;

//             case STATE_RESCHEDULE_CONFIRM:
//                 break;

//             case STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT:
//                 break;

//             case STATE_CANCEL_SCHEDULE_CONFIRM:
//                 break;

//             case STATE_CHAT_WITH_HUMAN:
//                 break;

//             default:
//                 break;
//         }
//     }

//     private ProcessIncomingMessageResult STATE_MAIN_MENU(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
//         if ("SCHEDULE".equals(messageCommand.getButtonId())) {
//             conversationSession.setCurrentState(ConversationState.STATE_SCHEDULE_CHOOSE_PET);
//             return ProcessIncomingMessageResult.text("Primeiro, me diga seu nome completo.");
//         }
//         if ("RESCHEDULE".equals(messageCommand.getButtonId())) {
//             conversationSession.setCurrentState(ConversationState.STATE_RESCHEDULE_CHOOSE_APPOINTMENT);
//             return ProcessIncomingMessageResult.text("Primeiro, me diga seu nome completo.");
//         }
//         if ("CANCEL_SCHEDULE".equals(messageCommand.getButtonId())) {
//             conversationSession.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT);
//             return ProcessIncomingMessageResult.text("Primeiro, me diga seu nome completo.");
//         }
//         if ("REGISTER_ANOTHER_PET".equals(messageCommand.getButtonId())) {
//             conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_NAME);
//             return ProcessIncomingMessageResult.text("Primeiro, me diga seu nome completo.");
//         }
//         if ("CHECK_SERVICES".equals(messageCommand.getButtonId())) {
//             String servicesList = servicesFormatedList.getAllServicesFormated();
//             conversationSession.setCurrentState(ConversationState.STATE_CHECK_SERVICES);
//             return ProcessIncomingMessageResult.text(servicesList);
//         }
//         else if ("TALK_TO_HUMAN".equals(messageCommand.getButtonId())) {
//             conversationSession.setCurrentState(ConversationState.STATE_CHAT_WITH_HUMAN);
//             conversationSession.setChatWithHuman(true);
//             return ProcessIncomingMessageResult.text("Aguarde um instante, você já será atendido.");
//         }
//         else {
//             conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
//             return ProcessIncomingMessageResult.text("Opção inválida, por favor escolha uma das opções");
//         }
//     }



















     
// }
