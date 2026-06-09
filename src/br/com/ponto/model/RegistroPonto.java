package br.com.ponto.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa o registro diário de ponto de um funcionário.
 */
public class RegistroPonto {

    private int idRegistro;
    private int idFuncionario;
    private LocalDate data;
    private LocalTime horaEntrada;
    private LocalTime horaSaida;
    private String observacao;

    public RegistroPonto() {
    }

    public RegistroPonto(int idRegistro, int idFuncionario, LocalDate data,
                         LocalTime horaEntrada, LocalTime horaSaida, String observacao) {
        this.idRegistro = idRegistro;
        this.idFuncionario = idFuncionario;
        this.data = data;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.observacao = observacao;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return "RegistroPonto [id=" + idRegistro + ", data=" + data + "]";
    }
}
