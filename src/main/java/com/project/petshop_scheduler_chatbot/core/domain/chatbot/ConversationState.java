package com.project.petshop_scheduler_chatbot.core.domain.chatbot;

public enum ConversationState {
    STATE_START, //menu inicio de conversa
    STATE_NO_REGISTERED_MENU, //menu primeiro contato
    STATE_MAIN_MENU, //menu cliente recorrente
    
    //registrar novo cliente
    STATE_REGISTER_TUTOR_START,
    STATE_REGISTER_TUTOR_NAME,
    STATE_REGISTER_TUTOR_ADDRESS,
    
    //registrar novo pet
    STATE_REGISTER_PET_START,
    STATE_REGISTER_PET_NAME,
    STATE_REGISTER_PET_GENDER,
    STATE_REGISTER_PET_SIZE,
    STATE_REGISTER_PET_BREED,
    STATE_REGISTER_PET_OBS,
    STATE_REGISTER_PET_CONFIRM,

    STATE_CHECK_SERVICES, //somente ver serviços e preços

    //agendar serviço
    STATE_SCHEDULE_START,
    STATE_SCHEDULE_CHOOSE_PET,
    STATE_SCHEDULE_CHOOSE_SERVICE,
    STATE_SCHEDULE_CHOOSE_SLOT,
    STATE_SCHEDULE_CONFIRM,

    //reagendar serviço
    STATE_RESCHEDULE_START,
    STATE_RESCHEDULE_CHOOSE_APPOINTMENT,
    STATE_RESCHEDULE_CHOOSE_SLOT,
    STATE_RESCHEDULE_CONFIRM,

    //cancelar serviço
    STATE_CANCEL_SCHEDULE_START,
    STATE_CANCEL_SCHEDULE_CHOOSE_APPOINTMENT,
    STATE_CANCEL_SCHEDULE_CONFIRM,

    STATE_CHAT_WITH_HUMAN, //falar com atendente

    STATE_FINISHED
}
