package com.joaovictor.service;

import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SugestaoFinanceiraService {

    private static final BigDecimal LIMITE_ATENCAO = new BigDecimal("0.80");

    private final AnaliseFinanceiraService analiseFinanceiraService;
    private final SituacaoFinanceiraService situacaoFinanceiraService;
    private final CompromissoMetasService compromissoMetasService;

    public SugestaoFinanceiraService() {
        this.analiseFinanceiraService = new AnaliseFinanceiraService();
        this.situacaoFinanceiraService = new SituacaoFinanceiraService();
        this.compromissoMetasService = new CompromissoMetasService();
    }

    public List<String> gerarSugestoesDoMesAtual() {
        ResumoFinanceiro resumo = analiseFinanceiraService.gerarResumoDoMesAtual();
        BigDecimal diferencaMesAnterior = analiseFinanceiraService.compararGastosComMesAnterior();
        SituacaoFinanceira situacao = situacaoFinanceiraService.analisar();
        BigDecimal comprometimentoMetas = compromissoMetasService.calcularComprometimentoMensalTotal();
        BigDecimal margemAposMetas = situacao.getMargemLivre().subtract(comprometimentoMetas);

        List<String> sugestoes = new ArrayList<>();

        if (situacao.getDespesasPlanejadas().compareTo(situacao.getRendaTotal()) > 0) {
            sugestoes.add("Suas despesas planejadas estão acima da renda total. Revise o orçamento antes de assumir novos compromissos.");
        }

        if (situacao.getMargemLivre().compareTo(BigDecimal.ZERO) < 0) {
            sugestoes.add("Sua margem livre está negativa. Evite assumir novas despesas ou metas até reorganizar o orçamento.");
        } else if (situacao.getMargemLivre().compareTo(BigDecimal.ZERO) == 0) {
            sugestoes.add("Seu orçamento está totalmente comprometido. Tenha cautela antes de criar novas metas ou gastos.");
        }

        if (comprometimentoMetas.compareTo(BigDecimal.ZERO) > 0) {
            if (comprometimentoMetas.compareTo(situacao.getMargemLivre()) > 0) {
                sugestoes.add("Suas metas exigem juntas R$ " + comprometimentoMetas
                        + " por mês e ultrapassam a margem livre atual de R$ " + situacao.getMargemLivre() + ".");
            } else if (situacao.getMargemLivre().compareTo(BigDecimal.ZERO) > 0
                    && comprometimentoMetas.compareTo(situacao.getMargemLivre().multiply(LIMITE_ATENCAO)) > 0) {
                sugestoes.add("Suas metas consomem mais de 80% da margem livre mensal. O plano é possível, mas está apertado.");
            }
        }

        if (margemAposMetas.compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("Depois das metas, sua margem disponível estimada é de R$ " + margemAposMetas + " por mês.");
        }

        if (resumo.getCategoriaMaiorGasto() != null
                && !resumo.getCategoriaMaiorGasto().equalsIgnoreCase("Sem dados")
                && resumo.getValorMaiorGasto().compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("No histórico registrado, a categoria com maior gasto é '" + resumo.getCategoriaMaiorGasto()
                    + "', com R$ " + resumo.getValorMaiorGasto() + ".");
        }

        if (diferencaMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("No histórico de transações, você gastou R$ " + diferencaMesAnterior + " a mais que no mês anterior.");
        } else if (diferencaMesAnterior.compareTo(BigDecimal.ZERO) < 0) {
            sugestoes.add("No histórico de transações, seus gastos caíram R$ "
                    + diferencaMesAnterior.abs() + " em relação ao mês anterior.");
        }

        if (sugestoes.isEmpty()) {
            sugestoes.add("Sua situação financeira não apresenta alertas no momento.");
        }

        return sugestoes;
    }
}
