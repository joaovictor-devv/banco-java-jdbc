package com.joaovictor.service;

import com.joaovictor.model.AnaliseGasto;
import com.joaovictor.model.SituacaoFinanceira;

import java.math.BigDecimal;

public class AnaliseGastoService {

    private static final BigDecimal LIMITE_ATENCAO = new BigDecimal("0.80");

    private final SituacaoFinanceiraService situacaoService;

    public AnaliseGastoService() {
        this.situacaoService = new SituacaoFinanceiraService();
    }

    public AnaliseGasto analisar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do gasto deve ser maior que zero.");
        }

        SituacaoFinanceira situacao = situacaoService.analisar();
        BigDecimal saldoAtual = situacao.getSaldoAtual();
        BigDecimal margemLivre = situacao.getMargemLivre();
        BigDecimal comprometimentoMetas = situacao.getComprometimentoMensalMetas();
        BigDecimal margemDisponivel = situacao.getMargemDisponivelAposMetas();
        BigDecimal margemAposGasto = margemDisponivel.subtract(valor);
        boolean saldoSuficiente = saldoAtual.compareTo(valor) >= 0;

        if (!saldoSuficiente) {
            return resposta(
                    valor, saldoAtual, margemLivre, comprometimentoMetas, margemDisponivel, margemAposGasto,
                    false, false, "SALDO_INSUFICIENTE",
                    "O gasto não é possível porque o saldo atual informado é de R$ " + saldoAtual + "."
            );
        }

        if (margemLivre.compareTo(BigDecimal.ZERO) <= 0) {
            return resposta(
                    valor, saldoAtual, margemLivre, comprometimentoMetas, margemDisponivel, margemAposGasto,
                    true, false, "NAO_RECOMENDADO",
                    "O saldo é suficiente, mas o orçamento mensal não possui margem livre para assumir este gasto."
            );
        }

        if (margemDisponivel.compareTo(BigDecimal.ZERO) <= 0) {
            return resposta(
                    valor, saldoAtual, margemLivre, comprometimentoMetas, margemDisponivel, margemAposGasto,
                    true, false, "NAO_RECOMENDADO",
                    "O saldo é suficiente, mas suas metas já comprometem toda a margem livre mensal."
            );
        }

        if (valor.compareTo(margemDisponivel) > 0) {
            return resposta(
                    valor, saldoAtual, margemLivre, comprometimentoMetas, margemDisponivel, margemAposGasto,
                    true, false, "ATENCAO",
                    "O gasto ultrapassa em R$ " + valor.subtract(margemDisponivel)
                            + " a margem disponível depois das metas."
            );
        }

        if (valor.compareTo(margemDisponivel.multiply(LIMITE_ATENCAO)) > 0) {
            return resposta(
                    valor, saldoAtual, margemLivre, comprometimentoMetas, margemDisponivel, margemAposGasto,
                    true, true, "ATENCAO",
                    "O gasto cabe no orçamento, mas consumirá mais de 80% da margem disponível depois das metas."
            );
        }

        return resposta(
                valor, saldoAtual, margemLivre, comprometimentoMetas, margemDisponivel, margemAposGasto,
                true, true, "RECOMENDADO",
                "O gasto está dentro do saldo e da margem disponível após considerar as metas."
        );
    }

    private AnaliseGasto resposta(BigDecimal valor,
                                  BigDecimal saldoAtual,
                                  BigDecimal margemLivre,
                                  BigDecimal comprometimentoMetas,
                                  BigDecimal margemDisponivel,
                                  BigDecimal margemAposGasto,
                                  boolean saldoSuficiente,
                                  boolean recomendado,
                                  String classificacao,
                                  String mensagem) {
        return new AnaliseGasto(
                valor,
                saldoAtual,
                margemLivre,
                comprometimentoMetas,
                margemDisponivel,
                margemAposGasto,
                saldoSuficiente,
                recomendado,
                classificacao,
                mensagem
        );
    }
}
