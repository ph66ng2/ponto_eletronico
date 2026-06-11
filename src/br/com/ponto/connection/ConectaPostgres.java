package br.com.ponto.connection;

import java.sql.*;

public class ConectaPostgres {

    private Connection con = null;
    private String endereco;
    private String usuario;
    private String senha;

    public void Conectar(String strEnd, String strUsuario, String strSenha) {
        endereco = strEnd;
        usuario = strUsuario;
        senha = strSenha;

        System.out.println("Tentando realizar conexão com o banco de dados...");

        try {
            //  Registrar o driver do PostgreSQL
            Class.forName("org.postgresql.Driver");

            //  Obter a conexão com o banco de dados
            con = DriverManager.getConnection(endereco, usuario, senha);

            System.out.println("Banco conectado com sucesso!");

        } catch (ClassNotFoundException cnfe) {
            System.err.println("Erro: Driver do PostgreSQL não encontrado.");
            System.err.println("Verifique se o arquivo postgresql-42.7.3.jar está em lib/");
            cnfe.printStackTrace();

        } catch (SQLException sqlex) {
            System.err.println("Erro ao conectar ao banco de dados: " + sqlex.getMessage());
            sqlex.printStackTrace();
        }
    }

    
    public Connection getConexao() {
        return con;
    }

    
    public void Desconectar() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Conexão encerrada com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao desconectar do banco: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
