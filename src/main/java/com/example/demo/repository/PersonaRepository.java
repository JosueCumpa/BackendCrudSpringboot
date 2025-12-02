package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Persona;

import java.util.List;

@Repository
public class PersonaRepository {

    @Autowired
    private JdbcTemplate jdbc;

 public List<Persona> findAll() {
        String sql = "SELECT * FROM personas ORDER BY id ASC";
        return jdbc.query(sql, (rs, rowNum) -> {
            Persona p = new Persona();
            p.setId(rs.getLong("id"));
            p.setNombre(rs.getString("nombre"));
            p.setEmail(rs.getString("email"));
            return p;
        });
    }

    public int save(Persona p) {
        String sql = "INSERT INTO personas (nombre, email) VALUES (?, ?)";
        try {
            return jdbc.update(sql, p.getNombre(), p.getEmail());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("EMAIL_DUPLICADO");
        }
    }

    public int update(long id, Persona p) {
        String sql = "UPDATE personas SET nombre=?, email=? WHERE id=?";
        try {
            return jdbc.update(sql, p.getNombre(), p.getEmail(), id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("EMAIL_DUPLICADO");
        }
    }

    public int delete(long id) {
        String sql = "DELETE FROM personas WHERE id=?";
        return jdbc.update(sql, id);
    }
}
