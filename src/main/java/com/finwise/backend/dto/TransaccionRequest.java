package com.finwise.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finwise.backend.enums.TipoTransaccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class TransaccionRequest {

    @NotNull(message="La fecha es obligatoria")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate fecha;

    @NotBlank(message="La descripción no puede quedar vacía")
    private String descripcion;

    @NotNull(message="El monto es obligatorio")
    @Positive(message="El monto debe ser un número positivo")
    private Double monto;

    private String categoria;

    private TipoTransaccion tipo;

    public TransaccionRequest() {}

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

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}