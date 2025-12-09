package com.project.petshop_scheduler_chatbot.adapters.web.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.ErrorResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.error.ErrorCode;
import com.project.petshop_scheduler_chatbot.application.exceptions.AppointmentNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.AppointmentOverlapException;
import com.project.petshop_scheduler_chatbot.application.exceptions.DuplicatedPhoneNumberException;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetOverlapException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler({AppointmentNotFoundException.class,
                    TutorNotFoundException.class,
                    ProfessionalNotFoundException.class,
                    PetNotFoundException.class,
                    PetServiceNotFoundException.class}
                    )
    public ResponseEntity<ErrorResponse> handleNotFound(BusinessException exception, HttpServletRequest request) {
        ErrorCode code = (exception instanceof AppointmentNotFoundException) ? ErrorCode.APPOINTMENT_NOT_FOUND :
                        (exception instanceof TutorNotFoundException)       ? ErrorCode.TUTOR_NOT_FOUND :
                        (exception instanceof ProfessionalNotFoundException)? ErrorCode.PROFESSIONAL_NOT_FOUND :
                        (exception instanceof PetNotFoundException)         ? ErrorCode.PET_NOT_FOUND :
                        ErrorCode.SERVICE_NOT_FOUND;

        ErrorResponse body = new ErrorResponse(code.name(),
                                            exception.getMessage(),
                                            404,
                                            OffsetDateTime.now(ZoneOffset.UTC),
                                            request.getRequestURI());
        return ResponseEntity.status(404).body(body);
    }

    @ExceptionHandler({WorkingHoursOutsideException.class,
                    ProfessionalTimeOffException.class,
                    AppointmentOverlapException.class,
                    PetOverlapException.class,
                    DuplicatedPhoneNumberException.class,
                    InvalidAppointmentStateException.class})
    public ResponseEntity<ErrorResponse> handleConflict(BusinessException exception, HttpServletRequest request) {
        ErrorCode code = (exception instanceof WorkingHoursOutsideException) ? ErrorCode.WORKING_HOURS_OUTSIDE :
                        (exception instanceof ProfessionalTimeOffException) ? ErrorCode.PROFESSIONAL_TIME_OFF :
                        (exception instanceof AppointmentOverlapException) ? ErrorCode.APPOINTMENT_OVERLAP :
                        (exception instanceof PetOverlapException) ? ErrorCode.PET_OVERLAP :
                        (exception instanceof DuplicatedPhoneNumberException) ? ErrorCode.DUPLICATED_PHONE :
                        ErrorCode.INVALID_APPOINTMENT_STATE;

        ErrorResponse body = new ErrorResponse(code.name(),
                                            exception.getMessage(),
                                            409,
                                            OffsetDateTime.now(ZoneOffset.UTC),
                                            request.getRequestURI());
        return ResponseEntity.status(409).body(body);    
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessable(BusinessException exception, HttpServletRequest request) {
        ErrorCode code = ErrorCode.DOMAIN_VALIDATION_ERROR;
        ErrorResponse body = new ErrorResponse(code.name(),
                                            exception.getMessage(),
                                            422,
                                            OffsetDateTime.now(ZoneOffset.UTC),
                                            request.getRequestURI());
        return ResponseEntity.status(422).body(body); 
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationMethod(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        ErrorResponse body = new ErrorResponse(code.name(),
                                            "Erro de validação",
                                            422,
                                            OffsetDateTime.now(ZoneOffset.UTC),
                                            request.getRequestURI());
        return ResponseEntity.status(422).body(body); 
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationConstraint(ConstraintViolationException exception, HttpServletRequest request) {
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        ErrorResponse body = new ErrorResponse(code.name(),
                                            "Erro de validação",
                                            422,
                                            OffsetDateTime.now(ZoneOffset.UTC),
                                            request.getRequestURI());
        return ResponseEntity.status(422).body(body); 
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleFallback(Exception exception, HttpServletRequest request) {
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        ErrorResponse body = new ErrorResponse(code.name(),
                                            exception.getMessage(),
                                            500,
                                            OffsetDateTime.now(ZoneOffset.UTC),
                                            request.getRequestURI());
        return ResponseEntity.status(500).body(body); 
    }
}
