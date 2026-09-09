package com.joaovictor.model;

import java.math.BigDecimal;

public class ProjecaoMensal {

    private final int indiceMes;
    private final String mesReferencia;
    private final BigDecimal rendaTotal;
    private final BigDecimal gastosMensais;
    private final BigDecimal reservaPlanejada;
    private final BigDecimal aporteMetasBase;
    private final BigDecimal aporteExtraMetas;
    private final BigDecimal gastoExtraordinario;
    private final BigDecimal margemMensal;
    private final BigDecimal saldoDisponivelProjetado;
    private final String classificacao;

    public ProjecaoMensal(int indiceMes,
                          String mesReferencia,
                          BigDecimal rendaTotal,
                          BigDecimal gastosMensais,
                          BigDecimal reservaPlanejada,
                          BigDecimal aporteMetasBase,
                          BigDecimal aporteExtraMetas,
                          BigDecimal gastoExtraordinario,
                          BigDecimal margemMensal,
                          BigDecimal saldoDisponivelProjetado,
                          String classificacao) {
        this.indiceMes = indiceMes;
        this.mesReferencia = mesReferencia;
        this.rendaTotal = rendaTotal;
        this.gastosMensais = gastosMensais;
        this.reservaPlanejada = reservaPlanejada;
        this.aporteMetasBase = aporteMetasBase;
        this.aporteExtraMetas = aporteExtraMetas;
        this.gastoExtraordinario = gastoExtraordinario;
        this.margemMensal = margemMensal;
        this.saldoDisponivelProjetado = saldoDisponivelProjetado;
        this.classificacao = classificacao;
    }

    public int getIndiceMes() { return indiceMes; }
    public String getMesReferencia() { return mesReferencia; }
    public BigDecimal getRendaTotal() { return rendaTotal; }
    public BigDecimal getGastosMensais() { return gastosMensais; }
    public BigDecimal getReservaPlanejada() { return reservaPlanejada; }
    public BigDecimal getAporteMetasBase() { return aporteMetasBase; }
    public BigDecimal getAporteExtraMetas() { return aporteExtraMetas; }
    public BigDecimal getGastoExtraordinario() { return gastoExtraordinario; }
    public BigDecimal getMargemMensal() { return margemMensal; }
    public BigDecimal getSaldoDisponivelProjetado() { return saldoDisponivelProjetado; }
    public String getClassificacao() { return classificacao; }
}
