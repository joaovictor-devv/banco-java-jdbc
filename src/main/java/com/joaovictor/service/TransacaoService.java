package com.joaovictor.service;

import com.joaovictor.exception.SaldoInsuficienteException;
import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.model.Transacao;
import com.joaovictor.repository.PerfilFinanceiroRepository;
import com.joaovictor.repository.TransacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransacaoService {

    private final TransacaoRepository repository;
    private final PerfilFinanceiroRepository perfilRepository;

    public TransacaoService() {
        this.repository = new TransacaoRepository();
        this.perfilRepository = new PerfilFinanceiroRepository();
    }

    public void registrarEntrada(BigDecimal valor, String descricao, String categoria, LocalDate data) {
        validarCampos(valor, descricao, categoria, data);

        PerfilFinanceiro perfil = buscarPerfil();
        BigDecimal saldoAtual = valorOuZero(perfil.getSaldoAtual());
        BigDecimal novoSaldo = saldoAtual.add(valor);

        Transacao transacao = new Transacao("ENTRADA", valor, descricao.trim(), categoria.trim(), data);
        repository.salvarEAjustarSaldo(transacao, perfil.getId(), novoSaldo);
    }

    public void registrarSaida(BigDecimal valor, String descricao, String categoria, LocalDate data) {
        validarCampos(valor, descricao, categoria, data);

        PerfilFinanceiro perfil = buscarPerfil();
        BigDecimal saldoAtual = valorOuZero(perfil.getSaldoAtual());

        if (saldoAtual.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente. Saldo atual: R$ " + saldoAtual + " | Saída: R$ " + valor
            );
        }

        BigDecimal novoSaldo = saldoAtual.subtract(valor);
        Transacao transacao = new Transacao("SAIDA", valor, descricao.trim(), categoria.trim(), data);
        repository.salvarEAjustarSaldo(transacao, perfil.getId(), novoSaldo);
    }

    public BigDecimal buscarSaldoAtual() {
        return valorOuZero(buscarPerfil().getSaldoAtual());
    }

    public BigDecimal buscarSaldoHistoricoDeTransacoes() {
        return repository.calcularSaldoHistorico();
    }

    public List<Transacao> listarExtrato(int limite) {
        if (limite <= 0 || limite > 200) {
            throw new IllegalArgumentException("O limite do extrato deve estar entre 1 e 200.");
        }
        return repository.buscarExtrato(limite);
    }

    private PerfilFinanceiro buscarPerfil() {
        PerfilFinanceiro perfil = perfilRepository.buscarUltimoPerfil();
        if (perfil == null) {
            throw new IllegalArgumentException("Cadastre um perfil financeiro antes de registrar transações.");
        }
        return perfil;
    }

    private void validarCampos(BigDecimal valor, String descricao, String categoria, LocalDate data) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }

        if (descricao.length() > 120) {
            throw new IllegalArgumentException("A descrição deve ter no máximo 120 caracteres.");
        }

        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }

        if (categoria.length() > 50) {
            throw new IllegalArgumentException("A categoria deve ter no máximo 50 caracteres.");
        }

        if (data == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
