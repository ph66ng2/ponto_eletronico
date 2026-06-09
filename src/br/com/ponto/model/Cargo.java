package br.com.ponto.model;

import java.math.BigDecimal;

/**
 * Representa um cargo com seu salário base.
 */
public class Cargo {

    private int idCargo;
    private String titulo;
    private BigDecimal salarioBase;

    public Cargo() {
    }

    public Cargo(int idCargo, String titulo, BigDecimal salarioBase) {
        this.idCargo = idCargo;
        this.titulo = titulo;
        this.salarioBase = salarioBase;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    @Override
    public String toString() {
        return "Cargo [id=" + idCargo + ", titulo=" + titulo + "]";
    }
}
