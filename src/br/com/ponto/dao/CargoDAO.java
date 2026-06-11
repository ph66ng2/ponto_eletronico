package br.com.ponto.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.ponto.config.DBConfig;
import br.com.ponto.connection.ConectaPostgres;
import br.com.ponto.model.Cargo;

public class CargoDAO {

    public void inserir(Cargo c) {
        String sql = "INSERT INTO cargo (titulo, salario_base) VALUES (?, ?)";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, c.getTitulo());
            stmt.setBigDecimal(2, c.getSalarioBase());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                c.setIdCargo(rs.getInt(1));
            }
            rs.close();
            stmt.close();
            System.out.println("Cargo inserido com sucesso! ID: " + c.getIdCargo());
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cargo: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public List<Cargo> listarTodos() {
        String sql = "SELECT * FROM cargo ORDER BY id_cargo";
        List<Cargo> lista = new ArrayList<>();
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Cargo c = new Cargo();
                c.setIdCargo(rs.getInt("id_cargo"));
                c.setTitulo(rs.getString("titulo"));
                BigDecimal salarioBase = rs.getBigDecimal("salario_base");
                c.setSalarioBase(salarioBase);
                lista.add(c);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar cargos: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
        return lista;
    }

    public Cargo buscarPorId(int id) {
        String sql = "SELECT * FROM cargo WHERE id_cargo = ?";
        Cargo c = null;
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                c = new Cargo();
                c.setIdCargo(rs.getInt("id_cargo"));
                c.setTitulo(rs.getString("titulo"));
                c.setSalarioBase(rs.getBigDecimal("salario_base"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cargo por ID: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
        return c;
    }

    public void atualizar(Cargo c) {
        String sql = "UPDATE cargo SET titulo = ?, salario_base = ? WHERE id_cargo = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            stmt.setString(1, c.getTitulo());
            stmt.setBigDecimal(2, c.getSalarioBase());
            stmt.setInt(3, c.getIdCargo());
            stmt.executeUpdate();
            stmt.close();
            System.out.println("Cargo atualizado com sucesso! ID: " + c.getIdCargo());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cargo: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM cargo WHERE id_cargo = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            stmt.close();
            if (linhas > 0) System.out.println("Cargo removido com sucesso! ID: " + id);
            else System.out.println("Cargo não encontrado.");
        } catch (SQLException e) {
            System.err.println("Erro ao remover cargo: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }
}
