package com.finwise.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
public class Sugerencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int mes;
    private int anio;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    private LocalDateTime fechaGeneracion;

    @ManyToOne
    private Usuario usuario;

    public Sugerencia() {
    }


    public Sugerencia(Long id, int mes, int anio, String contenido, LocalDateTime fechaGeneracion, Usuario usuario) {
        this.id = id;
        this.mes = mes;
        this.anio = anio;
        this.contenido = contenido;
        this.fechaGeneracion = fechaGeneracion;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
