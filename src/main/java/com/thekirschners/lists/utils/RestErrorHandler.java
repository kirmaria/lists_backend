package com.thekirschners.lists.utils;

import com.thekirschners.lists.dto.ApiErrorDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;


@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<ApiErrorDTO> illegalArgumentToBadRequst(RuntimeException ex, WebRequest request) {
        LOGGER.error("Fatal error, returning [Bad request] ", ex);
        return ResponseEntity.badRequest().body(new ApiErrorDTO(ex.getLocalizedMessage()));
    }

    @ExceptionHandler(value = { NoSuchElementException.class })
    protected ResponseEntity<ApiErrorDTO> noSuchElementToNotFound(RuntimeException ex, WebRequest request) {
        LOGGER.error("Fatal error, returning [NotFound] ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorDTO(ex.getLocalizedMessage()));
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<ApiErrorDTO> exception(RuntimeException ex, WebRequest request) {
        LOGGER.error("Fatal error, returning [Internal server error] ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorDTO(ex.getLocalizedMessage()));
    }
}
