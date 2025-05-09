package com.example.donation.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super("E-mail não cadastrado: " + email);
    }
}
