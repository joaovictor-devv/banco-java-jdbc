package com.joaovictor.model;

import java.math.BigDecimal;

public class SituacaoFinanceira {

    private final BigDecimal rendaMensal;
    private final BigDecimal gastosMensais;
    private final BigDecimal valorPlanejadoGuardar;
    private final BigDecimal margemAntesMetas;
    private final BigDecimal comprometimentoMensalMetas;
    private final BigDecimal margemDisponivelAposMetas;
    private final BigDecimal capacidadeGastoMensal;
    private final BigDecimal capacidadeGastoImediato;
    private final BigDecimal saldoAtual;
    private final BigDecimal percentualRendaComprometida;
    private final String classificacao;

    public SituacaoFinanceira(BigDecimal rendaMensal,
                              BigDecimal gastosMensais,
                              BigDecimal valorPlanejadoGuardar,
                              BigDecimal margemAntesMetas,
                              BigDecimal comprometimentoMensalMetas,
                              BigDecimal margemDisponivelAposMetas,
                              BigDecimal capacidadeGastoMensal,
                              BigDecimal capacidadeGastoImediato,
                              BigDecimal saldoAtual,
                              BigDecimal percentualRendaComprometida,
                              String classificacao) {
        this.rendaMensal = rendaMensal;
        this.gastosMensais = gastosMensais;
        this.valorPlanejadoGuardar = valorPlanejadoGuardar;
        this.margemAntesMetas = margemAntesMetas;
        this.comprometimentoMensalMetas = comprometimentoMensalMetas;
        this.margemDisponivelAposMetas = margemDisponivelAposMetas;
        this.capacidadeGastoMensal = capacidadeGastoMensal;
        this.capacidadeGastoImediato = capacidadeGastoImediato;
        this.saldoAtual = saldoAtual;
        this.percentualRendaComprometida = percentualRendaComprometida;
        this.classificacao = classificacao;
    }

    public BigDecimal getRendaMensal() { return rendaMensal; }
    public BigDecimal getGastosMensais() { return gastosMensais; }
    public BigDecimal getValorPlanejadoGuardar() { return valorPlanejadoGuardar; }
    public BigDecimal getMargemAntesMetas() { return margemAntesMetas; }
    public BigDecimal getComprometimentoMensalMetas() { return comprometimentoMensalMetas; }
    public BigDecimal getMargemDisponivelAposMetas() { return margemDisponivelAposMetas; }
    public BigDecimal getCapacidadeGastoMensal() { return capacidadeGastoMensal; }
    public BigDecimal getCapacidadeGastoImediato() { return capacidadeGastoImediato; }
    public BigDecimal getSaldoAtual() { return saldoAtual; }
    public BigDecimal getPercentualRendaComprometida() { return percentualRendaComprometida; }
    public String getClassificacao() { return classificacao; }
}
