package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

public class PhoneNumberTest {

    @Test
    public void createValidPhoneNumber_Sucess(){
        PhoneNumber phoneNumber = new PhoneNumber("21988398302");

        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.value() ).isEqualTo("+5521988398302");
    }

    @Test
    public void createValidPhoneNumber_SucessNoDDI(){
        PhoneNumber phoneNumber = new PhoneNumber("5521988398302");

        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.value() ).isEqualTo("+5521988398302");
    }

    @Test
    public void CreatePhoneNumber_Success_WithSpecialCharacters() {
        PhoneNumber phone = new PhoneNumber("(21)98839-8302");
        assertThat(phone.value()).isEqualTo("+5521988398302");
    }

    @Test
    public void createValidPhoneNumber_SucessTrimPhone(){
        PhoneNumber phoneNumber = new PhoneNumber("   21988398302   ");

        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.value() ).isEqualTo("+5521988398302");
    }

    @Test
    public void createValidPhoneNumber_FailNull(){
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PhoneNumber(null);
        });
        assertThat(ex.getMessage()).isEqualTo("Numero de telefone inválido");
    }

    @Test
    public void createValidPhoneNumber_FailOnlySpace(){
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PhoneNumber("          ");
        });
        assertThat(ex.getMessage()).isEqualTo("Numero de telefone inválido");
    }

    @Test
    public void createValidPhoneNumber_FailEmpty(){
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PhoneNumber("");
        });
        assertThat(ex.getMessage()).isEqualTo("Numero de telefone inválido");
    }

    @Test
    public void createValidPhoneNumber_FailTooShort(){
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PhoneNumber("2198839830");
        });
        assertThat(ex.getMessage()).isEqualTo("Numero de telefone inválido");
    }

    @Test
    public void createValidPhoneNumber_FailTooLong(){
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PhoneNumber("21988398302222222");
        });
        assertThat(ex.getMessage()).isEqualTo("Numero de telefone inválido");
    }



    

    
}
