package com.gabrielsilva.dscatalog.services.exceptions;

@SuppressWarnings("serial")
public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}
