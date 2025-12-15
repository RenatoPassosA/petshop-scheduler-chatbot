package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversation_session")
public class ConversationSessionEntity {
    @Id
    @Column(nullable = false)
    private String waId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationState currentState;

    private String registeredTutorName;

    @Column(nullable = false)
    private OffsetDateTime lastInteraction;

    @Column(nullable = false)
    private boolean chatWithHuman;

    // temporários do fluxo de agendamento
    private Long tutorId;
    private Long petId;
    private Long chosenServiceId;
    private String observations;
    private Long appointmentId;

    // dados simpels para persistir o slot escolhido
    private OffsetDateTime chosenSlotStartAt;
    private Long chosenSlotProfessionalId;

    // reagendamento
    private Long chosenAppointmentId;

    // cadastro tutor
    private String tempTutorName;
    private String tempTutorAddress;

    // cadastro pet
    private String tempPetName;
    private String tempPetGender;
    private String tempPetSize;
    private String tempPetBreed;
    private String tempPetObs;

    public ConversationSessionEntity() {}

    public String getWaId() {
        return waId;
    }

    public ConversationState getCurrentState() {
        return currentState;
    }

    public String getRegisteredTutorName() {
        return registeredTutorName;
    }

    public OffsetDateTime getLastInteraction() {
        return lastInteraction;
    }

    public boolean isChatWithHuman() {
        return chatWithHuman;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public Long getPetId() {
        return petId;
    }

    public Long getChosenServiceId() {
        return chosenServiceId;
    }

    public String getObservations() {
        return observations;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public OffsetDateTime getChosenSlotStartAt() {
        return chosenSlotStartAt;
    }

    public Long getChosenSlotProfessionalId() {
        return chosenSlotProfessionalId;
    }

    public Long getChosenAppointmentId() {
        return chosenAppointmentId;
    }

    public String getTempTutorName() {
        return tempTutorName;
    }

    public String getTempTutorAddress() {
        return tempTutorAddress;
    }

    public String getTempPetName() {
        return tempPetName;
    }

    public String getTempPetGender() {
        return tempPetGender;
    }

    public String getTempPetSize() {
        return tempPetSize;
    }

    public String getTempPetBreed() {
        return tempPetBreed;
    }

    public String getTempPetObs() {
        return tempPetObs;
    }

    public void setWaId(String waId) {
        this.waId = waId;
    }

    public void setCurrentState(ConversationState currentState) {
        this.currentState = currentState;
    }

    public void setRegisteredTutorName(String registeredTutorName) {
        this.registeredTutorName = registeredTutorName;
    }

    public void setLastInteraction(OffsetDateTime lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public void setChatWithHuman(boolean chatWithHuman) {
        this.chatWithHuman = chatWithHuman;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setChosenServiceId(Long chosenServiceId) {
        this.chosenServiceId = chosenServiceId;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setChosenSlotStartAt(OffsetDateTime chosenSlotStartAt) {
        this.chosenSlotStartAt = chosenSlotStartAt;
    }

    public void setChosenSlotProfessionalId(Long chosenSlotProfessionalId) {
        this.chosenSlotProfessionalId = chosenSlotProfessionalId;
    }

    public void setChosenAppointmentId(Long chosenAppointmentId) {
        this.chosenAppointmentId = chosenAppointmentId;
    }

    public void setTempTutorName(String tempTutorName) {
        this.tempTutorName = tempTutorName;
    }

    public void setTempTutorAddress(String tempTutorAddress) {
        this.tempTutorAddress = tempTutorAddress;
    }

    public void setTempPetName(String tempPetName) {
        this.tempPetName = tempPetName;
    }

    public void setTempPetGender(String tempPetGender) {
        this.tempPetGender = tempPetGender;
    }

    public void setTempPetSize(String tempPetSize) {
        this.tempPetSize = tempPetSize;
    }

    public void setTempPetBreed(String tempPetBreed) {
        this.tempPetBreed = tempPetBreed;
    }

    public void setTempPetObs(String tempPetObs) {
        this.tempPetObs = tempPetObs;
    }

    

    
}

