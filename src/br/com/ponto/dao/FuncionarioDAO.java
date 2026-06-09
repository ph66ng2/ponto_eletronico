package br.com.ponto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ponto.connection.DBConnection;
import br.com.ponto.model.Funcionario;

public class FuncionarioDAO {

    public void inserir(Funcionario f) {
        String sql = "INSERT INTO funcionario (nome, cpf, id_departamento, id_cargo, data_admissao) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, f.getNome());
            stmt.setString(2, f.getCpf());
            stmt.setInt(3, f.getIdDepartamento());
            stmt.setInt(4, f.getIdCargo());
            stmt.setDate(5, java.sql.Date.valueOf(f.getDataAdmissao()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    f.setIdFuncionario(rs.getInt(1));
                }
            }

            System.out.println("Funcionário inserido com sucesso! ID: " + f.getIdFuncionario());

        } catch (SQLException e) {
            System.err.println("Erro ao inserir funcionário: " + e.getMessage());
        }
    }

    public Funcionario buscarPorId(int id) {
        String sql = "SELECT * FROM funcionario WHERE id_funcionario = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario f = new Funcionario();
                    f.setIdFuncionario(rs.getInt("id_funcionario"));
                    f.setNome(rs.getString("nome"));
                    f.setCpf(rs.getString("cpf"));
                    f.setIdDepartamento(rs.getInt("id_departamento"));
                    f.setIdCargo(rs.getInt("id_cargo"));
                    f.setDataAdmissao(rs.getDate("data_admissao").toLocalDate());
                    return f;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário por ID: " + e.getMessage());
        }

        return null;
    }

    public List<Funcionario> listarTodos() {
        String sql = "SELECT * FROM funcionario ORDER BY id_funcionario";
        List<Funcionario> lista = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setIdFuncionario(rs.getInt("id_funcionario"));
                f.setNome(rs.getString("nome"));
                f.setCpf(rs.getString("cpf"));
                f.setIdDepartamento(rs.getInt("id_departamento"));
                f.setIdCargo(rs.getInt("id_cargo"));
                f.setDataAdmissao(rs.getDate("data_admissao").toLocalDate());
                lista.add(f);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários: " + e.getMessage());
        }

        return lista;
    }

    public void atualizar(Funcionario f) {
        String sql = "UPDATE funcionario SET nome = ?, cpf = ?, id_departamento = ?, id_cargo = ? WHERE id_funcionario = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getNome());
            stmt.setString(2, f.getCpf());
            stmt.setInt(3, f.getIdDepartamento());
            stmt.setInt(4, f.getIdCargo());
            stmt.setInt(5, f.getIdFuncionario());

            int linhas = stmt.executeUpdate();
            System.out.println("Funcionário atualizado! Linhas afetadas: " + linhas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar funcionário: " + e.getMessage());
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM funcionario WHERE id_funcionario = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Funcionário removido com sucesso!");

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                System.err.println("Não é possível remover o funcionário pois ele possui registros de ponto ou justificativas associados.");
            } else {
                System.err.println("Erro ao remover funcionário: " + e.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listarComDepartamentoECargo() {
        String sql = "SELECT f.id_funcionario, f.nome, f.cpf, d.nome AS departamento, c.titulo AS cargo, f.data_admissao "
                   + "FROM funcionario f "
                   + "JOIN departamento d ON f.id_departamento = d.id_departamento "
                   + "JOIN cargo c ON f.id_cargo = c.id_cargo "
                   + "ORDER BY f.id_funcionario";

        List<Map<String, Object>> resultado = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Funcionários com Departamento e Cargo ===");
            System.out.printf("%-5s %-20s %-15s %-20s %-20s %-15s%n",
                    "ID", "Nome", "CPF", "Departamento", "Cargo", "Data Adm.");
            System.out.println("=".repeat(95));

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id_funcionario", rs.getInt("id_funcionario"));
                row.put("nome", rs.getString("nome"));
                row.put("cpf", rs.getString("cpf"));
                row.put("departamento", rs.getString("departamento"));
                row.put("cargo", rs.getString("cargo"));
                row.put("data_admissao", rs.getDate("data_admissao").toLocalDate());
                resultado.add(row);

                System.out.printf("%-5d %-20s %-15s %-20s %-20s %-15s%n",
                        rs.getInt("id_funcionario"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("departamento"),
                        rs.getString("cargo"),
                        rs.getDate("data_admissao"));
            }

            System.out.println("=".repeat(95));

        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários com departamento e cargo: " + e.getMessage());
        }

        return resultado;
    }
}
