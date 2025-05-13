package com.finwise.backend.dto;

public class CategoriaResumen {
    private String categoria;
    private Double total;

    public CategoriaResumen(String categoria, Double total) {
        this.categoria = categoria;
        this.total = total;
    }

    public String getCategoria() {
        return categoria;
    }

    public Double getTotal() {
        return total;
    }
}
