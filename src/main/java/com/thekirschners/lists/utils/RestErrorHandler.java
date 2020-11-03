package com.thekirschners.lists.utils;

import com.thekirschners.lists.dto.ApiErrorDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<ApiErrorDTO> illegalArgumentToBadRequst(RuntimeException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ApiErrorDTO(ex.getLocalizedMessage()));
    }

    @ExceptionHandler(value = { NoSuchElementException.class })
    protected ResponseEntity<ApiErrorDTO> noSuchElementToNotFound(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorDTO(ex.getLocalizedMessage()));
    }
}
