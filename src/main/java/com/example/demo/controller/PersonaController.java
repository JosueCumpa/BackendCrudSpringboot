package com.example.demo.controller;

import com.example.demo.model.Persona;
import com.example.demo.repository.PersonaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = {"http/localhost:4200",  "http://localhost:4202"})
public class PersonaController {
    private final PersonaRepository repo;

    public PersonaController(PersonaRepository repo){
        this.repo= repo;
    }

    @GetMapping
    public List<Persona> getAll(){
        return repo.findAll();
    }

    @PostMapping
    public String create(@RequestBody Persona p){
       try {
            repo.save(p);
            return "Persona creada correctamente";
        } catch (RuntimeException e) {
            if ("EMAIL_DUPLICADO".equals(e.getMessage())) {
                return "Error: el email ya está registrado";
            }
            return "Error inesperado al crear persona";
        }
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody Persona p){
        try {
            int rows = repo.update(id, p);
            if (rows == 0) return "Error: persona no encontrada";

            return "Persona actualizada correctamente";
        } catch (RuntimeException e) {
            if ("EMAIL_DUPLICADO".equals(e.getMessage())) {
                return "Error: el email ya está registrado";
            }
            return "Error inesperado al actualizar persona";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        int rows = repo.delete(id);
        return (rows > 0) ? "Persona eliminada" : "Error: persona no encontrada";
    }
}
