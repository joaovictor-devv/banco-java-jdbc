package com.joaovictor.service;

import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.ContextoFinanceiroIA;
import com.joaovictor.model.Meta;
import com.joaovictor.model.SituacaoFinanceira;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextoFinanceiroIAService {

    private final SituacaoFinanceiraService situacaoService;
    private final SugestaoFinanceiraService sugestaoService;
    private final MetaService metaService;
    private final AnaliseMetaService analiseMetaService;
    private final MotorFinanceiroService motorFinanceiroService;

    public ContextoFinanceiroIAService() {
        this.situacaoService = new SituacaoFinanceiraService();
        this.sugestaoService = new SugestaoFinanceiraService();
        this.metaService = new MetaService();
        this.analiseMetaService = new AnaliseMetaService();
        this.motorFinanceiroService = new MotorFinanceiroService();
    }

    public ContextoFinanceiroIA montar() {
        SituacaoFinanceira situacao = situacaoService.analisar();
        List<String> sugestoes = sugestaoService.gerarSugestoesDoMesAtual();
        List<Meta> metas = metaService.listarMetas();

        return new ContextoFinanceiroIA(situacao, sugestoes, metas);
    }

    public String montarTexto() {
        ContextoFinanceiroIA contexto = montar();
        SituacaoFinanceira s = contexto.getSituacao();
        CapacidadeFinanceira capacidade = motorFinanceiroService.calcularCapacidade();

        StringBuilder texto = new StringBuilder();

        texto.append("SITUAÇÃO FINANCEIRA ATUAL (FONTE PRINCIPAL):\n")
                .append("Classificação: ").append(s.getClassificacao()).append('\n')
                .append("Saldo atual informado pelo usuário: R$ ").append(s.getSaldoAtual()).append('\n')
                .append("Renda mensal: R$ ").append(s.getRendaMensal()).append('\n')
                .append("Gastos mensais: R$ ").append(s.getGastosMensais()).append('\n')
                .append("Valor planejado para guardar: R$ ").append(s.getValorPlanejadoGuardar()).append('\n')
                .append("Margem antes das metas: R$ ").append(s.getMargemAntesMetas()).append('\n')
                .append("Comprometimento mensal das metas: R$ ").append(s.getComprometimentoMensalMetas()).append('\n')
                .append("Margem disponível após as metas: R$ ").append(s.getMargemDisponivelAposMetas()).append('\n')
                .append("Capacidade de gasto imediato: R$ ").append(s.getCapacidadeGastoImediato()).append("\n\n");

        texto.append("CAPACIDADE DE GASTO CALCULADA PELO MOTOR:\n")
                .append("Total de compromissos mensais: R$ ").append(capacidade.getTotalCompromissosMensais()).append('\n')
                .append("Percentual da renda comprometida: ").append(capacidade.getPercentualRendaComprometida()).append("%\n")
                .append("Capacidade para novos gastos no mês: R$ ").append(capacidade.getCapacidadeGastoMensal()).append('\n')
                .append("Limite de gasto imediato recomendado: R$ ").append(capacidade.getCapacidadeGastoImediato()).append('\n')
                .append("Classificação do motor: ").append(capacidade.getClassificacao()).append('\n')
                .append("Explicação do motor: ").append(capacidade.getMensagem()).append("\n\n");

        texto.append("METAS CADASTRADAS:\n");
        if (contexto.getMetas().isEmpty()) {
            texto.append("Nenhuma meta cadastrada.\n");
        } else {
            for (Meta meta : contexto.getMetas()) {
                AnaliseMeta analise = analiseMetaService.analisar(meta);
                texto.append("- ").append(meta.getNome())
                        .append(" | alvo: R$ ").append(meta.getValorAlvo())
                        .append(" | atual reservado: R$ ").append(meta.getValorInicial())
                        .append(" | prazo informado: ").append(meta.getPrazoMeses()).append(" meses")
                        .append(" | prioridade: ").append(meta.getPrioridade())
                        .append(" | necessário/mês: R$ ").append(analise.getValorMensalNecessario())
                        .append(" | outras metas comprometem: R$ ").append(analise.getComprometimentoOutrasMetas())
                        .append(" | margem disponível para esta meta: R$ ").append(analise.getMargemDisponivelParaMeta())
                        .append(" | percentual da margem exigido: ").append(analise.getPercentualMargemComprometida()).append("%")
                        .append(" | prazo mínimo viável: ").append(analise.getPrazoMinimoViavelMeses())
                        .append(" | prazo confortável: ").append(analise.getPrazoConfortavelMeses())
                        .append(" | classificação: ").append(analise.getClassificacao())
                        .append(" | viável: ").append(analise.isViavel())
                        .append(" | motivo: ").append(analise.getMensagem())
                        .append('\n');
            }
        }

        texto.append("\nSUGESTÕES DO MOTOR FINANCEIRO:\n");
        for (String sugestao : contexto.getSugestoes()) {
            texto.append("- ").append(sugestao).append('\n');
        }

        texto.append("\nREGRA DE INTERPRETAÇÃO:\n")
                .append("O motor financeiro é a fonte dos números e classificações. A IA deve explicar, comparar e contextualizar esses resultados, sem inventar valores nem sobrescrever os cálculos.\n");

        return texto.toString();
    }
}
