package com.joaovictor.service;

import com.joaovictor.model.AnaliseIARequest;
import com.joaovictor.model.Meta;
import com.joaovictor.model.SituacaoFinanceira;

import java.util.List;

@org.springframework.stereotype.Service
public class FiniaIAService {

    private final SituacaoFinanceiraService situacaoService;
    private final MetaService metaService;
    private final SugestaoFinanceiraService sugestaoService;
    private final OpenAiService openAiService;

    public FiniaIAService(
            SituacaoFinanceiraService situacaoService,
            MetaService metaService,
            SugestaoFinanceiraService sugestaoService,
            OpenAiService openAiService) {
        this.situacaoService = situacaoService;
        this.metaService = metaService;
        this.sugestaoService = sugestaoService;
        this.openAiService = openAiService;
    }

    public String analisar(AnaliseIARequest request) {
        SituacaoFinanceira situacao = situacaoService.analisar();
        List<Meta> metas = metaService.listarMetas();
        List<String> sugestoes = sugestaoService.gerarSugestoesDoMesAtual();

        StringBuilder contexto = new StringBuilder();
        contexto.append("Renda mensal: R$ ").append(situacao.getRendaMensal()).append('\n');
        contexto.append("Renda extra: R$ ").append(situacao.getRendaExtra()).append('\n');
        contexto.append("Renda total: R$ ").append(situacao.getRendaTotal()).append('\n');
        contexto.append("Despesas planejadas: R$ ").append(situacao.getDespesasPlanejadas()).append('\n');
        contexto.append("Valor planejado para guardar: R$ ").append(situacao.getValorPlanejadoGuardar()).append('\n');
        contexto.append("Margem livre: R$ ").append(situacao.getMargemLivre()).append('\n');
        contexto.append("Saldo do mês atual: R$ ").append(situacao.getSaldoMesAtual()).append('\n');
        contexto.append("Entradas do mês: R$ ").append(situacao.getEntradasMesAtual()).append('\n');
        contexto.append("Saídas do mês: R$ ").append(situacao.getSaidasMesAtual()).append('\n');
        contexto.append("Classificação financeira: ").append(situacao.getClassificacao()).append('\n');

        contexto.append("\nMETAS CADASTRADAS:\n");
        if (metas.isEmpty()) {
            contexto.append("Nenhuma meta cadastrada.\n");
        } else {
            for (Meta meta : metas) {
                contexto.append("- ")
                        .append(meta.getNome())
                        .append(" | alvo R$ ").append(meta.getValorAlvo())
                        .append(" | inicial R$ ").append(meta.getValorInicial())
                        .append(" | prazo ").append(meta.getPrazoMeses()).append(" meses")
                        .append(" | prioridade ").append(meta.getPrioridade())
                        .append('\n');
            }
        }

        contexto.append("\nSUGESTÕES GERADAS PELO MOTOR FINANCEIRO:\n");
        for (String sugestao : sugestoes) {
            contexto.append("- ").append(sugestao).append('\n');
        }

        return openAiService.perguntar(contexto.toString(), request.getPergunta());
    }
}