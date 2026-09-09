package com.joaovictor.service;

import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.repository.PerfilFinanceiroRepository;

import java.math.BigDecimal;

public class PerfilFinanceiroService {

    private final PerfilFinanceiroRepository repository;

    public PerfilFinanceiroService() {
        this.repository = new PerfilFinanceiroRepository();
    }

    public PerfilFinanceiro cadastrarPerfil(String nome, BigDecimal saldoAtual) {
        if (repository.buscarUltimoPerfil() != null) {
            throw new IllegalArgumentException("Já existe um perfil financeiro. Use a atualização do perfil.");
        }

        String nomeNormalizado = normalizarNome(nome);
        BigDecimal saldoNormalizado = valorOuZero(saldoAtual);
        validarSaldo(saldoNormalizado);

        PerfilFinanceiro perfil = new PerfilFinanceiro(
                nomeNormalizado,
                saldoNormalizado,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        repository.salvar(perfil);
        return buscarUltimoPerfil();
    }

    public PerfilFinanceiro buscarUltimoPerfil() {
        PerfilFinanceiro perfil = repository.buscarUltimoPerfil();

        if (perfil == null) {
            throw new IllegalArgumentException("Nenhum perfil financeiro foi cadastrado ainda.");
        }

        return perfil;
    }

    public PerfilFinanceiro atualizarPerfil(String nome, BigDecimal saldoAtual) {
        PerfilFinanceiro existente = buscarUltimoPerfil();

        String nomeNormalizado = nome == null
                ? normalizarNome(existente.getNome())
                : normalizarNome(nome);
        BigDecimal saldoNormalizado = saldoAtual == null
                ? valorOuZero(existente.getSaldoAtual())
                : saldoAtual;
        validarSaldo(saldoNormalizado);

        PerfilFinanceiro atualizado = new PerfilFinanceiro(
                existente.getId(),
                nomeNormalizado,
                saldoNormalizado,
                valorOuZero(existente.getRendaMensal()),
                valorOuZero(existente.getGastosMensais()),
                valorOuZero(existente.getValorPlanejadoGuardar())
        );

        repository.atualizar(existente.getId(), atualizado);
        return atualizado;
    }

    public PerfilFinanceiro atualizarSaldoAtual(BigDecimal saldoAtual) {
        validarSaldo(saldoAtual);

        PerfilFinanceiro perfil = buscarUltimoPerfil();
        repository.atualizarSaldo(perfil.getId(), saldoAtual);
        perfil.setSaldoAtual(saldoAtual);
        return perfil;
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "Usuário";
        }

        String nomeNormalizado = nome.trim();
        if (nomeNormalizado.length() > 100) {
            throw new IllegalArgumentException("O nome deve ter no máximo 100 caracteres.");
        }
        return nomeNormalizado;
    }

    private void validarSaldo(BigDecimal saldoAtual) {
        if (saldoAtual == null || saldoAtual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O saldo atual é obrigatório e não pode ser negativo.");
        }
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
