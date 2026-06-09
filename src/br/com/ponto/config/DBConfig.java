package br.com.ponto.config;

/**
 * Configuração de conexão com o banco de dados PostgreSQL.
 * Ajuste as constantes conforme sua instalação.
 */
public class DBConfig {
    public static final String URL      = "jdbc:postgresql://localhost:5432/ponto_eletronico";
    public static final String USER     = "paulo";
    public static final String PASSWORD = "paulo";  // Altere para sua senha

    private DBConfig() {} // classe utilitária, não instanciável
}
