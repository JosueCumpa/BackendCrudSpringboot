package com.crudpersonas.app.infrastructure.adapters.output.persistence;

import com.crudpersonas.app.domain.exception.DuplicateEmailException;
import com.crudpersonas.app.domain.model.Persona;
import com.crudpersonas.app.infrastructure.adapters.output.persistence.entity.PersonaEntity;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PersonaPersistenceAdapterTest {
    @Mock
    private PersonaJpaRepository personaJpaRepository;

    @InjectMocks
    private PersonaPersistenceAdapter adapter;

    public PersonaPersistenceAdapterTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_notFound() {
        when(personaJpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Persona> result = adapter.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmail_true() {
        when(personaJpaRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertTrue(adapter.existsByEmail("ana@example.com"));
    }

    @Test
    void existsByEmail_false() {
        when(personaJpaRepository.existsByEmail("ana@example.com")).thenReturn(false);

        assertFalse(adapter.existsByEmail("ana@example.com"));
    }

    @Test
    void deleteById_callsRepository() {
        doNothing().when(personaJpaRepository).deleteById(1L);

        adapter.deleteById(1L);

        verify(personaJpaRepository).deleteById(1L);
    }
    @Test
    void save_persistsPersonaAndMapsToDomain() {
        Persona persona = new Persona(1L, "Juan", "juan@example.com");

        PersonaEntity savedEntity = new PersonaEntity();
        savedEntity.setId(1L);
        savedEntity.setNombre("Juan");
        savedEntity.setEmail("juan@example.com");

        when(personaJpaRepository.save(any(PersonaEntity.class))).thenReturn(savedEntity);

        Persona result = adapter.save(persona);

        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
        assertEquals("juan@example.com", result.getEmail());
        verify(personaJpaRepository).save(any(PersonaEntity.class));
    }

    @Test
    void save_whenDataIntegrityViolation_throwsDuplicateEmailException() {
        Persona persona = new Persona(null, "Juan", "juan@example.com");

        when(personaJpaRepository.save(any(PersonaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(DuplicateEmailException.class, () -> adapter.save(persona));
        verify(personaJpaRepository).save(any(PersonaEntity.class));
    }

    @Test
    void findAll_returnsSortedAndMappedList() {
        PersonaEntity e1 = new PersonaEntity();
        e1.setId(1L);
        e1.setNombre("Juan");
        e1.setEmail("juan@example.com");

        PersonaEntity e2 = new PersonaEntity();
        e2.setId(2L);
        e2.setNombre("Ana");
        e2.setEmail("ana@example.com");

        when(personaJpaRepository.findAll(any(Sort.class))).thenReturn(List.of(e1, e2));

        List<Persona> result = adapter.findAll();

        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getNombre());
        assertEquals("Ana", result.get(1).getNombre());
        verify(personaJpaRepository).findAll(any(Sort.class));
    }

    @Test
    void existsByEmailExcludingId_delegatesToRepository() {
        when(personaJpaRepository.existsByEmailAndIdNot("ana@example.com", 1L))
                .thenReturn(true);

        boolean exists = adapter.existsByEmailExcludingId("ana@example.com", 1L);

        assertTrue(exists);
        verify(personaJpaRepository)
                .existsByEmailAndIdNot("ana@example.com", 1L);
    }

    @Test
    void findById_existsAndIsMappedToDomain() {
        PersonaEntity entity = new PersonaEntity();
        entity.setId(5L);
        entity.setNombre("Pame");
        entity.setEmail("pame@example.com");

        when(personaJpaRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        Optional<Persona> result = adapter.findById(5L);

        assertTrue(result.isPresent());
        assertEquals("Pame", result.get().getNombre());
        assertEquals("pame@example.com", result.get().getEmail());
    }

    @Test
    void listAll_withPageable_returnsMappedPage() {
    // Arrange
    PersonaEntity e1 = new PersonaEntity();
    e1.setId(1L);
    e1.setNombre("Juan");
    e1.setEmail("juan@example.com");

    Pageable pageable = PageRequest.of(0, 10);
    Page<PersonaEntity> entityPage =
            new PageImpl<>(List.of(e1), pageable, 1);

    when(personaJpaRepository.findAll(pageable))
            .thenReturn(entityPage);

    // Act
    Page<Persona> result = adapter.listAll(pageable);

    // Assert
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals("Juan", result.getContent().get(0).getNombre());
    assertEquals("juan@example.com", result.getContent().get(0).getEmail());

    verify(personaJpaRepository).findAll(pageable);
    }
}
