package com.joaovictor.service;

import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.repository.PerfilFinanceiroRepository;

import java.math.BigDecimal;

public class SituacaoFinanceiraService {

    private final PerfilFinanceiroRepository perfilRepository;
    private final AnaliseFinanceiraService analiseFinanceiraService;

    public SituacaoFinanceiraService() {
        this.perfilRepository = new PerfilFinanceiroRepository();
        this.analiseFinanceiraService = new AnaliseFinanceiraService();
    }

    public SituacaoFinanceira analisar() {
        PerfilFinanceiro perfil = perfilRepository.buscarUltimoPerfil();

        if (perfil == null) {
            throw new IllegalArgumentException("Nenhum perfil financeiro foi cadastrado ainda.");
        }

        BigDecimal rendaMensal = valor(perfil.getRendaMensal());
        BigDecimal rendaExtra = valor(perfil.getRendaExtra());
        BigDecimal rendaTotal = rendaMensal.add(rendaExtra);

        BigDecimal despesasPlanejadas = somar(
                perfil.getGastoMoradia(),
                perfil.getGastoAgua(),
                perfil.getGastoEnergia(),
                perfil.getGastoInternet(),
                perfil.getGastoTransporte(),
                perfil.getGastoAlimentacao(),
                perfil.getOutrasDespesas()
        );

        BigDecimal valorPlanejadoGuardar = valor(perfil.getValorPlanejadoGuardar());
        BigDecimal margemLivre = rendaTotal.subtract(despesasPlanejadas).subtract(valorPlanejadoGuardar);
        BigDecimal saldoAtual = valor(perfil.getSaldoAtual());

        // Transações continuam disponíveis como histórico opcional, mas não são
        // necessárias para o saldo usado nas decisões do FinIA.
        ResumoFinanceiro resumo = analiseFinanceiraService.gerarResumoDoMesAtual();

        String classificacao = classificar(
                rendaTotal,
                despesasPlanejadas,
                valorPlanejadoGuardar,
                margemLivre,
                saldoAtual
        );

        return new SituacaoFinanceira(
                rendaMensal,
                rendaExtra,
                rendaTotal,
                despesasPlanejadas,
                valorPlanejadoGuardar,
                margemLivre,
                saldoAtual,
                resumo.getSaldoMes(),
                resumo.getTotalEntradas(),
                resumo.getTotalSaidas(),
                classificacao
        );
    }

    private String classificar(BigDecimal rendaTotal,
                               BigDecimal despesasPlanejadas,
                               BigDecimal valorPlanejadoGuardar,
                               BigDecimal margemLivre,
                               BigDecimal saldoAtual) {

        if (rendaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return "SEM_RENDA";
        }

        if (saldoAtual.compareTo(BigDecimal.ZERO) < 0) {
            return "DEFICIT";
        }

        BigDecimal margemAntesDaReserva = rendaTotal.subtract(despesasPlanejadas);

        if (margemAntesDaReserva.compareTo(BigDecimal.ZERO) < 0) {
            return "DESPESAS_ACIMA_DA_RENDA";
        }

        if (valorPlanejadoGuardar.compareTo(margemAntesDaReserva) > 0) {
            return "RESERVA_INVIAVEL";
        }

        if (margemLivre.compareTo(BigDecimal.ZERO) == 0) {
            return "EQUILIBRADA";
        }

        return "SAUDAVEL";
    }

    private BigDecimal somar(BigDecimal... valores) {
        BigDecimal total = BigDecimal.ZERO;

        for (BigDecimal item : valores) {
            total = total.add(valor(item));
        }

        return total;
    }

    private BigDecimal valor(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
