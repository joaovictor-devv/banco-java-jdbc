package com.joaovictor.dto;

import java.math.BigDecimal;

public class OrcamentoRequest {

    private BigDecimal rendaMensal;
    private BigDecimal rendaExtra;
    private BigDecimal gastosMensais;
    private BigDecimal valorPlanejadoGuardar;

    public BigDecimal getRendaMensal() { return rendaMensal; }
    public void setRendaMensal(BigDecimal rendaMensal) { this.rendaMensal = rendaMensal; }

    public BigDecimal getRendaExtra() { return rendaExtra; }
    public void setRendaExtra(BigDecimal rendaExtra) { this.rendaExtra = rendaExtra; }

    public BigDecimal getGastosMensais() { return gastosMensais; }
    public void setGastosMensais(BigDecimal gastosMensais) { this.gastosMensais = gastosMensais; }

    public BigDecimal getValorPlanejadoGuardar() { return valorPlanejadoGuardar; }
    public void setValorPlanejadoGuardar(BigDecimal valorPlanejadoGuardar) { this.valorPlanejadoGuardar = valorPlanejadoGuardar; }
}
