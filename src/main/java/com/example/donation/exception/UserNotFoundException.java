package com.example.donation.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário não encontrado: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
