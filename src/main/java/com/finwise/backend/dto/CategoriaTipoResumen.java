package com.finwise.backend.dto;

public class CategoriaTipoResumen {
    private String tipo;
    private String categoria;
    private Double total;

    public CategoriaTipoResumen(String tipo, String categoria, Double total) {
        this.tipo = tipo;
        this.categoria = categoria;
        this.total = total;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public Double getTotal() {
        return total;
    }
}
