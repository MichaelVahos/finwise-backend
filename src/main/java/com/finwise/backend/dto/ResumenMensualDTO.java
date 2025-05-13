package com.finwise.backend.dto;

import java.math.BigDecimal;

public class ResumenMensualDTO {
    private BigDecimal ingresos;
    private BigDecimal gastos;
    private BigDecimal balance;

    public ResumenMensualDTO(BigDecimal ingresos, BigDecimal gastos) {
        this.ingresos = ingresos;
        this.gastos = gastos;
        this.balance = ingresos.subtract(gastos);
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public BigDecimal getGastos() {
        return gastos;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}