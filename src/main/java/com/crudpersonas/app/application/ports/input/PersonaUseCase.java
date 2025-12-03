package com.crudpersonas.app.application.ports.input;

import com.crudpersonas.app.domain.model.Persona;

import java.util.List;

public interface PersonaUseCase {

    List<Persona> listAll();

    Persona create(Persona persona);

    Persona update(Long id, Persona persona);

    void delete(Long id);
}
