package com.joaovictor.service;

import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.repository.PerfilFinanceiroRepository;

import java.math.BigDecimal;

public class PerfilFinanceiroService {

    private final PerfilFinanceiroRepository repository;

    public PerfilFinanceiroService() {
        this.repository = new PerfilFinanceiroRepository();
    }

    public void cadastrarPerfil(BigDecimal rendaMensal,
                                BigDecimal rendaExtra,
                                BigDecimal gastoMoradia,
                                BigDecimal gastoAgua,
                                BigDecimal gastoEnergia,
                                BigDecimal gastoInternet,
                                BigDecimal gastoTransporte,
                                BigDecimal gastoAlimentacao,
                                BigDecimal outrasDespesas,
                                BigDecimal valorPlanejadoGuardar,
                                String objetivoPrincipal,
                                BigDecimal saldoAtual) {

        BigDecimal rendaExtraNormalizada = valorOuZero(rendaExtra);
        BigDecimal outrasDespesasNormalizadas = valorOuZero(outrasDespesas);
        BigDecimal saldoNormalizado = valorOuZero(saldoAtual);
        String objetivoNormalizado = normalizarObjetivo(objetivoPrincipal);

        validarCampos(
                rendaMensal,
                rendaExtraNormalizada,
                gastoMoradia,
                gastoAgua,
                gastoEnergia,
                gastoInternet,
                gastoTransporte,
                gastoAlimentacao,
                outrasDespesasNormalizadas,
                valorPlanejadoGuardar,
                saldoNormalizado
        );

        PerfilFinanceiro perfil = new PerfilFinanceiro(
                rendaMensal,
                rendaExtraNormalizada,
                gastoMoradia,
                gastoAgua,
                gastoEnergia,
                gastoInternet,
                gastoTransporte,
                gastoAlimentacao,
                outrasDespesasNormalizadas,
                valorPlanejadoGuardar,
                objetivoNormalizado,
                saldoNormalizado
        );

        repository.salvar(perfil);
    }

    public PerfilFinanceiro buscarUltimoPerfil() {
        PerfilFinanceiro perfil = repository.buscarUltimoPerfil();

        if (perfil == null) {
            throw new IllegalArgumentException("Nenhum perfil financeiro foi cadastrado ainda.");
        }

        return perfil;
    }

    public PerfilFinanceiro buscarPorId(long id) {
        PerfilFinanceiro perfil = repository.buscarPorId(id);

        if (perfil == null) {
            throw new IllegalArgumentException("Perfil financeiro não encontrado para o id informado.");
        }

        return perfil;
    }

    public void atualizarPerfil(long id,
                                BigDecimal rendaMensal,
                                BigDecimal rendaExtra,
                                BigDecimal gastoMoradia,
                                BigDecimal gastoAgua,
                                BigDecimal gastoEnergia,
                                BigDecimal gastoInternet,
                                BigDecimal gastoTransporte,
                                BigDecimal gastoAlimentacao,
                                BigDecimal outrasDespesas,
                                BigDecimal valorPlanejadoGuardar,
                                String objetivoPrincipal,
                                BigDecimal saldoAtual) {

        PerfilFinanceiro perfilExistente = repository.buscarPorId(id);
        if (perfilExistente == null) {
            throw new IllegalArgumentException("Perfil financeiro não encontrado para atualização.");
        }

        BigDecimal rendaExtraNormalizada = valorOuZero(rendaExtra);
        BigDecimal outrasDespesasNormalizadas = valorOuZero(outrasDespesas);
        BigDecimal saldoNormalizado = saldoAtual != null
                ? saldoAtual
                : valorOuZero(perfilExistente.getSaldoAtual());
        String objetivoNormalizado = objetivoPrincipal == null
                ? normalizarObjetivo(perfilExistente.getObjetivoPrincipal())
                : normalizarObjetivo(objetivoPrincipal);

        validarCampos(
                rendaMensal,
                rendaExtraNormalizada,
                gastoMoradia,
                gastoAgua,
                gastoEnergia,
                gastoInternet,
                gastoTransporte,
                gastoAlimentacao,
                outrasDespesasNormalizadas,
                valorPlanejadoGuardar,
                saldoNormalizado
        );

        PerfilFinanceiro perfilAtualizado = new PerfilFinanceiro(
                id,
                rendaMensal,
                rendaExtraNormalizada,
                gastoMoradia,
                gastoAgua,
                gastoEnergia,
                gastoInternet,
                gastoTransporte,
                gastoAlimentacao,
                outrasDespesasNormalizadas,
                valorPlanejadoGuardar,
                objetivoNormalizado,
                saldoNormalizado
        );

        repository.atualizar(id, perfilAtualizado);
    }

    public PerfilFinanceiro atualizarSaldoAtual(BigDecimal saldoAtual) {
        validarNaoNegativo(saldoAtual, "O saldo atual é obrigatório e não pode ser negativo.");

        PerfilFinanceiro perfil = buscarUltimoPerfil();
        repository.atualizarSaldo(perfil.getId(), saldoAtual);
        perfil.setSaldoAtual(saldoAtual);
        return perfil;
    }

    private void validarCampos(BigDecimal rendaMensal,
                               BigDecimal rendaExtra,
                               BigDecimal gastoMoradia,
                               BigDecimal gastoAgua,
                               BigDecimal gastoEnergia,
                               BigDecimal gastoInternet,
                               BigDecimal gastoTransporte,
                               BigDecimal gastoAlimentacao,
                               BigDecimal outrasDespesas,
                               BigDecimal valorPlanejadoGuardar,
                               BigDecimal saldoAtual) {

        validarNaoNegativo(rendaMensal, "A renda mensal é obrigatória e não pode ser negativa.");
        validarNaoNegativo(rendaExtra, "A renda extra não pode ser negativa.");
        validarNaoNegativo(gastoMoradia, "O gasto com moradia é obrigatório e não pode ser negativo.");
        validarNaoNegativo(gastoAgua, "O gasto com água é obrigatório e não pode ser negativo.");
        validarNaoNegativo(gastoEnergia, "O gasto com energia é obrigatório e não pode ser negativo.");
        validarNaoNegativo(gastoInternet, "O gasto com internet é obrigatório e não pode ser negativo.");
        validarNaoNegativo(gastoTransporte, "O gasto com transporte é obrigatório e não pode ser negativo.");
        validarNaoNegativo(gastoAlimentacao, "O gasto com alimentação é obrigatório e não pode ser negativo.");
        validarNaoNegativo(outrasDespesas, "Outras despesas não podem ser negativas.");
        validarNaoNegativo(valorPlanejadoGuardar, "O valor planejado para guardar é obrigatório e não pode ser negativo.");
        validarNaoNegativo(saldoAtual, "O saldo atual não pode ser negativo.");
    }

    private String normalizarObjetivo(String objetivoPrincipal) {
        if (objetivoPrincipal == null || objetivoPrincipal.isBlank()) {
            return "Organizar finanças";
        }
        return objetivoPrincipal.trim();
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
