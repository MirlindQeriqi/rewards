package com.example.reward.exceptions.handler;

import com.example.reward.exceptions.dto.MessageExceptionDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Log4j2
@Order(2)
@RestControllerAdvice
public class ExceptionsHandler {

    private static final String DOT = ". ";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageExceptionDto generalException(Exception e) {
        log.error(e.getMessage(), e);

        return new MessageExceptionDto(e.getMessage());
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageExceptionDto numberFormatException(NumberFormatException e) {
        log.error(e.getMessage(), e);

        return new MessageExceptionDto("Invalid number value.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MessageExceptionDto notFoundException(EntityNotFoundException e) {
        return new MessageExceptionDto(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageExceptionDto constraintViolationException(ConstraintViolationException e){
        log.error(e.getMessage(), e);
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(DOT));

        return new MessageExceptionDto(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageExceptionDto methodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(DOT));

        return new MessageExceptionDto(message);
    }
}