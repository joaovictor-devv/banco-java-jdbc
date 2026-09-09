package com.joaovictor.model;

import java.math.BigDecimal;
import java.util.List;

public class ResultadoSimulacaoFinanceira {

    private final String nomeCenario;
    private final int meses;
    private final boolean dadosPersistidos;
    private final BigDecimal saldoInicial;
    private final BigDecimal saldoFinalProjetado;
    private final BigDecimal variacaoSaldo;
    private final BigDecimal totalReservaPlanejada;
    private final BigDecimal totalAportadoMetas;
    private final BigDecimal totalGastosExtraordinarios;
    private final String classificacaoFinal;
    private final String mensagem;
    private final CapacidadeFinanceira situacaoAtual;
    private final List<ProjecaoMensal> evolucaoMensal;
    private final List<ProjecaoMeta> projecoesMetas;

    public ResultadoSimulacaoFinanceira(String nomeCenario,
                                        int meses,
                                        boolean dadosPersistidos,
                                        BigDecimal saldoInicial,
                                        BigDecimal saldoFinalProjetado,
                                        BigDecimal variacaoSaldo,
                                        BigDecimal totalReservaPlanejada,
                                        BigDecimal totalAportadoMetas,
                                        BigDecimal totalGastosExtraordinarios,
                                        String classificacaoFinal,
                                        String mensagem,
                                        CapacidadeFinanceira situacaoAtual,
                                        List<ProjecaoMensal> evolucaoMensal,
                                        List<ProjecaoMeta> projecoesMetas) {
        this.nomeCenario = nomeCenario;
        this.meses = meses;
        this.dadosPersistidos = dadosPersistidos;
        this.saldoInicial = saldoInicial;
        this.saldoFinalProjetado = saldoFinalProjetado;
        this.variacaoSaldo = variacaoSaldo;
        this.totalReservaPlanejada = totalReservaPlanejada;
        this.totalAportadoMetas = totalAportadoMetas;
        this.totalGastosExtraordinarios = totalGastosExtraordinarios;
        this.classificacaoFinal = classificacaoFinal;
        this.mensagem = mensagem;
        this.situacaoAtual = situacaoAtual;
        this.evolucaoMensal = evolucaoMensal;
        this.projecoesMetas = projecoesMetas;
    }

    public String getNomeCenario() { return nomeCenario; }
    public int getMeses() { return meses; }
    public boolean isDadosPersistidos() { return dadosPersistidos; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public BigDecimal getSaldoFinalProjetado() { return saldoFinalProjetado; }
    public BigDecimal getVariacaoSaldo() { return variacaoSaldo; }
    public BigDecimal getTotalReservaPlanejada() { return totalReservaPlanejada; }
    public BigDecimal getTotalAportadoMetas() { return totalAportadoMetas; }
    public BigDecimal getTotalGastosExtraordinarios() { return totalGastosExtraordinarios; }
    public String getClassificacaoFinal() { return classificacaoFinal; }
    public String getMensagem() { return mensagem; }
    public CapacidadeFinanceira getSituacaoAtual() { return situacaoAtual; }
    public List<ProjecaoMensal> getEvolucaoMensal() { return evolucaoMensal; }
    public List<ProjecaoMeta> getProjecoesMetas() { return projecoesMetas; }
}
