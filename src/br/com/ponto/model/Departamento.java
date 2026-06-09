package br.com.ponto.model;

/**
 * Representa um departamento da organização.
 */
public class Departamento {

    private int idDepartamento;
    private String nome;
    private String descricao;

    public Departamento() {
    }

    public Departamento(int idDepartamento, String nome, String descricao) {
        this.idDepartamento = idDepartamento;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Departamento [id=" + idDepartamento + ", nome=" + nome + "]";
    }
}
