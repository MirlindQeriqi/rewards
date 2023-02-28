package com.example.reward.exceptions.handler;

import com.example.reward.exceptions.dto.MessageExceptionDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@Order(1)
@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public MessageExceptionDto httpMediaTypeNotSupportedExcpetion(HttpMediaTypeNotSupportedException e) {
        log.error("Http Media Type not supported", e);

        return new MessageExceptionDto("This media content type: " + e.getContentType() + ", is not supported.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageExceptionDto httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Http Request Body Not Readable null or invalid", e);

        return new MessageExceptionDto(e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public MessageExceptionDto requestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("Http Method not Allowed", e);

        return new MessageExceptionDto("Http method not allowed: " + e.getMethod());
    }
}