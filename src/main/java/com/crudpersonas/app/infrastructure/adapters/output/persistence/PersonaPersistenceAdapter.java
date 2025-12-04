package com.crudpersonas.app.infrastructure.adapters.output.persistence;

import com.crudpersonas.app.application.ports.output.PersonaRepositoryPort;
import com.crudpersonas.app.domain.exception.DuplicateEmailException;
import com.crudpersonas.app.domain.model.Persona;
import com.crudpersonas.app.infrastructure.adapters.output.persistence.entity.PersonaEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PersonaPersistenceAdapter implements PersonaRepositoryPort {

    private final PersonaJpaRepository personaJpaRepository;

    public PersonaPersistenceAdapter(PersonaJpaRepository personaJpaRepository) {
        this.personaJpaRepository = personaJpaRepository;
    }

    @Override
    public List<Persona> findAll() {
        return personaJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Persona> listAll(Pageable pageable) {
        return personaJpaRepository
                .findAll(pageable)         // Page<PersonaEntity>
                .map(this::toDomain);      // Page<Persona>
    }

    @Override
    public Optional<Persona> findById(Long id) {
        return personaJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Persona save(Persona persona) {
        try {
            PersonaEntity entity = toEntity(persona);
            PersonaEntity saved = personaJpaRepository.save(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEmailException(persona.getEmail());
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return personaJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, Long id) {
        return personaJpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public void deleteById(Long id) {
        personaJpaRepository.deleteById(id);
    }

    private Persona toDomain(PersonaEntity entity) {
        return new Persona(entity.getId(), entity.getNombre(), entity.getEmail());
    }

    private PersonaEntity toEntity(Persona persona) {
        PersonaEntity entity = new PersonaEntity();
        entity.setId(persona.getId());
        entity.setNombre(persona.getNombre());
        entity.setEmail(persona.getEmail());
        return entity;
    }
}
