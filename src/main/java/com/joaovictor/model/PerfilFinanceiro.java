package com.joaovictor.model;

import java.math.BigDecimal;

public class PerfilFinanceiro {

    private long id;
    private String nome;
    private BigDecimal saldoAtual;
    private BigDecimal rendaMensal;
    private BigDecimal gastosMensais;
    private BigDecimal valorPlanejadoGuardar;

    public PerfilFinanceiro() {
    }

    public PerfilFinanceiro(long id,
                            String nome,
                            BigDecimal saldoAtual,
                            BigDecimal rendaMensal,
                            BigDecimal gastosMensais,
                            BigDecimal valorPlanejadoGuardar) {
        this.id = id;
        this.nome = nome;
        this.saldoAtual = saldoAtual;
        this.rendaMensal = rendaMensal;
        this.gastosMensais = gastosMensais;
        this.valorPlanejadoGuardar = valorPlanejadoGuardar;
    }

    public PerfilFinanceiro(String nome,
                            BigDecimal saldoAtual,
                            BigDecimal rendaMensal,
                            BigDecimal gastosMensais,
                            BigDecimal valorPlanejadoGuardar) {
        this.nome = nome;
        this.saldoAtual = saldoAtual;
        this.rendaMensal = rendaMensal;
        this.gastosMensais = gastosMensais;
        this.valorPlanejadoGuardar = valorPlanejadoGuardar;
    }

    public long getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getSaldoAtual() { return saldoAtual; }
    public BigDecimal getRendaMensal() { return rendaMensal; }
    public BigDecimal getGastosMensais() { return gastosMensais; }
    public BigDecimal getValorPlanejadoGuardar() { return valorPlanejadoGuardar; }

    public void setId(long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setSaldoAtual(BigDecimal saldoAtual) { this.saldoAtual = saldoAtual; }
    public void setRendaMensal(BigDecimal rendaMensal) { this.rendaMensal = rendaMensal; }
    public void setGastosMensais(BigDecimal gastosMensais) { this.gastosMensais = gastosMensais; }
    public void setValorPlanejadoGuardar(BigDecimal valorPlanejadoGuardar) { this.valorPlanejadoGuardar = valorPlanejadoGuardar; }
}
