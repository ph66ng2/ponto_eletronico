package br.com.ponto.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ponto.connection.DBConnection;
import br.com.ponto.model.RegistroPonto;

public class RegistroPontoDAO {

    public void inserir(RegistroPonto r) {
        String sql = "INSERT INTO registro_ponto (id_funcionario, data, hora_entrada, observacao) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, r.getIdFuncionario());
            stmt.setDate(2, java.sql.Date.valueOf(r.getData()));

            if (r.getHoraEntrada() != null) {
                stmt.setTime(3, java.sql.Time.valueOf(r.getHoraEntrada()));
            } else {
                stmt.setNull(3, Types.TIME);
            }

            stmt.setString(4, r.getObservacao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setIdRegistro(rs.getInt(1));
                }
            }

            System.out.println("Registro de ponto inserido com sucesso! ID: " + r.getIdRegistro());

        } catch (SQLException e) {
            System.err.println("Erro ao inserir registro de ponto: " + e.getMessage());
        }
    }

    public List<RegistroPonto> listarTodos() {
        String sql = "SELECT * FROM registro_ponto ORDER BY data DESC, id_funcionario";
        List<RegistroPonto> lista = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

        } catch (SQLException e) {
            System.err.println("Erro ao listar registros de ponto: " + e.getMessage());
        }

        return lista;
    }

    public RegistroPonto buscarPorId(int id) {
        String sql = "SELECT * FROM registro_ponto WHERE id_registro = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
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
                    return r;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar registro de ponto por ID: " + e.getMessage());
        }

        return null;
    }

    public void atualizar(RegistroPonto r) {
        String sql = "UPDATE registro_ponto SET hora_entrada = ?, hora_saida = ?, observacao = ? WHERE id_registro = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
            System.out.println("Registro de ponto atualizado! Linhas afetadas: " + linhas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar registro de ponto: " + e.getMessage());
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM registro_ponto WHERE id_registro = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Registro de ponto removido com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao remover registro de ponto: " + e.getMessage());
        }
    }

    /**
     * Consulta a View vw_registro_completo e retorna uma lista de mapas
     * com os dados completos do registro.
     */
    public List<Map<String, Object>> listarRegistroCompleto() {
        String sql = "SELECT * FROM vw_registro_completo ORDER BY data DESC";

        List<Map<String, Object>> resultado = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Registros Completos (View) ===");
            System.out.printf("%-5s %-20s %-15s %-12s %-12s %-12s %-15s %-20s%n",
                    "ID", "Funcionário", "Data", "Entrada", "Saída", "Horas Trab.", "Total Extras", "Departamento");
            System.out.println("=".repeat(115));

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                int idRegistro = rs.getInt("id_registro");
                String nomeFuncionario = rs.getString("nome_funcionario");
                LocalDate data = rs.getDate("data").toLocalDate();

                java.sql.Time horaEntrada = rs.getTime("hora_entrada");
                java.sql.Time horaSaida = rs.getTime("hora_saida");
                BigDecimal horasTrabalhadas = rs.getBigDecimal("horas_trabalhadas");
                BigDecimal totalHorasExtras = rs.getBigDecimal("total_horas_extras");
                String nomeDepartamento = rs.getString("nome_departamento");

                row.put("id_registro", idRegistro);
                row.put("nome_funcionario", nomeFuncionario);
                row.put("data", data);
                row.put("hora_entrada", horaEntrada);
                row.put("hora_saida", horaSaida);
                row.put("horas_trabalhadas", horasTrabalhadas);
                row.put("total_horas_extras", totalHorasExtras);
                row.put("nome_departamento", nomeDepartamento);
                resultado.add(row);

                System.out.printf("%-5d %-20s %-15s %-12s %-12s %-12s %-15s %-20s%n",
                        idRegistro,
                        nomeFuncionario,
                        data,
                        horaEntrada != null ? horaEntrada.toString() : "-",
                        horaSaida != null ? horaSaida.toString() : "-",
                        horasTrabalhadas != null ? horasTrabalhadas.toString() : "-",
                        totalHorasExtras != null ? totalHorasExtras.toString() : "-",
                        nomeDepartamento);
            }

            System.out.println("=".repeat(115));

        } catch (SQLException e) {
            System.err.println("Erro ao listar registros completos: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Chama a Function calcular_horas_trabalhadas via SELECT.
     */
    public BigDecimal calcularHorasTrabalhadas(int idFuncionario, String dataInicio, String dataFim) {
        String sql = "SELECT calcular_horas_trabalhadas(?, ?::date, ?::date)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFuncionario);
            stmt.setString(2, dataInicio);
            stmt.setString(3, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal horas = rs.getBigDecimal(1);
                    System.out.printf("Total de horas trabalhadas: %.2f horas%n", horas);
                    return horas;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular horas trabalhadas: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    /**
     * Chama a Function calcular_horas_extras via SELECT.
     */
    public BigDecimal calcularHorasExtras(int idFuncionario, String dataInicio, String dataFim) {
        String sql = "SELECT calcular_horas_extras(?, ?::date, ?::date)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFuncionario);
            stmt.setString(2, dataInicio);
            stmt.setString(3, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal horas = rs.getBigDecimal(1);
                    System.out.printf("Total de horas extras: %.2f horas%n", horas);
                    return horas;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular horas extras: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    /**
     * Chama a Function calcular_minutos_atraso via SELECT.
     */
    public int calcularMinutosAtraso(int idFuncionario, String data) {
        String sql = "SELECT calcular_minutos_atraso(?, ?::date)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFuncionario);
            stmt.setString(2, data);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int minutos = rs.getInt(1);
                    System.out.printf("Minutos de atraso: %d minutos%n", minutos);
                    return minutos;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular minutos de atraso: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Chama a Procedure registrar_saida via CallableStatement.
     */
    public void registrarSaida(int idRegistro, String horaSaida) {
        String sql = "{call registrar_saida(?, ?::time)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idRegistro);
            stmt.setString(2, horaSaida);
            stmt.execute();
            System.out.println("Saída registrada com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao registrar saída: " + e.getMessage());
        }
    }

    /**
     * Chama a Procedure fechar_mes via CallableStatement.
     */
    public void fecharMes(int idDepartamento, int mes, int ano) {
        String sql = "{call fechar_mes(?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idDepartamento);
            stmt.setInt(2, mes);
            stmt.setInt(3, ano);
            stmt.execute();
            System.out.println("Mês fechado com sucesso para o departamento!");

        } catch (SQLException e) {
            System.err.println("Erro ao fechar mês: " + e.getMessage());
        }
    }

    /**
     * Consulta a View vw_atrasos e exibe os resultados.
     */
    public List<Map<String, Object>> listarAtrasos() {
        String sql = "SELECT * FROM vw_atrasos ORDER BY data DESC";

        List<Map<String, Object>> resultado = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Atrasos (View vw_atrasos) ===");
            System.out.printf("%-5s %-20s %-15s %-10s %-12s%n",
                    "ID", "Funcionário", "Data", "Entrada", "Min. Atraso");
            System.out.println("=".repeat(75));

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                int idRegistro = rs.getInt("id_registro");
                String nomeFuncionario = rs.getString("nome_funcionario");
                LocalDate data = rs.getDate("data").toLocalDate();
                java.sql.Time horaEntrada = rs.getTime("hora_entrada");
                int minutosAtraso = rs.getInt("minutos_atraso");

                row.put("id_registro", idRegistro);
                row.put("nome_funcionario", nomeFuncionario);
                row.put("data", data);
                row.put("hora_entrada", horaEntrada);
                row.put("minutos_atraso", minutosAtraso);
                resultado.add(row);

                System.out.printf("%-5d %-20s %-15s %-10s %-12d%n",
                        idRegistro,
                        nomeFuncionario,
                        data,
                        horaEntrada != null ? horaEntrada.toString() : "-",
                        minutosAtraso);
            }

            System.out.println("=".repeat(75));

        } catch (SQLException e) {
            System.err.println("Erro ao listar atrasos: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Consulta a View vw_horas_extras_mes e exibe os resultados.
     */
    public List<Map<String, Object>> listarHorasExtrasMes() {
        String sql = "SELECT * FROM vw_horas_extras_mes ORDER BY mes DESC";

        List<Map<String, Object>> resultado = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Horas Extras Mensais (View vw_horas_extras_mes) ===");
            System.out.printf("%-5s %-20s %-6s %-15s%n",
                    "ID", "Funcionário", "Mês", "Horas Extras");
            System.out.println("=".repeat(55));

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                int idFuncionario = rs.getInt("id_funcionario");
                String nomeFuncionario = rs.getString("nome_funcionario");
                String mes = rs.getString("mes");
                BigDecimal horasExtras = rs.getBigDecimal("horas_extras");

                row.put("id_funcionario", idFuncionario);
                row.put("nome_funcionario", nomeFuncionario);
                row.put("mes", mes);
                row.put("horas_extras", horasExtras);
                resultado.add(row);

                System.out.printf("%-5d %-20s %-6s %-15s%n",
                        idFuncionario,
                        nomeFuncionario,
                        mes,
                        horasExtras != null ? horasExtras.toString() : "0.00");
            }

            System.out.println("=".repeat(55));

        } catch (SQLException e) {
            System.err.println("Erro ao listar horas extras mensais: " + e.getMessage());
        }

        return resultado;
    }
}
