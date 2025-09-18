package com.project.petshop_scheduler_chatbot.core.domain.valueobject;


public final class PhoneNumber {
    private final String			phoneNumber;

    public PhoneNumber (String phoneNumber) {
        validation(phoneNumber);
        this.phoneNumber = normalization(phoneNumber);
    }

    private void validation(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty())
            throw new IllegalArgumentException("Numero de telefone inválido");
        if (phoneNumber.length() < 13 || phoneNumber.length() > 14)
            throw new IllegalArgumentException("Numero de telefone inválido");
    }

    private String normalization(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\D", "");
        phoneNumber = phoneNumber.replaceAll("^0+", "");
        if (phoneNumber.startsWith("55"))
            phoneNumber = "+" + phoneNumber;
        else
            phoneNumber = "+55" + phoneNumber;
        validation(phoneNumber);
        return phoneNumber;
    }

    public String value() {
        return phoneNumber;
    }

}
