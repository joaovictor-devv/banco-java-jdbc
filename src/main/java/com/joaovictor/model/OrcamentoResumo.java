package com.joaovictor.model;

import java.math.BigDecimal;

public class OrcamentoResumo {

    private final BigDecimal rendaMensal;
    private final BigDecimal rendaExtra;
    private final BigDecimal rendaTotal;
    private final BigDecimal gastosMensais;
    private final BigDecimal reservaPlanejada;
    private final BigDecimal margemAntesMetas;
    private final BigDecimal comprometimentoMensalMetas;
    private final BigDecimal disponivelAposMetas;
    private final BigDecimal saldoAtual;
    private final BigDecimal podeGastarAgora;
    private final BigDecimal percentualRendaComprometida;
    private final String classificacao;

    public OrcamentoResumo(BigDecimal rendaMensal,
                           BigDecimal rendaExtra,
                           BigDecimal rendaTotal,
                           BigDecimal gastosMensais,
                           BigDecimal reservaPlanejada,
                           BigDecimal margemAntesMetas,
                           BigDecimal comprometimentoMensalMetas,
                           BigDecimal disponivelAposMetas,
                           BigDecimal saldoAtual,
                           BigDecimal podeGastarAgora,
                           BigDecimal percentualRendaComprometida,
                           String classificacao) {
        this.rendaMensal = rendaMensal;
        this.rendaExtra = rendaExtra;
        this.rendaTotal = rendaTotal;
        this.gastosMensais = gastosMensais;
        this.reservaPlanejada = reservaPlanejada;
        this.margemAntesMetas = margemAntesMetas;
        this.comprometimentoMensalMetas = comprometimentoMensalMetas;
        this.disponivelAposMetas = disponivelAposMetas;
        this.saldoAtual = saldoAtual;
        this.podeGastarAgora = podeGastarAgora;
        this.percentualRendaComprometida = percentualRendaComprometida;
        this.classificacao = classificacao;
    }

    public BigDecimal getRendaMensal() { return rendaMensal; }
    public BigDecimal getRendaExtra() { return rendaExtra; }
    public BigDecimal getRendaTotal() { return rendaTotal; }
    public BigDecimal getGastosMensais() { return gastosMensais; }
    public BigDecimal getReservaPlanejada() { return reservaPlanejada; }
    public BigDecimal getMargemAntesMetas() { return margemAntesMetas; }
    public BigDecimal getComprometimentoMensalMetas() { return comprometimentoMensalMetas; }
    public BigDecimal getDisponivelAposMetas() { return disponivelAposMetas; }
    public BigDecimal getSaldoAtual() { return saldoAtual; }
    public BigDecimal getPodeGastarAgora() { return podeGastarAgora; }
    public BigDecimal getPercentualRendaComprometida() { return percentualRendaComprometida; }
    public String getClassificacao() { return classificacao; }
}
