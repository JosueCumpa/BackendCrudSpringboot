package com.crudpersonas.app.application.service;

import com.crudpersonas.app.application.ports.output.PersonaRepositoryPort;
import com.crudpersonas.app.domain.exception.DuplicateEmailException;
import com.crudpersonas.app.domain.exception.PersonaNotFoundException;
import com.crudpersonas.app.domain.model.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @InjectMocks
    private PersonaService personaService;

    private Persona persona;

    @BeforeEach
    void setUp() {
        persona = new Persona(1L, "Juan", "juan@example.com");
    }

    @Test
    void listAll_returnsData() {
        doReturn(List.of(persona)).when(personaRepositoryPort).findAll();

        List<Persona> result = personaService.listAll();

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getNombre());
    }

    @Test
    void create_throwsWhenEmailDuplicate() {
        doReturn(true).when(personaRepositoryPort).existsByEmail(persona.getEmail());

        assertThrows(DuplicateEmailException.class, () -> personaService.create(persona));
    }

    @Test
    void create_savesWhenEmailUnique() {
        Persona personaSinId = new Persona(null, "Juan", "juan@example.com");
        doReturn(false).when(personaRepositoryPort).existsByEmail(personaSinId.getEmail());
        doReturn(persona).when(personaRepositoryPort).save(any(Persona.class));

        Persona result = personaService.create(personaSinId);

        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
    }

    @Test
    void update_throwsWhenNotFound() {
        doReturn(Optional.empty()).when(personaRepositoryPort).findById(99L);

        assertThrows(PersonaNotFoundException.class, () -> personaService.update(99L, persona));
    }

    @Test
    void update_throwsWhenEmailDuplicate() {
        doReturn(Optional.of(persona)).when(personaRepositoryPort).findById(1L);
        doReturn(true).when(personaRepositoryPort).existsByEmailExcludingId("nuevo@example.com", 1L);

        Persona updated = new Persona(null, "Nuevo", "nuevo@example.com");
        assertThrows(DuplicateEmailException.class, () -> personaService.update(1L, updated));
    }

    @Test
    void update_succeeds() {
        doReturn(Optional.of(persona)).when(personaRepositoryPort).findById(1L);
        doReturn(false).when(personaRepositoryPort).existsByEmailExcludingId("nuevo@example.com", 1L);
        Persona persisted = new Persona(1L, "Nuevo", "nuevo@example.com");
        doReturn(persisted).when(personaRepositoryPort).save(any(Persona.class));

        Persona result = personaService.update(1L, persisted);

        assertEquals("Nuevo", result.getNombre());
        assertEquals("nuevo@example.com", result.getEmail());
    }

    @Test
    void delete_throwsWhenNotFound() {
        doReturn(Optional.empty()).when(personaRepositoryPort).findById(1L);

        assertThrows(PersonaNotFoundException.class, () -> personaService.delete(1L));
    }

    @Test
    void delete_succeeds() {
        doReturn(Optional.of(persona)).when(personaRepositoryPort).findById(1L);
        doNothing().when(personaRepositoryPort).deleteById(1L);

        personaService.delete(1L);

        verify(personaRepositoryPort).deleteById(1L);
        verifyNoMoreInteractions(personaRepositoryPort);
    }
}
