package com.joaovictor.repository;

import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.Transacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TransacaoRepository {

    public void salvar(Transacao transacao) {
        String sql = """
                INSERT INTO transacoes (tipo, valor, descricao, categoria, data_transacao)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherTransacao(stmt, transacao);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar transação.", e);
        }
    }

    public void salvarEAjustarSaldo(Transacao transacao, long perfilId, BigDecimal novoSaldo) {
        String sqlTransacao = """
                INSERT INTO transacoes (tipo, valor, descricao, categoria, data_transacao)
                VALUES (?, ?, ?, ?, ?)
                """;
        String sqlSaldo = "UPDATE perfil_financeiro SET saldo_atual = ? WHERE id = ?";

        try (Connection conn = Conexao.abrir()) {
            boolean autoCommitAnterior = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtTransacao = conn.prepareStatement(sqlTransacao);
                 PreparedStatement stmtSaldo = conn.prepareStatement(sqlSaldo)) {

                preencherTransacao(stmtTransacao, transacao);
                stmtTransacao.executeUpdate();

                stmtSaldo.setBigDecimal(1, novoSaldo);
                stmtSaldo.setLong(2, perfilId);

                if (stmtSaldo.executeUpdate() == 0) {
                    throw new SQLException("Perfil financeiro não encontrado para atualização do saldo.");
                }

                conn.commit();
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackErro) {
                    e.addSuppressed(rollbackErro);
                }
                throw e;
            } finally {
                conn.setAutoCommit(autoCommitAnterior);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar transação e atualizar saldo.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar transação e atualizar saldo.", e);
        }
    }

    public BigDecimal calcularSaldoHistorico() {
        String sql = """
                SELECT
                    COALESCE(SUM(CASE WHEN tipo = 'ENTRADA' THEN valor ELSE 0 END), 0) -
                    COALESCE(SUM(CASE WHEN tipo = 'SAIDA' THEN valor ELSE 0 END), 0) AS saldo
                FROM transacoes
                """;

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("saldo");
            }

            return BigDecimal.ZERO;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular saldo histórico.", e);
        }
    }

    public List<Transacao> buscarExtrato(int limite) {
        String sql = """
                SELECT id, tipo, valor, descricao, categoria, data_transacao
                FROM transacoes
                ORDER BY data_transacao DESC, id DESC
                LIMIT ?
                """;

        List<Transacao> lista = new ArrayList<>();

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTransacao(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar extrato.", e);
        }
    }

    public List<Transacao> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        String sql = """
                SELECT id, tipo, valor, descricao, categoria, data_transacao
                FROM transacoes
                WHERE data_transacao BETWEEN ? AND ?
                ORDER BY data_transacao DESC, id DESC
                """;

        List<Transacao> lista = new ArrayList<>();

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTransacao(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transações por período.", e);
        }
    }

    public BigDecimal totalEntradasNoMes(YearMonth mes) {
        String sql = """
                SELECT COALESCE(SUM(valor), 0) AS total
                FROM transacoes
                WHERE tipo = 'ENTRADA'
                  AND data_transacao BETWEEN ? AND ?
                """;

        return buscarTotalPorTipoNoMes(sql, mes);
    }

    public BigDecimal totalSaidasNoMes(YearMonth mes) {
        String sql = """
                SELECT COALESCE(SUM(valor), 0) AS total
                FROM transacoes
                WHERE tipo = 'SAIDA'
                  AND data_transacao BETWEEN ? AND ?
                """;

        return buscarTotalPorTipoNoMes(sql, mes);
    }

    public ResumoFinanceiro gerarResumoMensal(YearMonth mes) {
        BigDecimal entradas = totalEntradasNoMes(mes);
        BigDecimal saidas = totalSaidasNoMes(mes);
        BigDecimal saldo = entradas.subtract(saidas);

        String categoriaMaiorGasto = "Sem dados";
        BigDecimal valorMaiorGasto = BigDecimal.ZERO;

        String sqlCategoria = """
                SELECT categoria, COALESCE(SUM(valor), 0) AS total
                FROM transacoes
                WHERE tipo = 'SAIDA'
                  AND data_transacao BETWEEN ? AND ?
                GROUP BY categoria
                ORDER BY total DESC
                LIMIT 1
                """;

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sqlCategoria)) {

            stmt.setDate(1, Date.valueOf(mes.atDay(1)));
            stmt.setDate(2, Date.valueOf(mes.atEndOfMonth()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    categoriaMaiorGasto = rs.getString("categoria");
                    valorMaiorGasto = rs.getBigDecimal("total");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar resumo mensal.", e);
        }

        return new ResumoFinanceiro(
                entradas,
                saidas,
                saldo,
                categoriaMaiorGasto,
                valorMaiorGasto
        );
    }

    private BigDecimal buscarTotalPorTipoNoMes(String sql, YearMonth mes) {
        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(mes.atDay(1)));
            stmt.setDate(2, Date.valueOf(mes.atEndOfMonth()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }

            return BigDecimal.ZERO;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar total mensal.", e);
        }
    }

    private void preencherTransacao(PreparedStatement stmt, Transacao transacao) throws SQLException {
        stmt.setString(1, transacao.getTipo());
        stmt.setBigDecimal(2, transacao.getValor());
        stmt.setString(3, transacao.getDescricao());
        stmt.setString(4, transacao.getCategoria());
        stmt.setDate(5, Date.valueOf(transacao.getDataTransacao()));
    }

    private Transacao mapearTransacao(ResultSet rs) throws SQLException {
        return new Transacao(
                rs.getLong("id"),
                rs.getString("tipo"),
                rs.getBigDecimal("valor"),
                rs.getString("descricao"),
                rs.getString("categoria"),
                rs.getDate("data_transacao").toLocalDate()
        );
    }
}
