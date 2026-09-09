package com.joaovictor.dto;

import java.math.BigDecimal;

public class EventoSimulacaoRequest {

    private Integer mes;
    private String tipo;
    private BigDecimal valor;
    private Long metaId;

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Long getMetaId() { return metaId; }
    public void setMetaId(Long metaId) { this.metaId = metaId; }
}
