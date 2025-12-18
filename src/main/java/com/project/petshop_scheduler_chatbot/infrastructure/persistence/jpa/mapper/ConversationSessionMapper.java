package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.application.appointment.AvailableSlots;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ConversationSessionEntity;

public final class ConversationSessionMapper {

    private ConversationSessionMapper() {}

    public static ConversationSession toDomain(ConversationSessionEntity entity) {
        ConversationSession session = new ConversationSession(entity.getWaId());

        if (entity.getCurrentState() != null) {
            session.setCurrentState(entity.getCurrentState());
        } else {
            session.setCurrentState(ConversationState.STATE_START);
        }

        session.setRegisteredTutorName(entity.getRegisteredTutorName());

        if (entity.getLastInteraction() != null) {
            session.setLastInteraction(entity.getLastInteraction());
        } else {
            session.setLastInteraction(OffsetDateTime.now());
        }

        session.setChatWithHuman(entity.isChatWithHuman());

        session.setTutorId(entity.getTutorId());
        session.setPetId(entity.getPetId());
        session.setChosenServiceId(entity.getChosenServiceId());
        session.setObservations(entity.getObservations());
        session.setAppointmentId(entity.getAppointmentId());

        session.setChosenAppointmentId(entity.getChosenAppointmentId());

        session.setTempTutorName(entity.getTempTutorName());
        session.setTempTutorAddress(entity.getTempTutorAddress());

        session.setTempPetName(entity.getTempPetName());
        session.setTempPetGender(entity.getTempPetGender());
        session.setTempPetSize(entity.getTempPetSize());
        session.setTempPetBreed(entity.getTempPetBreed());
        session.setTempPetObs(entity.getTempPetObs());

        if (entity.getChosenSlotStartAt() != null && entity.getChosenSlotProfessionalId() != null) {
            AvailableSlots slot = new AvailableSlots(
                entity.getChosenSlotStartAt(),
                entity.getChosenSlotProfessionalId(),
                null);
            session.setChosenSlot(slot);
        } else {
            session.setChosenSlot(null);
        }

        session.setSlots(null);
        session.setAllTutorsPets(null);
        session.setChosenAppointment(null);

        return session;
    }

 
    public static ConversationSessionEntity toEntity(ConversationSession session) {
        ConversationSessionEntity entity = new ConversationSessionEntity();

        entity.setWaId(session.getWaId());
        entity.setCurrentState(
            session.getCurrentState() != null
                ? session.getCurrentState()
                : ConversationState.STATE_START
        );

        entity.setRegisteredTutorName(session.getRegisteredTutorName());

        entity.setLastInteraction(
            session.getLastInteraction() != null
                ? session.getLastInteraction()
                : OffsetDateTime.now()
        );

        entity.setChatWithHuman(session.isChatWithHuman());


        entity.setTutorId(session.getTutorId());
        entity.setPetId(session.getPetId());
        entity.setChosenServiceId(session.getChosenServiceId());
        entity.setObservations(session.getObservations());
        entity.setAppointmentId(session.getAppointmentId());

        entity.setChosenAppointmentId(session.getChosenAppointmentId());

        entity.setTempTutorName(session.getTempTutorName());
        entity.setTempTutorAddress(session.getTempTutorAddress());

        entity.setTempPetName(session.getTempPetName());
        entity.setTempPetGender(session.getTempPetGender());
        entity.setTempPetSize(session.getTempPetSize());
        entity.setTempPetBreed(session.getTempPetBreed());
        entity.setTempPetObs(session.getTempPetObs());

        if (session.getChosenSlot() != null) {
            entity.setChosenSlotStartAt(session.getChosenSlot().getStartAt());
            entity.setChosenSlotProfessionalId(session.getChosenSlot().getProfessionalId());
        } else {
            entity.setChosenSlotStartAt(null);
            entity.setChosenSlotProfessionalId(null);
        }

        return entity;
    }
}
