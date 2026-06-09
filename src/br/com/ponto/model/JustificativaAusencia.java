package br.com.ponto.model;

import java.time.LocalDate;

/**
 * Representa uma justificativa de ausência de um funcionário.
 */
public class JustificativaAusencia {

    private int idJustificativa;
    private int idFuncionario;
    private LocalDate data;
    private String tipo;
    private String descricao;
    private boolean aprovada;

    public JustificativaAusencia() {
    }

    public JustificativaAusencia(int idJustificativa, int idFuncionario, LocalDate data,
                                 String tipo, String descricao, boolean aprovada) {
        this.idJustificativa = idJustificativa;
        this.idFuncionario = idFuncionario;
        this.data = data;
        this.tipo = tipo;
        this.descricao = descricao;
        this.aprovada = aprovada;
    }

    public int getIdJustificativa() {
        return idJustificativa;
    }

    public void setIdJustificativa(int idJustificativa) {
        this.idJustificativa = idJustificativa;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAprovada() {
        return aprovada;
    }

    public void setAprovada(boolean aprovada) {
        this.aprovada = aprovada;
    }

    @Override
    public String toString() {
        return "JustificativaAusencia [id=" + idJustificativa + ", tipo=" + tipo + "]";
    }
}
