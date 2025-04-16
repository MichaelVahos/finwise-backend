package com.finwise.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class ActualizarTransaccionRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;


    private String descripcion;


    private Double monto;

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
}