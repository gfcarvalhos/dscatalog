package com.gabrielsilva.dscatalog.controllers.handlers;

import com.gabrielsilva.dscatalog.dto.CustomError;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError {

    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, String path, String error, Integer status) {
        super(timestamp, path, error, status);
    }


    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }
}
