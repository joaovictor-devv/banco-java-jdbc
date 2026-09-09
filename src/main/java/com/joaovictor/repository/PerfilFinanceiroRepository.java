package com.joaovictor.repository;

import com.joaovictor.model.PerfilFinanceiro;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PerfilFinanceiroRepository {

    public void salvar(PerfilFinanceiro perfil) {
        String sql = """
                INSERT INTO perfil_financeiro (
                    nome,
                    saldo_atual,
                    renda_mensal,
                    gastos_mensais,
                    valor_planejado_guardar
                ) VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, perfil);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar perfil financeiro.", e);
        }
    }

    public PerfilFinanceiro buscarPorId(long id) {
        String sql = "SELECT * FROM perfil_financeiro WHERE id = ?";

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPerfil(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar perfil financeiro.", e);
        }
    }

    public PerfilFinanceiro buscarUltimoPerfil() {
        String sql = "SELECT * FROM perfil_financeiro ORDER BY id DESC LIMIT 1";

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapearPerfil(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar último perfil financeiro.", e);
        }
    }

    public void atualizar(long id, PerfilFinanceiro perfil) {
        String sql = """
                UPDATE perfil_financeiro
                SET nome = ?,
                    saldo_atual = ?,
                    renda_mensal = ?,
                    gastos_mensais = ?,
                    valor_planejado_guardar = ?
                WHERE id = ?
                """;

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, perfil);
            stmt.setLong(6, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new IllegalArgumentException("Perfil financeiro não encontrado para atualização.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar perfil financeiro.", e);
        }
    }

    public void atualizarSaldo(long id, BigDecimal saldoAtual) {
        String sql = "UPDATE perfil_financeiro SET saldo_atual = ? WHERE id = ?";

        try (Connection conn = Conexao.abrir();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, saldoAtual);
            stmt.setLong(2, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new IllegalArgumentException("Perfil financeiro não encontrado para atualização do saldo.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar saldo do perfil financeiro.", e);
        }
    }

    private void preencherStatement(PreparedStatement stmt, PerfilFinanceiro perfil) throws SQLException {
        stmt.setString(1, perfil.getNome());
        stmt.setBigDecimal(2, perfil.getSaldoAtual());
        stmt.setBigDecimal(3, perfil.getRendaMensal());
        stmt.setBigDecimal(4, perfil.getGastosMensais());
        stmt.setBigDecimal(5, perfil.getValorPlanejadoGuardar());
    }

    private PerfilFinanceiro mapearPerfil(ResultSet rs) throws SQLException {
        return new PerfilFinanceiro(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getBigDecimal("saldo_atual"),
                rs.getBigDecimal("renda_mensal"),
                rs.getBigDecimal("gastos_mensais"),
                rs.getBigDecimal("valor_planejado_guardar")
        );
    }
}
