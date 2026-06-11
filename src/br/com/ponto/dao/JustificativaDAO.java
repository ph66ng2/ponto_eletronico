package br.com.ponto.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.ponto.config.DBConfig;
import br.com.ponto.connection.ConectaPostgres;
import br.com.ponto.model.JustificativaAusencia;

public class JustificativaDAO {

    public void inserir(JustificativaAusencia j) {
        String sql = "INSERT INTO justificativa_ausencia (id_funcionario, data, tipo, descricao, aprovada) VALUES (?, ?, ?, ?, ?)";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, j.getIdFuncionario());
            stmt.setDate(2, java.sql.Date.valueOf(j.getData()));
            stmt.setString(3, j.getTipo());
            stmt.setString(4, j.getDescricao());
            stmt.setBoolean(5, j.isAprovada());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                j.setIdJustificativa(rs.getInt(1));
            }
            rs.close();
            stmt.close();

            System.out.println("Justificativa inserida com sucesso! ID: " + j.getIdJustificativa());

        } catch (SQLException e) {
            System.err.println("Erro ao inserir justificativa: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public List<JustificativaAusencia> listarTodos() {
        String sql = "SELECT * FROM justificativa_ausencia ORDER BY data DESC";
        List<JustificativaAusencia> lista = new ArrayList<>();
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JustificativaAusencia j = new JustificativaAusencia();
                j.setIdJustificativa(rs.getInt("id_justificativa"));
                j.setIdFuncionario(rs.getInt("id_funcionario"));
                j.setData(rs.getDate("data").toLocalDate());
                j.setTipo(rs.getString("tipo"));
                j.setDescricao(rs.getString("descricao"));
                j.setAprovada(rs.getBoolean("aprovada"));
                lista.add(j);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao listar justificativas: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }

        return lista;
    }

    public JustificativaAusencia buscarPorId(int id) {
        String sql = "SELECT * FROM justificativa_ausencia WHERE id_justificativa = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JustificativaAusencia j = new JustificativaAusencia();
                j.setIdJustificativa(rs.getInt("id_justificativa"));
                j.setIdFuncionario(rs.getInt("id_funcionario"));
                j.setData(rs.getDate("data").toLocalDate());
                j.setTipo(rs.getString("tipo"));
                j.setDescricao(rs.getString("descricao"));
                j.setAprovada(rs.getBoolean("aprovada"));
                rs.close();
                stmt.close();
                return j;
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao buscar justificativa por ID: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }

        return null;
    }

    public void atualizar(JustificativaAusencia j) {
        String sql = "UPDATE justificativa_ausencia SET tipo = ?, descricao = ?, aprovada = ? WHERE id_justificativa = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            stmt.setString(1, j.getTipo());
            stmt.setString(2, j.getDescricao());
            stmt.setBoolean(3, j.isAprovada());
            stmt.setInt(4, j.getIdJustificativa());

            int linhas = stmt.executeUpdate();
            stmt.close();
            System.out.println("Justificativa atualizada! Linhas afetadas: " + linhas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar justificativa: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM justificativa_ausencia WHERE id_justificativa = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            stmt.close();
            if (linhas > 0) System.out.println("Justificativa removida com sucesso!");
            else System.out.println("Justificativa não encontrada.");

        } catch (SQLException e) {
            System.err.println("Erro ao remover justificativa: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public void aprovar(int id) {
        String sql = "UPDATE justificativa_ausencia SET aprovada = TRUE WHERE id_justificativa = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
            System.out.println("Justificativa aprovada com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao aprovar justificativa: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }
}
