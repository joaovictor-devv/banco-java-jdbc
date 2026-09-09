package com.joaovictor.dto;

import java.math.BigDecimal;

public class PerfilFinanceiroRequest {

    private String nome;
    private BigDecimal saldoAtual;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getSaldoAtual() { return saldoAtual; }
    public void setSaldoAtual(BigDecimal saldoAtual) { this.saldoAtual = saldoAtual; }
}
