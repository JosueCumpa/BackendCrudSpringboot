package com.crudpersonas.app.infrastructure.adapters.input.rest;

import com.crudpersonas.app.application.ports.input.PersonaUseCase;
import com.crudpersonas.app.domain.model.Persona;
import com.crudpersonas.app.infrastructure.adapters.input.rest.dto.PersonaRequest;
import com.crudpersonas.app.utils.ApiResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4202"})
public class PersonaController {

    private final PersonaUseCase personaUseCase;

    public PersonaController(PersonaUseCase personaUseCase) {
        this.personaUseCase = personaUseCase;
    }

    @GetMapping
    public List<Persona> getAll() {
        return personaUseCase.listAll();
    }

    @PostMapping
    public ResponseEntity<Persona> create(@Valid @RequestBody PersonaRequest request) {
        Persona created = personaUseCase.create(toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Persona> update(@PathVariable Long id, @Valid @RequestBody PersonaRequest request) {
        Persona updated = personaUseCase.update(id, toDomain(request));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        personaUseCase.delete(id);
        return ResponseEntity.ok(new ApiResponse("Persona eliminada"));
    }

    private Persona toDomain(PersonaRequest request) {
        return new Persona(null, request.getNombre(), request.getEmail());
    }

    @GetMapping("/page")
    public ResponseEntity<?> getAllPaginated( //NOSONAR
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(personaUseCase.listAll(PageRequest.of(page, size)));
    }
}
