package com.finwise.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class ActualizarTransaccionRequest {

    @NotNull(message="La fecha es obligatoria")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate fecha;

    @NotBlank(message="La descripción no puede quedar vacía")
    private String descripcion;

    @NotNull(message="El monto es obligatorio")
    @Positive(message="El monto debe ser un número positivo")
    private Double monto;

    private String tipo; // "INGRESO" o "GASTO"

    public ActualizarTransaccionRequest() {}

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getMonto() {
        return monto;
    }
    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getTipo() {
        return tipo; }
    public void setTipo(String tipo) {
        this.tipo = tipo; }
}