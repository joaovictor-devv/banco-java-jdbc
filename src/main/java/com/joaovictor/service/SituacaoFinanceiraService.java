package com.joaovictor.service;

import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.SituacaoFinanceira;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SituacaoFinanceiraService {

    private final MotorFinanceiroService motorFinanceiroService;

    public SituacaoFinanceiraService() {
        this(new MotorFinanceiroService());
    }

    @Autowired
    public SituacaoFinanceiraService(MotorFinanceiroService motorFinanceiroService) {
        this.motorFinanceiroService = motorFinanceiroService;
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
