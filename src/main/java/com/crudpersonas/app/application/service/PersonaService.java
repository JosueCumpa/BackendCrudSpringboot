package com.crudpersonas.app.application.service;

import com.crudpersonas.app.application.ports.input.PersonaUseCase;
import com.crudpersonas.app.application.ports.output.PersonaRepositoryPort;
import com.crudpersonas.app.domain.exception.DuplicateEmailException;
import com.crudpersonas.app.domain.exception.PersonaNotFoundException;
import com.crudpersonas.app.domain.model.Persona;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PersonaService implements PersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    public PersonaService(PersonaRepositoryPort personaRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
    }

    @Override
    public List<Persona> listAll() {
        return personaRepositoryPort.findAll();
    }

    @Override
    public Persona create(Persona persona) {
        if (personaRepositoryPort.existsByEmail(persona.getEmail())) {
            throw new DuplicateEmailException(persona.getEmail());
        }

        return personaRepositoryPort.save(persona);
    }

    @Override
    public Persona update(Long id, Persona persona) {
        Persona current = personaRepositoryPort.findById(id)
                .orElseThrow(() -> new PersonaNotFoundException(id));

        if (personaRepositoryPort.existsByEmailExcludingId(persona.getEmail(), id)) {
            throw new DuplicateEmailException(persona.getEmail());
        }

        Persona toUpdate = current.withNombreAndEmail(persona.getNombre(), persona.getEmail());
        return personaRepositoryPort.save(toUpdate);
    }

    @Override
    public void delete(Long id) {
        Persona current = personaRepositoryPort.findById(id)
                .orElseThrow(() -> new PersonaNotFoundException(id));

        personaRepositoryPort.deleteById(current.getId());
    }
}
