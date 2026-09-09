package com.joaovictor.service;

import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.Meta;
import com.joaovictor.model.SituacaoFinanceira;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AnaliseMetaService {

    private static final BigDecimal LIMITE_ATENCAO = new BigDecimal("0.80");
    private static final BigDecimal LIMITE_CONFORTAVEL = new BigDecimal("0.70");
    private static final BigDecimal CEM = new BigDecimal("100");

    private final MetaService metaService;
    private final SituacaoFinanceiraService situacaoFinanceiraService;
    private final CompromissoMetasService compromissoMetasService;

    public AnaliseMetaService() {
        this.metaService = new MetaService();
        this.situacaoFinanceiraService = new SituacaoFinanceiraService();
        this.compromissoMetasService = new CompromissoMetasService();
    }

    public AnaliseMeta analisar(long id) {
        return analisar(metaService.buscarPorId(id));
    }

    public AnaliseMeta analisar(Meta meta) {
        if (meta == null) {
            throw new IllegalArgumentException("A meta é obrigatória para análise.");
        }

        SituacaoFinanceira situacao = situacaoFinanceiraService.analisar();

        BigDecimal valorRestante = meta.getValorAlvo().subtract(meta.getValorInicial());
        BigDecimal valorMensalNecessario = compromissoMetasService.calcularValorMensal(meta);
        BigDecimal margemAntesMetas = situacao.getMargemAntesMetas();
        Long idParaExcluir = meta.getId() > 0 ? meta.getId() : null;
        BigDecimal comprometimentoOutrasMetas =
                compromissoMetasService.calcularComprometimentoMensalExcluindo(idParaExcluir);
        BigDecimal margemDisponivel = margemAntesMetas.subtract(comprometimentoOutrasMetas);

        if (valorRestante.compareTo(BigDecimal.ZERO) <= 0) {
            return resposta(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    margemAntesMetas,
                    comprometimentoOutrasMetas,
                    margemDisponivel,
                    true,
                    "CONCLUIDA",
                    "Esta meta já atingiu o valor alvo."
            );
        }

        if (margemAntesMetas.compareTo(BigDecimal.ZERO) <= 0) {
            return resposta(
                    valorRestante,
                    valorMensalNecessario,
                    margemAntesMetas,
                    comprometimentoOutrasMetas,
                    margemDisponivel,
                    false,
                    "INVIAVEL",
                    "A meta não é viável no momento porque o orçamento não possui margem livre mensal."
            );
        }

        if (margemDisponivel.compareTo(BigDecimal.ZERO) <= 0) {
            return resposta(
                    valorRestante,
                    valorMensalNecessario,
                    margemAntesMetas,
                    comprometimentoOutrasMetas,
                    margemDisponivel,
                    false,
                    "INVIAVEL",
                    "As outras metas já comprometem toda a margem disponível para uma nova meta."
            );
        }

        if (valorMensalNecessario.compareTo(margemDisponivel) > 0) {
            return resposta(
                    valorRestante,
                    valorMensalNecessario,
                    margemAntesMetas,
                    comprometimentoOutrasMetas,
                    margemDisponivel,
                    false,
                    "INVIAVEL",
                    "Para atingir esta meta no prazo, seriam necessários R$ "
                            + valorMensalNecessario + " por mês. Depois das outras metas, restam R$ "
                            + margemDisponivel + " por mês."
            );
        }

        if (valorMensalNecessario.compareTo(margemDisponivel.multiply(LIMITE_ATENCAO)) > 0) {
            return resposta(
                    valorRestante,
                    valorMensalNecessario,
                    margemAntesMetas,
                    comprometimentoOutrasMetas,
                    margemDisponivel,
                    true,
                    "VIAVEL_COM_ATENCAO",
                    "A meta é viável, mas consumirá mais de 80% da margem que sobra após considerar as outras metas."
            );
        }

        return resposta(
                valorRestante,
                valorMensalNecessario,
                margemAntesMetas,
                comprometimentoOutrasMetas,
                margemDisponivel,
                true,
                "VIAVEL",
                "A meta é viável considerando o orçamento atual e as outras metas cadastradas."
        );
    }

    private AnaliseMeta resposta(BigDecimal valorRestante,
                                 BigDecimal valorMensalNecessario,
                                 BigDecimal margemAntesMetas,
                                 BigDecimal comprometimentoOutrasMetas,
                                 BigDecimal margemDisponivel,
                                 boolean viavel,
                                 String classificacao,
                                 String mensagem) {

        BigDecimal percentualMargemComprometida = calcularPercentual(
                valorMensalNecessario,
                margemDisponivel
        );

        Integer prazoMinimoViavel = calcularPrazo(
                valorRestante,
                margemDisponivel
        );

        Integer prazoConfortavel = calcularPrazo(
                valorRestante,
                margemDisponivel.compareTo(BigDecimal.ZERO) > 0
                        ? margemDisponivel.multiply(LIMITE_CONFORTAVEL)
                        : BigDecimal.ZERO
        );

        String mensagemFinal = mensagem;
        if (!viavel && prazoConfortavel != null && prazoConfortavel > 0) {
            mensagemFinal += " Mantendo a situação atual, o prazo mínimo é de "
                    + prazoMinimoViavel + " meses e um prazo mais confortável seria de aproximadamente "
                    + prazoConfortavel + " meses.";
        }

        return new AnaliseMeta(
                valorRestante,
                valorMensalNecessario,
                margemAntesMetas,
                comprometimentoOutrasMetas,
                margemDisponivel,
                percentualMargemComprometida,
                prazoMinimoViavel,
                prazoConfortavel,
                viavel,
                classificacao,
                mensagemFinal
        );
    }

    private BigDecimal calcularPercentual(BigDecimal parte, BigDecimal total) {
        if (parte == null || total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return parte.multiply(CEM)
                .divide(total, 2, RoundingMode.HALF_UP);
    }

    private Integer calcularPrazo(BigDecimal valorRestante, BigDecimal capacidadeMensal) {
        if (valorRestante == null || valorRestante.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        if (capacidadeMensal == null || capacidadeMensal.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        return valorRestante
                .divide(capacidadeMensal, 0, RoundingMode.CEILING)
                .intValue();
    }
}
