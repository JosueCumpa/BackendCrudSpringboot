package com.crudpersonas.app.application.ports.output;

import com.crudpersonas.app.domain.model.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonaRepositoryPort {

    List<Persona> findAll();

    Page<Persona> listAll(Pageable pageable);

    Optional<Persona> findById(Long id);

    Persona save(Persona persona);

    boolean existsByEmail(String email);

    boolean existsByEmailExcludingId(String email, Long id);

    void deleteById(Long id);
}
