package com.joaovictor.model;

import java.math.BigDecimal;

public class CapacidadeFinanceira {

    private final BigDecimal rendaMensal;
    private final BigDecimal rendaExtra;
    private final BigDecimal rendaTotal;
    private final BigDecimal despesasPlanejadas;
    private final BigDecimal reservaPlanejada;
    private final BigDecimal comprometimentoMensalMetas;
    private final BigDecimal totalCompromissosMensais;
    private final BigDecimal margemAntesMetas;
    private final BigDecimal margemAposMetas;
    private final BigDecimal capacidadeGastoMensal;
    private final BigDecimal saldoAtual;
    private final BigDecimal capacidadeGastoImediato;
    private final BigDecimal percentualRendaComprometida;
    private final String classificacao;
    private final String mensagem;

    public CapacidadeFinanceira(BigDecimal rendaMensal,
                                BigDecimal rendaExtra,
                                BigDecimal rendaTotal,
                                BigDecimal despesasPlanejadas,
                                BigDecimal reservaPlanejada,
                                BigDecimal comprometimentoMensalMetas,
                                BigDecimal totalCompromissosMensais,
                                BigDecimal margemAntesMetas,
                                BigDecimal margemAposMetas,
                                BigDecimal capacidadeGastoMensal,
                                BigDecimal saldoAtual,
                                BigDecimal capacidadeGastoImediato,
                                BigDecimal percentualRendaComprometida,
                                String classificacao,
                                String mensagem) {
        this.rendaMensal = rendaMensal;
        this.rendaExtra = rendaExtra;
        this.rendaTotal = rendaTotal;
        this.despesasPlanejadas = despesasPlanejadas;
        this.reservaPlanejada = reservaPlanejada;
        this.comprometimentoMensalMetas = comprometimentoMensalMetas;
        this.totalCompromissosMensais = totalCompromissosMensais;
        this.margemAntesMetas = margemAntesMetas;
        this.margemAposMetas = margemAposMetas;
        this.capacidadeGastoMensal = capacidadeGastoMensal;
        this.saldoAtual = saldoAtual;
        this.capacidadeGastoImediato = capacidadeGastoImediato;
        this.percentualRendaComprometida = percentualRendaComprometida;
        this.classificacao = classificacao;
        this.mensagem = mensagem;
    }

    public BigDecimal getRendaMensal() { return rendaMensal; }
    public BigDecimal getRendaExtra() { return rendaExtra; }
    public BigDecimal getRendaTotal() { return rendaTotal; }
    public BigDecimal getDespesasPlanejadas() { return despesasPlanejadas; }
    public BigDecimal getReservaPlanejada() { return reservaPlanejada; }
    public BigDecimal getComprometimentoMensalMetas() { return comprometimentoMensalMetas; }
    public BigDecimal getTotalCompromissosMensais() { return totalCompromissosMensais; }
    public BigDecimal getMargemAntesMetas() { return margemAntesMetas; }
    public BigDecimal getMargemAposMetas() { return margemAposMetas; }
    public BigDecimal getCapacidadeGastoMensal() { return capacidadeGastoMensal; }
    public BigDecimal getSaldoAtual() { return saldoAtual; }
    public BigDecimal getCapacidadeGastoImediato() { return capacidadeGastoImediato; }
    public BigDecimal getPercentualRendaComprometida() { return percentualRendaComprometida; }
    public String getClassificacao() { return classificacao; }
    public String getMensagem() { return mensagem; }
}
