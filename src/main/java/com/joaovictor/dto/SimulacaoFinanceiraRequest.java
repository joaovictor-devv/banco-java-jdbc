package com.joaovictor.dto;

import java.math.BigDecimal;

public class SimulacaoFinanceiraRequest {

    private String nomeCenario;
    private Integer meses;
    private BigDecimal rendaMensal;
    private BigDecimal rendaExtraMensal;
    private BigDecimal gastosMensais;
    private BigDecimal gastoExtraordinario;
    private Integer mesGastoExtraordinario;
    private BigDecimal aporteExtraMetasMensal;
    private Long metaPrioritariaId;

    public String getNomeCenario() { return nomeCenario; }
    public void setNomeCenario(String nomeCenario) { this.nomeCenario = nomeCenario; }

    public Integer getMeses() { return meses; }
    public void setMeses(Integer meses) { this.meses = meses; }

    public BigDecimal getRendaMensal() { return rendaMensal; }
    public void setRendaMensal(BigDecimal rendaMensal) { this.rendaMensal = rendaMensal; }

    public BigDecimal getRendaExtraMensal() { return rendaExtraMensal; }
    public void setRendaExtraMensal(BigDecimal rendaExtraMensal) { this.rendaExtraMensal = rendaExtraMensal; }

    public BigDecimal getGastosMensais() { return gastosMensais; }
    public void setGastosMensais(BigDecimal gastosMensais) { this.gastosMensais = gastosMensais; }

    public BigDecimal getGastoExtraordinario() { return gastoExtraordinario; }
    public void setGastoExtraordinario(BigDecimal gastoExtraordinario) { this.gastoExtraordinario = gastoExtraordinario; }

    public Integer getMesGastoExtraordinario() { return mesGastoExtraordinario; }
    public void setMesGastoExtraordinario(Integer mesGastoExtraordinario) { this.mesGastoExtraordinario = mesGastoExtraordinario; }

    public BigDecimal getAporteExtraMetasMensal() { return aporteExtraMetasMensal; }
    public void setAporteExtraMetasMensal(BigDecimal aporteExtraMetasMensal) { this.aporteExtraMetasMensal = aporteExtraMetasMensal; }

    public Long getMetaPrioritariaId() { return metaPrioritariaId; }
    public void setMetaPrioritariaId(Long metaPrioritariaId) { this.metaPrioritariaId = metaPrioritariaId; }
}
