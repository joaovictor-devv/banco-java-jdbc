package com.joaovictor.model;

import java.math.BigDecimal;

public class AnaliseMeta {

    private final BigDecimal valorRestante;
    private final BigDecimal valorMensalNecessario;
    private final BigDecimal margemLivre;
    private final BigDecimal comprometimentoOutrasMetas;
    private final BigDecimal margemDisponivelParaMeta;
    private final boolean viavel;
    private final String classificacao;
    private final String mensagem;

    public AnaliseMeta(BigDecimal valorRestante,
                       BigDecimal valorMensalNecessario,
                       BigDecimal margemLivre,
                       BigDecimal comprometimentoOutrasMetas,
                       BigDecimal margemDisponivelParaMeta,
                       boolean viavel,
                       String classificacao,
                       String mensagem) {
        this.valorRestante = valorRestante;
        this.valorMensalNecessario = valorMensalNecessario;
        this.margemLivre = margemLivre;
        this.comprometimentoOutrasMetas = comprometimentoOutrasMetas;
        this.margemDisponivelParaMeta = margemDisponivelParaMeta;
        this.viavel = viavel;
        this.classificacao = classificacao;
        this.mensagem = mensagem;
    }

    public BigDecimal getValorRestante() { return valorRestante; }
    public BigDecimal getValorMensalNecessario() { return valorMensalNecessario; }
    public BigDecimal getMargemLivre() { return margemLivre; }
    public BigDecimal getComprometimentoOutrasMetas() { return comprometimentoOutrasMetas; }
    public BigDecimal getMargemDisponivelParaMeta() { return margemDisponivelParaMeta; }
    public boolean isViavel() { return viavel; }
    public String getClassificacao() { return classificacao; }
    public String getMensagem() { return mensagem; }
}
