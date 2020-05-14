package com.sree.rampup.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic error Handler for thrown exceptions.
 */
@ControllerAdvice
@RestController
public class RestErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class})
    public Map<String, String> handleEntityNotFoundExceptions(EntityNotFoundException ex) {
        Map<String, String>  map = new HashMap<>();
        map.put("ID", "UUID Not Found!");
        return  map;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({javax.persistence.EntityNotFoundException.class})
    public Map<String, String> handleEntityNotFoundExceptions(javax.persistence.EntityNotFoundException ex) {
        Map<String, String>  map = new HashMap<>();
        map.put("ID", "UUID Not Found!");
        return  map;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({EntityAlreadyExistsException.class})
    public Map<String, String> handleEntityNotFoundExceptions(EntityAlreadyExistsException ex) {
        Map<String, String>  map = new HashMap<>();
        map.put("ID", "Record already exists!");
        return  map;
    }
}