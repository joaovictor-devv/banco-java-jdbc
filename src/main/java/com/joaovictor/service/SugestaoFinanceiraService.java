package com.joaovictor.service;

import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SugestaoFinanceiraService {

    private final AnaliseFinanceiraService analiseFinanceiraService;
    private final SituacaoFinanceiraService situacaoFinanceiraService;

    public SugestaoFinanceiraService() {
        this.analiseFinanceiraService = new AnaliseFinanceiraService();
        this.situacaoFinanceiraService = new SituacaoFinanceiraService();
    }

    public List<String> gerarSugestoesDoMesAtual() {
        ResumoFinanceiro resumo = analiseFinanceiraService.gerarResumoDoMesAtual();
        BigDecimal diferencaMesAnterior = analiseFinanceiraService.compararGastosComMesAnterior();
        SituacaoFinanceira situacao = situacaoFinanceiraService.analisar();

        List<String> sugestoes = new ArrayList<>();

        if (resumo.getTotalSaidas().compareTo(resumo.getTotalEntradas()) > 0) {
            sugestoes.add("Seus gastos estão maiores que suas entradas neste mês. Revise despesas não essenciais.");
        }

        if (situacao.getMargemLivre().compareTo(BigDecimal.ZERO) < 0) {
            sugestoes.add("Sua margem livre está negativa. Evite assumir novas despesas ou metas até reorganizar o orçamento.");
        } else if (situacao.getMargemLivre().compareTo(BigDecimal.ZERO) == 0) {
            sugestoes.add("Seu orçamento está totalmente comprometido. Tenha cautela antes de criar novas metas ou gastos.");
        }

        if (situacao.getValorPlanejadoGuardar().compareTo(BigDecimal.ZERO) > 0
                && situacao.getMargemLivre().compareTo(BigDecimal.ZERO) < 0) {
            sugestoes.add("O valor planejado para guardar está acima da sua capacidade financeira atual. Considere reduzir temporariamente esse valor.");
        }

        if (resumo.getCategoriaMaiorGasto() != null
                && !resumo.getCategoriaMaiorGasto().equalsIgnoreCase("Sem dados")
                && resumo.getValorMaiorGasto().compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("Sua categoria com maior gasto é '" + resumo.getCategoriaMaiorGasto()
                    + "' com total de R$ " + resumo.getValorMaiorGasto() + ".");
        }

        if (diferencaMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("Você gastou R$ " + diferencaMesAnterior + " a mais que no mês anterior.");
        } else if (diferencaMesAnterior.compareTo(BigDecimal.ZERO) < 0) {
            sugestoes.add("Parabéns. Você reduziu seus gastos em R$ "
                    + diferencaMesAnterior.abs() + " em relação ao mês anterior.");
        }

        if (resumo.getSaldoMes().compareTo(BigDecimal.ZERO) > 0
                && situacao.getMargemLivre().compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("Seu mês está positivo. Considere reservar parte da margem livre para uma meta financeira.");
        }

        if (sugestoes.isEmpty()) {
            sugestoes.add("Sua situação financeira não apresenta alertas no momento.");
        }

        return sugestoes;
    }
}