package br.com.ponto.model;

import java.time.LocalDate;

/**
 * Representa um funcionário da organização.
 */
public class Funcionario {

    private int idFuncionario;
    private String nome;
    private String cpf;
    private int idDepartamento;
    private int idCargo;
    private LocalDate dataAdmissao;

    public Funcionario() {
    }

    public Funcionario(int idFuncionario, String nome, String cpf,
                       int idDepartamento, int idCargo, LocalDate dataAdmissao) {
        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.cpf = cpf;
        this.idDepartamento = idDepartamento;
        this.idCargo = idCargo;
        this.dataAdmissao = dataAdmissao;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    @Override
    public String toString() {
        return "Funcionario [id=" + idFuncionario + ", nome=" + nome + "]";
    }
}
