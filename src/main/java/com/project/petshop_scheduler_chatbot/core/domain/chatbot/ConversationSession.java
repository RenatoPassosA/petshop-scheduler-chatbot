package com.project.petshop_scheduler_chatbot.core.domain.chatbot;

import java.time.OffsetDateTime;

public class ConversationSession {
    private String              waId; //numero/ID do whatsapp
    private ConversationState   currentState;
    private Long                tutorId;
    private Long                petId;
    private Long                chosenServiceId;
    private Long                appointmentId;
    private OffsetDateTime      lastInteraction;
    private boolean             chatWithHuman;

    // Dados temporários do fluxo de cadastro de tutor
    private String              tempTutorName;
    private String              tempTutorAddress;

    // Dados temporários do fluxo de cadastro de pet
    private String              tempPetName;
    private String              tempPetGender;
    private String              tempPetSize;
    private String              tempPetBreed;
    private String              tempPetObs;

    // private String              tempServiceDate;

    public ConversationSession(String waId) {
        this.waId = waId;
        this.currentState = ConversationState.STATE_START;
        this.lastInteraction = OffsetDateTime.now();
    }

    public String getWaId() {
        return waId;
    }

    public ConversationState getCurrentState() {
        return currentState;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public Long getPetId() {
        return petId;
    }

    public Long getChoosenServiceId() {
        return chosenServiceId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public OffsetDateTime getLastInteraction() {
        return lastInteraction;
    }

    public boolean isChatWithHuman() {
        return chatWithHuman;
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

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setChoosenServiceId(Long chosenServiceId) {
        this.chosenServiceId = chosenServiceId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setLastInteraction(OffsetDateTime lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public void setChatWithHuman(boolean chatWithHuman) {
        this.chatWithHuman = chatWithHuman;
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
