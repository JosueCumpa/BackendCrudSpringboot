package com.crudpersonas.app.infrastructure.adapters.input.rest;

import com.crudpersonas.app.application.ports.input.PersonaUseCase;
import com.crudpersonas.app.domain.exception.DuplicateEmailException;
import com.crudpersonas.app.domain.exception.PersonaNotFoundException;
import com.crudpersonas.app.domain.model.Persona;
import com.crudpersonas.app.infrastructure.adapters.input.rest.dto.PersonaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PersonaController.class)
@Import(RestExceptionHandler.class)
@ActiveProfiles("test")
class PersonaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private PersonaUseCase personaUseCase;

        @Test
        void getAll_returnsList() throws Exception {
                given(personaUseCase.listAll()).willReturn(List.of(
                                new Persona(1L, "Juan", "juan@example.com"),
                                new Persona(2L, "Ana", "ana@example.com")));

                mockMvc.perform(get("/api/personas"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                                .andExpect(jsonPath("$[1].email").value("ana@example.com"));
        }

        @Test
        void create_returnsCreated() throws Exception {
                PersonaRequest request = new PersonaRequest();
                request.setNombre("Juan");
                request.setEmail("juan@example.com");

                given(personaUseCase.create(any(Persona.class)))
                                .willReturn(new Persona(1L, "Juan", "juan@example.com"));

                mockMvc.perform(post("/api/personas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nombre").value("Juan"));
        }

        @Test
        void create_whenDuplicateEmail_returnsConflict() throws Exception {
                PersonaRequest request = new PersonaRequest();
                request.setNombre("Juan");
                request.setEmail("juan@example.com");

                given(personaUseCase.create(any(Persona.class)))
                                .willThrow(new DuplicateEmailException("juan@example.com"));

                mockMvc.perform(post("/api/personas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message")
                                                .value("El email ya esta registrado: juan@example.com"));
        }

        @Test
        void create_whenValidationFails_returnsBadRequest() throws Exception {
                PersonaRequest request = new PersonaRequest();
                request.setNombre("");
                request.setEmail("no-es-email");

                mockMvc.perform(post("/api/personas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void update_returnsOk() throws Exception {
                PersonaRequest request = new PersonaRequest();
                request.setNombre("Ana");
                request.setEmail("ana@example.com");

                given(personaUseCase.update(eq(1L), any(Persona.class)))
                                .willReturn(new Persona(1L, "Ana", "ana@example.com"));

                mockMvc.perform(put("/api/personas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Ana"));
        }

        @Test
        void update_whenNotFound_returns404() throws Exception {
                PersonaRequest request = new PersonaRequest();
                request.setNombre("Ana");
                request.setEmail("ana@example.com");

                given(personaUseCase.update(eq(99L), any(Persona.class)))
                                .willThrow(new PersonaNotFoundException(99L));

                mockMvc.perform(put("/api/personas/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Persona no encontrada con id 99"));
        }

        @Test
        void delete_returnsOk() throws Exception {
                doNothing().when(personaUseCase).delete(1L);

                mockMvc.perform(delete("/api/personas/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Persona eliminada"));
        }

        @Test
        void delete_whenNotFound_returns404() throws Exception {
                doThrow(new PersonaNotFoundException(9L)).when(personaUseCase).delete(9L);

                mockMvc.perform(delete("/api/personas/9"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Persona no encontrada con id 9"));
        }

        @Test
        void getAll_whenUnexpectedException_returns500() throws Exception {
                given(personaUseCase.listAll()).willThrow(new RuntimeException("DB down"));

                mockMvc.perform(get("/api/personas"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message")
                                                .value("Error inesperado, intente nuevamente"));
        }
        
        @Test
        void getAllPaginated_returnsPage() throws Exception {
                Persona persona = new Persona(1L, "Juan", "juan@example.com");

                PageRequest pageable = PageRequest.of(0, 10);
                Page<Persona> pageResult = new PageImpl<>(List.of(persona), pageable, 1);

                given(personaUseCase.listAll(pageable)).willReturn(pageResult);

                mockMvc.perform(get("/api/personas/page")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].nombre").value("Juan"))
                                .andExpect(jsonPath("$.totalElements").value(1));
        }
        
}
