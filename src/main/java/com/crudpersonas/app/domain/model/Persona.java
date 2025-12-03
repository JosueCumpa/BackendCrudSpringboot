package com.crudpersonas.app.domain.model;

public class Persona {

    private final Long id;
    private final String nombre;
    private final String email;

    public Persona(Long id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public Persona withNombreAndEmail(String nombre, String email) {
        return new Persona(this.id, nombre, email);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
}

   

