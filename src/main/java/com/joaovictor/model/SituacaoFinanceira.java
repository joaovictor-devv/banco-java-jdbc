package com.joaovictor.model;

import java.math.BigDecimal;

public class SituacaoFinanceira {

    private final BigDecimal rendaMensal;
    private final BigDecimal rendaExtra;
    private final BigDecimal rendaTotal;
    private final BigDecimal despesasPlanejadas;
    private final BigDecimal valorPlanejadoGuardar;
    private final BigDecimal margemLivre;
    private final BigDecimal saldoMesAtual;
    private final BigDecimal entradasMesAtual;
    private final BigDecimal saidasMesAtual;
    private final String classificacao;

    public SituacaoFinanceira(BigDecimal rendaMensal,
                              BigDecimal rendaExtra,
                              BigDecimal rendaTotal,
                              BigDecimal despesasPlanejadas,
                              BigDecimal valorPlanejadoGuardar,
                              BigDecimal margemLivre,
                              BigDecimal saldoMesAtual,
                              BigDecimal entradasMesAtual,
                              BigDecimal saidasMesAtual,
                              String classificacao) {
        this.rendaMensal = rendaMensal;
        this.rendaExtra = rendaExtra;
        this.rendaTotal = rendaTotal;
        this.despesasPlanejadas = despesasPlanejadas;
        this.valorPlanejadoGuardar = valorPlanejadoGuardar;
        this.margemLivre = margemLivre;
        this.saldoMesAtual = saldoMesAtual;
        this.entradasMesAtual = entradasMesAtual;
        this.saidasMesAtual = saidasMesAtual;
        this.classificacao = classificacao;
    }

    public BigDecimal getRendaMensal() { return rendaMensal; }
    public BigDecimal getRendaExtra() { return rendaExtra; }
    public BigDecimal getRendaTotal() { return rendaTotal; }
    public BigDecimal getDespesasPlanejadas() { return despesasPlanejadas; }
    public BigDecimal getValorPlanejadoGuardar() { return valorPlanejadoGuardar; }
    public BigDecimal getMargemLivre() { return margemLivre; }
    public BigDecimal getSaldoMesAtual() { return saldoMesAtual; }
    public BigDecimal getEntradasMesAtual() { return entradasMesAtual; }
    public BigDecimal getSaidasMesAtual() { return saidasMesAtual; }
    public String getClassificacao() { return classificacao; }
}
