package com.joaovictor.model;

import java.math.BigDecimal;

public class AnaliseGasto {

    private final BigDecimal valorGasto;
    private final BigDecimal saldoAtual;
    private final BigDecimal margemLivreAtual;
    private final BigDecimal comprometimentoMensalMetas;
    private final BigDecimal margemDisponivelAposMetas;
    private final BigDecimal margemLivreAposGasto;
    private final boolean saldoSuficiente;
    private final boolean recomendado;
    private final String classificacao;
    private final String mensagem;

    public AnaliseGasto(BigDecimal valorGasto,
                        BigDecimal saldoAtual,
                        BigDecimal margemLivreAtual,
                        BigDecimal comprometimentoMensalMetas,
                        BigDecimal margemDisponivelAposMetas,
                        BigDecimal margemLivreAposGasto,
                        boolean saldoSuficiente,
                        boolean recomendado,
                        String classificacao,
                        String mensagem) {
        this.valorGasto = valorGasto;
        this.saldoAtual = saldoAtual;
        this.margemLivreAtual = margemLivreAtual;
        this.comprometimentoMensalMetas = comprometimentoMensalMetas;
        this.margemDisponivelAposMetas = margemDisponivelAposMetas;
        this.margemLivreAposGasto = margemLivreAposGasto;
        this.saldoSuficiente = saldoSuficiente;
        this.recomendado = recomendado;
        this.classificacao = classificacao;
        this.mensagem = mensagem;
    }

    public BigDecimal getValorGasto() { return valorGasto; }
    public BigDecimal getSaldoAtual() { return saldoAtual; }
    public BigDecimal getMargemLivreAtual() { return margemLivreAtual; }
    public BigDecimal getComprometimentoMensalMetas() { return comprometimentoMensalMetas; }
    public BigDecimal getMargemDisponivelAposMetas() { return margemDisponivelAposMetas; }
    public BigDecimal getMargemLivreAposGasto() { return margemLivreAposGasto; }
    public boolean isSaldoSuficiente() { return saldoSuficiente; }
    public boolean isRecomendado() { return recomendado; }
    public String getClassificacao() { return classificacao; }
    public String getMensagem() { return mensagem; }
}
