package com.project.petshop_scheduler_chatbot.core.domain.valueobject;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;

public final class PhoneNumber {
    private final String			phoneNumber;

    public PhoneNumber (String phoneNumber) {
        if (phoneNumber == null)
            throw new DomainValidationException("Numero de telefone inválido");
        phoneNumber = phoneNumber.trim();
        validation(phoneNumber);
        this.phoneNumber = normalization(phoneNumber);
    }

    private void validation(String phoneNumber) {
        if (phoneNumber.isEmpty())
            throw new DomainValidationException("Numero de telefone inválido");
    }

    private void checkRange(String phoneNumber) {
        if (phoneNumber.length() < 8 || phoneNumber.length() > 15)
            throw new DomainValidationException("Numero de telefone inválido");
    }

    private String normalization(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\D", "");
        phoneNumber = phoneNumber.replaceAll("^0+", "");
        if (phoneNumber.startsWith("55"))
            phoneNumber = "+" + phoneNumber;
        else
            phoneNumber = "+55" + phoneNumber;
        checkRange(phoneNumber);
        return phoneNumber;
    }

    public String value() {
        return phoneNumber;
    }

}
