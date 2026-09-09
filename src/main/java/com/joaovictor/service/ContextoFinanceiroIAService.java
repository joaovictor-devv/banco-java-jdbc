package com.joaovictor.service;

import com.joaovictor.model.ContextoFinanceiroIA;
import com.joaovictor.model.Meta;
import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextoFinanceiroIAService {

    private final SituacaoFinanceiraService situacaoService;
    private final AnaliseFinanceiraService analiseService;
    private final SugestaoFinanceiraService sugestaoService;
    private final MetaService metaService;

    public ContextoFinanceiroIAService() {
        this.situacaoService = new SituacaoFinanceiraService();
        this.analiseService = new AnaliseFinanceiraService();
        this.sugestaoService = new SugestaoFinanceiraService();
        this.metaService = new MetaService();
    }

    public ContextoFinanceiroIA montar() {
        SituacaoFinanceira situacao = situacaoService.analisar();
        ResumoFinanceiro resumo = analiseService.gerarResumoDoMesAtual();
        List<String> sugestoes = sugestaoService.gerarSugestoesDoMesAtual();
        List<Meta> metas = metaService.listarMetas();

        return new ContextoFinanceiroIA(situacao, resumo, sugestoes, metas);
    }

    public String montarTexto() {
        ContextoFinanceiroIA contexto = montar();
        StringBuilder texto = new StringBuilder();

        SituacaoFinanceira s = contexto.getSituacao();
        ResumoFinanceiro r = contexto.getResumo();

        texto.append("SITUAÇÃO FINANCEIRA ATUAL:\n")
                .append("Classificação: ").append(s.getClassificacao()).append('\n')
                .append("Renda mensal: R$ ").append(s.getRendaMensal()).append('\n')
                .append("Renda extra: R$ ").append(s.getRendaExtra()).append('\n')
                .append("Renda total: R$ ").append(s.getRendaTotal()).append('\n')
                .append("Despesas planejadas: R$ ").append(s.getDespesasPlanejadas()).append('\n')
                .append("Valor planejado para guardar: R$ ").append(s.getValorPlanejadoGuardar()).append('\n')
                .append("Margem livre: R$ ").append(s.getMargemLivre()).append('\n')
                .append("Saldo do mês atual: R$ ").append(s.getSaldoMesAtual()).append('\n')
                .append("Entradas no mês: R$ ").append(s.getEntradasMesAtual()).append('\n')
                .append("Saídas no mês: R$ ").append(s.getSaidasMesAtual()).append("\n\n");

        texto.append("RESUMO DO MÊS:\n")
                .append("Total de entradas: R$ ").append(r.getTotalEntradas()).append('\n')
                .append("Total de saídas: R$ ").append(r.getTotalSaidas()).append('\n')
                .append("Saldo: R$ ").append(r.getSaldoMes()).append('\n')
                .append("Maior categoria de gasto: ").append(r.getCategoriaMaiorGasto()).append('\n')
                .append("Valor da maior categoria: R$ ").append(r.getValorMaiorGasto()).append("\n\n");

        texto.append("METAS CADASTRADAS:\n");
        if (contexto.getMetas().isEmpty()) {
            texto.append("Nenhuma meta cadastrada.\n");
        } else {
            for (Meta meta : contexto.getMetas()) {
                AnaliseMeta analise = new AnaliseMetaService().analisar(meta);
                texto.append("- ").append(meta.getNome())
                        .append(" | alvo: R$ ").append(meta.getValorAlvo())
                        .append(" | inicial: R$ ").append(meta.getValorInicial())
                        .append(" | prazo: ").append(meta.getPrazoMeses()).append(" meses")
                        .append(" | prioridade: ").append(meta.getPrioridade())
                        .append(" | classificação: ").append(analise.getClassificacao())
                        .append(" | necessário/mês: R$ ").append(analise.getValorMensalNecessario())
                        .append(" | viável: ").append(analise.isViavel())
                        .append('\n');
            }
        }

        texto.append("\nSUGESTÕES DO SISTEMA:\n");
        if (contexto.getSugestoes().isEmpty()) {
            texto.append("Nenhuma sugestão automática no momento.\n");
        } else {
            for (String sugestao : contexto.getSugestoes()) {
                texto.append("- ").append(sugestao).append('\n');
            }
        }

        return texto.toString();
    }
}
