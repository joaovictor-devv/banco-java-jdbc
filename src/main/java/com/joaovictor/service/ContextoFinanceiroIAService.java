package com.joaovictor.service;

import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.ContextoFinanceiroIA;
import com.joaovictor.model.Meta;
import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.RevisaoMensal;
import com.joaovictor.model.SituacaoFinanceira;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContextoFinanceiroIAService {

    private final SituacaoFinanceiraService situacaoService;
    private final AnaliseFinanceiraService analiseService;
    private final SugestaoFinanceiraService sugestaoService;
    private final MetaService metaService;
    private final RevisaoMensalService revisaoMensalService;
    private final AnaliseMetaService analiseMetaService;
    private final CompromissoMetasService compromissoMetasService;

    public ContextoFinanceiroIAService() {
        this.situacaoService = new SituacaoFinanceiraService();
        this.analiseService = new AnaliseFinanceiraService();
        this.sugestaoService = new SugestaoFinanceiraService();
        this.metaService = new MetaService();
        this.revisaoMensalService = new RevisaoMensalService();
        this.analiseMetaService = new AnaliseMetaService();
        this.compromissoMetasService = new CompromissoMetasService();
    }

    public ContextoFinanceiroIA montar() {
        SituacaoFinanceira situacao = situacaoService.analisar();
        ResumoFinanceiro resumo = analiseService.gerarResumoDoMesAtual();
        List<String> sugestoes = sugestaoService.gerarSugestoesDoMesAtual();
        List<Meta> metas = metaService.listarMetas();
        List<RevisaoMensal> revisoes = revisaoMensalService.listarRevisoes()
                .stream()
                .limit(5)
                .toList();

        return new ContextoFinanceiroIA(situacao, resumo, sugestoes, metas, revisoes);
    }

    public String montarTexto() {
        ContextoFinanceiroIA contexto = montar();
        SituacaoFinanceira s = contexto.getSituacao();
        ResumoFinanceiro r = contexto.getResumo();
        BigDecimal comprometimentoMetas = compromissoMetasService.calcularComprometimentoMensalTotal();
        BigDecimal margemAposMetas = s.getMargemLivre().subtract(comprometimentoMetas);

        StringBuilder texto = new StringBuilder();

        texto.append("SITUAÇÃO FINANCEIRA ATUAL (FONTE PRINCIPAL):\n")
                .append("Classificação: ").append(s.getClassificacao()).append('\n')
                .append("Saldo atual informado pelo usuário: R$ ").append(s.getSaldoAtual()).append('\n')
                .append("Renda mensal: R$ ").append(s.getRendaMensal()).append('\n')
                .append("Renda extra: R$ ").append(s.getRendaExtra()).append('\n')
                .append("Renda total: R$ ").append(s.getRendaTotal()).append('\n')
                .append("Despesas planejadas: R$ ").append(s.getDespesasPlanejadas()).append('\n')
                .append("Valor planejado para guardar: R$ ").append(s.getValorPlanejadoGuardar()).append('\n')
                .append("Margem livre antes das metas: R$ ").append(s.getMargemLivre()).append('\n')
                .append("Comprometimento mensal total das metas: R$ ").append(comprometimentoMetas).append('\n')
                .append("Margem disponível após as metas: R$ ").append(margemAposMetas).append("\n\n");

        texto.append("HISTÓRICO OPCIONAL DE TRANSAÇÕES DO MÊS:\n")
                .append("Entradas registradas: R$ ").append(r.getTotalEntradas()).append('\n')
                .append("Saídas registradas: R$ ").append(r.getTotalSaidas()).append('\n')
                .append("Resultado das transações do mês: R$ ").append(r.getSaldoMes()).append('\n')
                .append("Maior categoria de gasto registrada: ").append(r.getCategoriaMaiorGasto()).append('\n')
                .append("Valor da maior categoria: R$ ").append(r.getValorMaiorGasto()).append('\n')
                .append("Observação: o saldo atual informado no perfil é a fonte usada para decisões de disponibilidade imediata.\n\n");

        texto.append("METAS CADASTRADAS:\n");
        if (contexto.getMetas().isEmpty()) {
            texto.append("Nenhuma meta cadastrada.\n");
        } else {
            for (Meta meta : contexto.getMetas()) {
                AnaliseMeta analise = analiseMetaService.analisar(meta);
                texto.append("- ").append(meta.getNome())
                        .append(" | alvo: R$ ").append(meta.getValorAlvo())
                        .append(" | inicial: R$ ").append(meta.getValorInicial())
                        .append(" | prazo: ").append(meta.getPrazoMeses()).append(" meses")
                        .append(" | prioridade: ").append(meta.getPrioridade())
                        .append(" | necessário/mês: R$ ").append(analise.getValorMensalNecessario())
                        .append(" | outras metas comprometem: R$ ").append(analise.getComprometimentoOutrasMetas())
                        .append(" | margem disponível para esta meta: R$ ").append(analise.getMargemDisponivelParaMeta())
                        .append(" | classificação: ").append(analise.getClassificacao())
                        .append(" | viável: ").append(analise.isViavel())
                        .append(" | motivo: ").append(analise.getMensagem())
                        .append('\n');
            }
        }

        texto.append("\nREVISÕES MENSAIS RECENTES:\n");
        if (contexto.getRevisoesMensais().isEmpty()) {
            texto.append("Nenhuma revisão mensal cadastrada.\n");
        } else {
            for (RevisaoMensal revisao : contexto.getRevisoesMensais()) {
                texto.append("- ").append(revisao.getMesReferencia())
                        .append(" | gasto inesperado: ").append(revisao.isGastoInesperado())
                        .append(" | valor incorreto: ").append(revisao.isValorIncorreto())
                        .append(" | revisar categorias: ").append(revisao.isRevisarCategorias());

                if (revisao.getObservacoes() != null && !revisao.getObservacoes().isBlank()) {
                    texto.append(" | observações: ").append(revisao.getObservacoes());
                }

                texto.append('\n');
            }
        }

        texto.append("\nSUGESTÕES DO MOTOR FINANCEIRO:\n");
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
