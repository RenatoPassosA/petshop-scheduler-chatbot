// package com.project.petshop_scheduler_chatbot.application.chat.impl;

// import java.time.OffsetDateTime;
// import java.util.ArrayList;
// import java.util.List;

// import com.project.petshop_scheduler_chatbot.adapters.web.controller.AppointmentController;
// import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
// import com.project.petshop_scheduler_chatbot.application.appointment.impl.DefaultCancelAppointmentUseCase;
// import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
// import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
// import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
// import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
// import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
// import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
// import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
// import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
// import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
// import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

// public class CancelScheduleHandler {

//     private final AppointmentRepository appointmentRepository;
//     private final PetServiceRepository petServiceRepository;
//     private final PetRepository petRepository;

//     private final DefaultCancelAppointmentUseCase defaultCancelAppointmentUseCase;

//     public CancelScheduleHandler (AppointmentRepository appointmentRepository, PetServiceRepository petServiceRepository, PetRepository petRepository, DefaultCancelAppointmentUseCase defaultCancelAppointmentUseCase) {
//         this.appointmentRepository = appointmentRepository;
//         this.petServiceRepository = petServiceRepository;
//         this.petRepository = petRepository;
//         this.defaultCancelAppointmentUseCase = defaultCancelAppointmentUseCase;
//     }

//     public ProcessIncomingMessageResult handle_STATE_CANCEL_SCHEDULE_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
//         List<Appointment> appointments = appointmentRepository.findByTutorId(conversationSession.getTutorId());
//         if (appointments.isEmpty()){
//             conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
//             ProcessIncomingMessageResult.interactiveWithMessage("Você não tem nenhum serviço agendado.\n\n O que deseja fazer?\n\n",
//                                                             MenuMessages.mainMenu(conversationSession.getTempTutorName()));
//         }
//         List<ButtonOption> appointmentButtons = new ArrayList<>();
//                 for (Appointment appointmentList : appointments) {
//                     String stringIndex = appointmentList.getId().toString();
//                     String serviceName = petServiceRepository.findById(appointmentList.getServiceId()).get().getName();
//                     String petName = petRepository.findById(appointmentList.getPetId()).get().getName();
//                     String serviceDate = appointmentList.getStartAt().toString();
//                     String appointmentInfo = serviceName + " para " + petName + "dia: " + serviceDate;
//                     appointmentButtons.add(new ButtonOption(stringIndex, appointmentInfo));
//                 }
//         conversationSession.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT);
//         return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Qual serviço deseja cancelar?\n", appointmentButtons));
//     }

//     public ProcessIncomingMessageResult handle_STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
//         Long appointmentId = Long.valueOf(messageCommand.getButtonId());
//         if (check24hCancelWindow(appointmentId)) {
//             conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
//             return ProcessIncomingMessageResult.interactiveWithMessage("🚫 Infelizmente não é possível cancelar agendamente com menos de 24h de antecedência.\n\n", 
//                                                                         MenuMessages.mainMenu(conversationSession.getTempTutorName()));
//         }
//         conversationSession.setCurrentState(ConversationState.STATE_CANCEL_SCHEDULE_CONFIRM);
//         conversationSession.setAppointmentId(appointmentId);
//         String serviceName = petServiceRepository.findById(appointmentId).get().getName();
//         String petName = petRepository.findById(appointmentId).get().getName();
//         String serviceDate = appointmentRepository.findById(appointmentId).get().getStartAt().toString();
//         String cancelAppointmentInfo = serviceName + " para " + petName + " dia: " + serviceDate;
//         return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Podemos confirmar o cancelamento?\n" + cancelAppointmentInfo,
//                 List.of(new ButtonOption("YES", "SIM"),
//                     new ButtonOption("NO", "NÃO"))));  
//     }

//     public ProcessIncomingMessageResult handle_STATE_CANCEL_SCHEDULE_CONFIRM(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
//         if ("NO".equals(messageCommand.getButtonId())) {
//             conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
//             return ProcessIncomingMessageResult.interactiveWithMessage("O que deseja fazer?\n\n", MenuMessages.mainMenu(conversationSession.getTempTutorName()));
//         }
//         else {
//             Long appointmentId = conversationSession.getAppointmentId();
//             Appointment appointment = appointmentRepository.findById(appointmentId).get();
//             // CancelAppointmentCommand command = new CancelAppointmentCommand(appointmentId);
//             defaultCancelAppointmentUseCase.execute(null)
//             conversationSession.setCurrentState(ConversationState.STATE_FINISHED);
//             return ProcessIncomingMessageResult.text("Agradecemos a preferencia!\nEstamos aguardando o seu pet!");
//         }
//     }



//     private boolean check24hCancelWindow(Long appointmentId) {
//         OffsetDateTime appointmentStartDate = appointmentRepository.findById(appointmentId).get().getStartAt();
//         if (appointmentStartDate.minusHours(24).isAfter(OffsetDateTime.now()))
//             return true;
//         return false;
//     }
// }

