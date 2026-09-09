package com.joaovictor.service;

import com.joaovictor.model.AnaliseGasto;
import com.joaovictor.model.SituacaoFinanceira;

import java.math.BigDecimal;

public class AnaliseGastoService {

    private final SituacaoFinanceiraService situacaoService;
    private final TransacaoService transacaoService;

    public AnaliseGastoService() {
        this.situacaoService = new SituacaoFinanceiraService();
        this.transacaoService = new TransacaoService();
    }

    public AnaliseGasto analisar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do gasto deve ser maior que zero.");
        }

        SituacaoFinanceira situacao = situacaoService.analisar();
        BigDecimal saldoAtual = transacaoService.buscarSaldoAtual();
        BigDecimal margemAtual = situacao.getMargemLivre();
        BigDecimal margemAposGasto = margemAtual.subtract(valor);
        boolean saldoSuficiente = saldoAtual.compareTo(valor) >= 0;

        if (!saldoSuficiente) {
            return new AnaliseGasto(
                    valor, margemAtual, margemAposGasto, false, false,
                    "SALDO_INSUFICIENTE",
                    "Este gasto não pode ser realizado porque o saldo atual é insuficiente."
            );
        }

        if (margemAtual.compareTo(BigDecimal.ZERO) <= 0) {
            return new AnaliseGasto(
                    valor, margemAtual, margemAposGasto, true, false,
                    "NAO_RECOMENDADO",
                    "O saldo é suficiente, mas sua situação financeira atual não possui margem livre para este gasto."
            );
        }

        if (valor.compareTo(margemAtual) > 0) {
            return new AnaliseGasto(
                    valor, margemAtual, margemAposGasto, true, false,
                    "ATENCAO",
                    "Atenção: este gasto ultrapassa sua margem livre atual em R$ "
                            + valor.subtract(margemAtual) + "."
            );
        }

        if (valor.compareTo(margemAtual.multiply(new BigDecimal("0.8"))) > 0) {
            return new AnaliseGasto(
                    valor, margemAtual, margemAposGasto, true, true,
                    "ATENCAO",
                    "O gasto cabe na sua margem livre, mas consumirá uma parcela alta dela."
            );
        }

        return new AnaliseGasto(
                valor, margemAtual, margemAposGasto, true, true,
                "RECOMENDADO",
                "O gasto está dentro da sua margem livre atual."
        );
    }
}
