package com.joaovictor.service;

import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.SituacaoFinanceira;

public class SituacaoFinanceiraService {

    private final MotorFinanceiroService motorFinanceiroService;

    public SituacaoFinanceiraService() {
        this.motorFinanceiroService = new MotorFinanceiroService();
    }

    public SituacaoFinanceira analisar() {
        CapacidadeFinanceira capacidade = motorFinanceiroService.calcularCapacidade();

        return new SituacaoFinanceira(
                capacidade.getRendaMensal(),
                capacidade.getGastosMensais(),
                capacidade.getReservaPlanejada(),
                capacidade.getMargemAntesMetas(),
                capacidade.getComprometimentoMensalMetas(),
                capacidade.getMargemAposMetas(),
                capacidade.getCapacidadeGastoMensal(),
                capacidade.getCapacidadeGastoImediato(),
                capacidade.getSaldoAtual(),
                capacidade.getPercentualRendaComprometida(),
                capacidade.getClassificacao()
        );
    }
}
