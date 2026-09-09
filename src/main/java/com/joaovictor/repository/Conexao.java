package com.joaovictor.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL_PADRAO =
            "jdbc:mysql://localhost:3306/banco_app" +
                    "?useSSL=false" +
                    "&serverTimezone=America/Sao_Paulo" +
                    "&allowPublicKeyRetrieval=true";

    private Conexao() {
    }

    public static Connection abrir() throws SQLException {
        String url = configuracao("DB_URL", URL_PADRAO);
        String usuario = configuracao("DB_USER", "root");
        String senha = configuracao("DB_PASSWORD", "root");

        return DriverManager.getConnection(url, usuario, senha);
    }

    private static String configuracao(String nome, String valorPadrao) {
        String valorSistema = System.getProperty(nome);
        if (valorSistema != null && !valorSistema.isBlank()) {
            return valorSistema;
        }

        String valorAmbiente = System.getenv(nome);
        if (valorAmbiente != null && !valorAmbiente.isBlank()) {
            return valorAmbiente;
        }

        return valorPadrao;
    }
}
