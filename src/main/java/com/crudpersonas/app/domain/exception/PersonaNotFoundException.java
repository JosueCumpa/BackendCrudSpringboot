package com.crudpersonas.app.domain.exception;

public class PersonaNotFoundException extends RuntimeException {

    public PersonaNotFoundException(Long id) {
        super("Persona no encontrada con id " + id);
    }
}
