package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.converter;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String>{
    
    @Override
    public String convertToDatabaseColumn(PhoneNumber phone) {
        return phone == null ? null : phone.value();
    }

     @Override
    public PhoneNumber convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new PhoneNumber(dbData); // VO valida/normaliza
    }
}
