package com.joaovictor.service;

import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.Meta;
import com.joaovictor.model.SituacaoFinanceira;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AnaliseMetaService {

    private final MetaService metaService;
    private final SituacaoFinanceiraService situacaoFinanceiraService;

    public AnaliseMetaService() {
        this.metaService = new MetaService();
        this.situacaoFinanceiraService = new SituacaoFinanceiraService();
    }

    public AnaliseMeta analisar(long id) {
        return analisar(metaService.buscarPorId(id));
    }

    public AnaliseMeta analisar(Meta meta) {
        SituacaoFinanceira situacao = situacaoFinanceiraService.analisar();

        BigDecimal valorRestante = meta.getValorAlvo().subtract(meta.getValorInicial());
        BigDecimal valorMensalNecessario = valorRestante
                .divide(BigDecimal.valueOf(meta.getPrazoMeses()), 2, RoundingMode.CEILING);
        BigDecimal margemLivre = situacao.getMargemLivre();

        if (valorRestante.compareTo(BigDecimal.ZERO) <= 0) {
            return new AnaliseMeta(BigDecimal.ZERO, BigDecimal.ZERO, margemLivre, true,
                    "CONCLUIDA", "Esta meta já atingiu o valor alvo.");
        }

        if (margemLivre.compareTo(BigDecimal.ZERO) <= 0) {
            return new AnaliseMeta(valorRestante, valorMensalNecessario, margemLivre, false,
                    "INVIAVEL", "A meta não é viável no momento porque não há margem livre disponível para guardar mensalmente.");
        }

        if (valorMensalNecessario.compareTo(margemLivre) > 0) {
            return new AnaliseMeta(valorRestante, valorMensalNecessario, margemLivre, false,
                    "INVIAVEL", "Atenção: para atingir esta meta no prazo informado, seria necessário guardar R$ "
                    + valorMensalNecessario + " por mês, mas sua margem livre atual é de R$ " + margemLivre + ".");
        }

        if (valorMensalNecessario.compareTo(margemLivre.multiply(new BigDecimal("0.8"))) > 0) {
            return new AnaliseMeta(valorRestante, valorMensalNecessario, margemLivre, true,
                    "VIAVEL_COM_ATENCAO", "A meta é viável, mas exige uma parcela alta da sua margem livre mensal.");
        }

        return new AnaliseMeta(valorRestante, valorMensalNecessario, margemLivre, true,
                "VIAVEL", "A meta é viável considerando sua situação financeira atual.");
    }
}
