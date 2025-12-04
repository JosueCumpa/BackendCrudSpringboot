package com.crudpersonas.app.application.ports.input;

import com.crudpersonas.app.domain.model.Persona;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



import java.util.List;

public interface PersonaUseCase {

    List<Persona> listAll();

    Page<Persona> listAll(Pageable pageable);

    Persona create(Persona persona);

    Persona update(Long id, Persona persona);

    void delete(Long id);
}
