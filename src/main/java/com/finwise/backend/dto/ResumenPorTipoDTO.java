package com.finwise.backend.dto;

public class ResumenPorTipoDTO {
    private double totalIngresos;
    private double totalGastos;
    private double balance;

    public ResumenPorTipoDTO(double totalIngresos, double totalGastos, double balance) {
        this.totalIngresos = totalIngresos;
        this.totalGastos = totalGastos;
        this.balance = balance;
    }

    public ResumenPorTipoDTO() {

    }

    public double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public double getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(double totalGastos) {
        this.totalGastos = totalGastos;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
