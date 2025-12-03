package com.crudpersonas.app.domain.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("El email ya esta registrado: " + email);
    }
}
