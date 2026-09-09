package com.joaovictor.service;

import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;

public class SituacaoFinanceiraService {

    private final MotorFinanceiroService motorFinanceiroService;
    private final AnaliseFinanceiraService analiseFinanceiraService;

    public SituacaoFinanceiraService() {
        this.motorFinanceiroService = new MotorFinanceiroService();
        this.analiseFinanceiraService = new AnaliseFinanceiraService();
    }

    public SituacaoFinanceira analisar() {
        CapacidadeFinanceira capacidade = motorFinanceiroService.calcularCapacidade();
        ResumoFinanceiro resumo = analiseFinanceiraService.gerarResumoDoMesAtual();

        return new SituacaoFinanceira(
                capacidade.getRendaMensal(),
                capacidade.getRendaExtra(),
                capacidade.getRendaTotal(),
                capacidade.getDespesasPlanejadas(),
                capacidade.getReservaPlanejada(),
                capacidade.getMargemAntesMetas(),
                capacidade.getComprometimentoMensalMetas(),
                capacidade.getMargemAposMetas(),
                capacidade.getSaldoAtual(),
                resumo.getSaldoMes(),
                resumo.getTotalEntradas(),
                resumo.getTotalSaidas(),
                capacidade.getClassificacao()
        );
    }
}
