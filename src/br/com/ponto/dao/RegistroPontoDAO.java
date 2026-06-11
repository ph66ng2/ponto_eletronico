package br.com.ponto.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ponto.config.DBConfig;
import br.com.ponto.connection.ConectaPostgres;
import br.com.ponto.model.RegistroPonto;

public class RegistroPontoDAO {

    public void inserir(RegistroPonto r) {
        String sql = "INSERT INTO registro_ponto (id_funcionario, data, hora_entrada, observacao) VALUES (?, ?, ?, ?)";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, r.getIdFuncionario());
            stmt.setDate(2, java.sql.Date.valueOf(r.getData()));

            if (r.getHoraEntrada() != null) {
                stmt.setTime(3, java.sql.Time.valueOf(r.getHoraEntrada()));
            } else {
                stmt.setNull(3, Types.TIME);
            }

            stmt.setString(4, r.getObservacao());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                r.setIdRegistro(rs.getInt(1));
            }
            rs.close();
            stmt.close();

            System.out.println("Registro de ponto inserido com sucesso! ID: " + r.getIdRegistro());

        } catch (SQLException e) {
            System.err.println("Erro ao inserir registro de ponto: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public List<RegistroPonto> listarTodos() {
        String sql = "SELECT * FROM registro_ponto ORDER BY data DESC, id_funcionario";
        List<RegistroPonto> lista = new ArrayList<>();
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RegistroPonto r = new RegistroPonto();
                r.setIdRegistro(rs.getInt("id_registro"));
                r.setIdFuncionario(rs.getInt("id_funcionario"));
                r.setData(rs.getDate("data").toLocalDate());

                java.sql.Time horaEntrada = rs.getTime("hora_entrada");
                if (horaEntrada != null) {
                    r.setHoraEntrada(horaEntrada.toLocalTime());
                }

                java.sql.Time horaSaida = rs.getTime("hora_saida");
                if (horaSaida != null) {
                    r.setHoraSaida(horaSaida.toLocalTime());
                }

                r.setObservacao(rs.getString("observacao"));
                lista.add(r);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao listar registros de ponto: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }

        return lista;
    }

    public RegistroPonto buscarPorId(int id) {
        String sql = "SELECT * FROM registro_ponto WHERE id_registro = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                RegistroPonto r = new RegistroPonto();
                r.setIdRegistro(rs.getInt("id_registro"));
                r.setIdFuncionario(rs.getInt("id_funcionario"));
                r.setData(rs.getDate("data").toLocalDate());

                java.sql.Time horaEntrada = rs.getTime("hora_entrada");
                if (horaEntrada != null) {
                    r.setHoraEntrada(horaEntrada.toLocalTime());
                }

                java.sql.Time horaSaida = rs.getTime("hora_saida");
                if (horaSaida != null) {
                    r.setHoraSaida(horaSaida.toLocalTime());
                }

                r.setObservacao(rs.getString("observacao"));
                rs.close();
                stmt.close();
                return r;
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao buscar registro de ponto por ID: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }

        return null;
    }

    public void atualizar(RegistroPonto r) {
        String sql = "UPDATE registro_ponto SET hora_entrada = ?, hora_saida = ?, observacao = ? WHERE id_registro = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            if (r.getHoraEntrada() != null) {
                stmt.setTime(1, java.sql.Time.valueOf(r.getHoraEntrada()));
            } else {
                stmt.setNull(1, Types.TIME);
            }

            if (r.getHoraSaida() != null) {
                stmt.setTime(2, java.sql.Time.valueOf(r.getHoraSaida()));
            } else {
                stmt.setNull(2, Types.TIME);
            }

            stmt.setString(3, r.getObservacao());
            stmt.setInt(4, r.getIdRegistro());

            int linhas = stmt.executeUpdate();
            stmt.close();
            System.out.println("Registro de ponto atualizado! Linhas afetadas: " + linhas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar registro de ponto: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM registro_ponto WHERE id_registro = ?";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            stmt.close();
            if (linhas > 0) System.out.println("Registro de ponto removido com sucesso!");
            else System.out.println("Registro de ponto não encontrado.");

        } catch (SQLException e) {
            System.err.println("Erro ao remover registro de ponto: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public List<Map<String, Object>> listarRegistroCompleto() {
        String sql = "SELECT * FROM vw_registro_completo ORDER BY data DESC";

        List<Map<String, Object>> resultado = new ArrayList<>();
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=== Registros Completos (View) ===");
            System.out.printf("%-5s %-20s %-15s %-20s %-12s %-12s %-12s %-25s%n",
                    "ID", "Funcionário", "Departamento", "Cargo", "Data", "Entrada", "Saída", "Observação");
            System.out.println("=".repeat(125));

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                int idRegistro = rs.getInt("id_registro");
                String funcionario = rs.getString("funcionario");
                LocalDate data = rs.getDate("data").toLocalDate();

                java.sql.Time horaEntrada = rs.getTime("hora_entrada");
                java.sql.Time horaSaida = rs.getTime("hora_saida");
                String departamento = rs.getString("departamento");
                String cargo = rs.getString("cargo");
                String observacao = rs.getString("observacao");

                row.put("id_registro", idRegistro);
                row.put("funcionario", funcionario);
                row.put("data", data);
                row.put("hora_entrada", horaEntrada);
                row.put("hora_saida", horaSaida);
                row.put("departamento", departamento);
                row.put("cargo", cargo);
                row.put("observacao", observacao);
                resultado.add(row);

                System.out.printf("%-5d %-20s %-15s %-20s %-12s %-12s %-12s %-25s%n",
                        idRegistro,
                        funcionario,
                        departamento,
                        cargo,
                        data,
                        horaEntrada != null ? horaEntrada.toString() : "-",
                        horaSaida != null ? horaSaida.toString() : "-",
                        observacao != null ? observacao : "-");
            }

            rs.close();
            stmt.close();
            System.out.println("=".repeat(125));

        } catch (SQLException e) {
            System.err.println("Erro ao listar registros completos: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }

        return resultado;
    }

    public BigDecimal calcularHoras(int idFuncionario, String dataInicio, String dataFim, String modo) {
        String sql = "SELECT calcular_horas(?, ?::date, ?::date, ?)";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);

            stmt.setInt(1, idFuncionario);
            stmt.setString(2, dataInicio);
            stmt.setString(3, dataFim);
            stmt.setString(4, modo);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal horas = rs.getBigDecimal(1);
                if ("extras".equals(modo)) {
                    System.out.printf("Total de horas extras: %.2f horas%n", horas);
                } else {
                    System.out.printf("Total de horas trabalhadas: %.2f horas%n", horas);
                }
                rs.close();
                stmt.close();
                return horas;
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao calcular horas: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }

        return BigDecimal.ZERO;
    }

    public void registrarSaida(int idRegistro, String horaSaida) {
        String sql = "CALL registrar_saida(?, ?::time)";
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            CallableStatement stmt = banco.getConexao().prepareCall(sql);

            stmt.setInt(1, idRegistro);
            stmt.setString(2, horaSaida);
            stmt.execute();
            stmt.close();
            System.out.println("Saída registrada com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao registrar saída: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }
    }

    public List<Map<String, Object>> listarAtrasos() {
        String sql = "SELECT * FROM vw_atrasos ORDER BY data DESC";

        List<Map<String, Object>> resultado = new ArrayList<>();
        ConectaPostgres banco = new ConectaPostgres();
        banco.Conectar(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
        try {
            PreparedStatement stmt = banco.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=== Atrasos (View vw_atrasos) ===");
            System.out.printf("%-5s %-20s %-15s %-10s %-12s%n",
                    "ID", "Funcionário", "Data", "Entrada", "Min. Atraso");
            System.out.println("=".repeat(75));

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                int idRegistro = rs.getInt("id_registro");
                String funcionario = rs.getString("funcionario");
                LocalDate data = rs.getDate("data").toLocalDate();
                java.sql.Time horaEntrada = rs.getTime("hora_entrada");
                int minutosAtraso = rs.getInt("minutos_atraso");

                row.put("id_registro", idRegistro);
                row.put("funcionario", funcionario);
                row.put("data", data);
                row.put("hora_entrada", horaEntrada);
                row.put("minutos_atraso", minutosAtraso);
                resultado.add(row);

                System.out.printf("%-5d %-20s %-15s %-10s %-12d%n",
                        idRegistro,
                        funcionario,
                        data,
                        horaEntrada != null ? horaEntrada.toString() : "-",
                        minutosAtraso);
            }

            rs.close();
            stmt.close();
            System.out.println("=".repeat(75));

        } catch (SQLException e) {
            System.err.println("Erro ao listar atrasos: " + e.getMessage());
        } finally {
            banco.Desconectar();
        }

        return resultado;
    }
}
