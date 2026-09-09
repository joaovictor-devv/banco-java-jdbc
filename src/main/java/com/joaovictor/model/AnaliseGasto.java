package com.joaovictor.model;

import java.math.BigDecimal;

public class AnaliseGasto {

    private final BigDecimal valorGasto;
    private final BigDecimal margemLivreAtual;
    private final BigDecimal margemLivreAposGasto;
    private final boolean saldoSuficiente;
    private final boolean recomendado;
    private final String classificacao;
    private final String mensagem;

    public AnaliseGasto(BigDecimal valorGasto,
                        BigDecimal margemLivreAtual,
                        BigDecimal margemLivreAposGasto,
                        boolean saldoSuficiente,
                        boolean recomendado,
                        String classificacao,
                        String mensagem) {
        this.valorGasto = valorGasto;
        this.margemLivreAtual = margemLivreAtual;
        this.margemLivreAposGasto = margemLivreAposGasto;
        this.saldoSuficiente = saldoSuficiente;
        this.recomendado = recomendado;
        this.classificacao = classificacao;
        this.mensagem = mensagem;
    }

    public BigDecimal getValorGasto() { return valorGasto; }
    public BigDecimal getMargemLivreAtual() { return margemLivreAtual; }
    public BigDecimal getMargemLivreAposGasto() { return margemLivreAposGasto; }
    public boolean isSaldoSuficiente() { return saldoSuficiente; }
    public boolean isRecomendado() { return recomendado; }
    public String getClassificacao() { return classificacao; }
    public String getMensagem() { return mensagem; }
}
