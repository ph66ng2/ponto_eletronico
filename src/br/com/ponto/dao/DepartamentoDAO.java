package br.com.ponto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.ponto.connection.DBConnection;
import br.com.ponto.model.Departamento;

public class DepartamentoDAO {

    public void inserir(Departamento d) {
        String sql = "INSERT INTO departamento (nome, descricao) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, d.getNome());
            stmt.setString(2, d.getDescricao());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setIdDepartamento(rs.getInt(1));
                }
            }
            System.out.println("Departamento inserido com sucesso! ID: " + d.getIdDepartamento());
        } catch (SQLException e) {
            System.err.println("Erro ao inserir departamento: " + e.getMessage());
        }
    }

    public List<Departamento> listarTodos() {
        String sql = "SELECT * FROM departamento ORDER BY id_departamento";
        List<Departamento> lista = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Departamento d = new Departamento();
                d.setIdDepartamento(rs.getInt("id_departamento"));
                d.setNome(rs.getString("nome"));
                d.setDescricao(rs.getString("descricao"));
                lista.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar departamentos: " + e.getMessage());
        }
        return lista;
    }

    public Departamento buscarPorId(int id) {
        String sql = "SELECT * FROM departamento WHERE id_departamento = ?";
        Departamento d = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    d = new Departamento();
                    d.setIdDepartamento(rs.getInt("id_departamento"));
                    d.setNome(rs.getString("nome"));
                    d.setDescricao(rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar departamento por ID: " + e.getMessage());
        }
        return d;
    }

    public void atualizar(Departamento d) {
        String sql = "UPDATE departamento SET nome = ?, descricao = ? WHERE id_departamento = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, d.getNome());
            stmt.setString(2, d.getDescricao());
            stmt.setInt(3, d.getIdDepartamento());
            stmt.executeUpdate();
            System.out.println("Departamento atualizado com sucesso! ID: " + d.getIdDepartamento());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar departamento: " + e.getMessage());
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM departamento WHERE id_departamento = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Departamento removido com sucesso! ID: " + id);
        } catch (SQLException e) {
            System.err.println("Erro ao remover departamento: " + e.getMessage());
        }
    }
}
