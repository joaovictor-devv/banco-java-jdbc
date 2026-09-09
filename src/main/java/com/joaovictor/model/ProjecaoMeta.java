package com.joaovictor.model;

import java.math.BigDecimal;

public class ProjecaoMeta {

    private final long id;
    private final String nome;
    private final BigDecimal valorAtual;
    private final BigDecimal valorAlvo;
    private final BigDecimal valorProjetado;
    private final BigDecimal progressoAtualPercentual;
    private final BigDecimal progressoProjetadoPercentual;
    private final boolean concluidaNoPeriodo;
    private final Integer mesConclusaoNaSimulacao;
    private final String mesConclusaoReferencia;
    private final String impacto;

    public ProjecaoMeta(long id,
                        String nome,
                        BigDecimal valorAtual,
                        BigDecimal valorAlvo,
                        BigDecimal valorProjetado,
                        BigDecimal progressoAtualPercentual,
                        BigDecimal progressoProjetadoPercentual,
                        boolean concluidaNoPeriodo,
                        Integer mesConclusaoNaSimulacao,
                        String mesConclusaoReferencia,
                        String impacto) {
        this.id = id;
        this.nome = nome;
        this.valorAtual = valorAtual;
        this.valorAlvo = valorAlvo;
        this.valorProjetado = valorProjetado;
        this.progressoAtualPercentual = progressoAtualPercentual;
        this.progressoProjetadoPercentual = progressoProjetadoPercentual;
        this.concluidaNoPeriodo = concluidaNoPeriodo;
        this.mesConclusaoNaSimulacao = mesConclusaoNaSimulacao;
        this.mesConclusaoReferencia = mesConclusaoReferencia;
        this.impacto = impacto;
    }

    public long getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getValorAtual() { return valorAtual; }
    public BigDecimal getValorAlvo() { return valorAlvo; }
    public BigDecimal getValorProjetado() { return valorProjetado; }
    public BigDecimal getProgressoAtualPercentual() { return progressoAtualPercentual; }
    public BigDecimal getProgressoProjetadoPercentual() { return progressoProjetadoPercentual; }
    public boolean isConcluidaNoPeriodo() { return concluidaNoPeriodo; }
    public Integer getMesConclusaoNaSimulacao() { return mesConclusaoNaSimulacao; }
    public String getMesConclusaoReferencia() { return mesConclusaoReferencia; }
    public String getImpacto() { return impacto; }
}
