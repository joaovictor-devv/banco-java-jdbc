package com.joaovictor.service;

import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.repository.PerfilFinanceiroRepository;
import com.joaovictor.repository.TransacaoRepository;

import java.math.BigDecimal;
import java.util.List;

public class SituacaoFinanceiraService {

    private final PerfilFinanceiroRepository perfilRepository;
    private final TransacaoRepository transacaoRepository;

    public SituacaoFinanceiraService() {
        this.perfilRepository = new PerfilFinanceiroRepository();
        this.transacaoRepository = new TransacaoRepository();
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

        ResumoFinanceiro resumo = new AnaliseFinanceiraService().gerarResumoDoMesAtual();

        String classificacao = classificar(
                rendaTotal,
                despesasPlanejadas,
                valorPlanejadoGuardar,
                resumo.getSaldoMes()
        );

        return new SituacaoFinanceira(
                rendaMensal,
                rendaExtra,
                rendaTotal,
                despesasPlanejadas,
                valorPlanejadoGuardar,
                margemLivre,
                resumo.getSaldoMes(),
                resumo.getTotalEntradas(),
                resumo.getTotalSaidas(),
                classificacao
        );
    }

    private String classificar(BigDecimal rendaTotal,
                               BigDecimal despesasPlanejadas,
                               BigDecimal valorPlanejadoGuardar,
                               BigDecimal saldoMesAtual) {

        if (rendaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return "SEM_RENDA";
        }

        if (saldoMesAtual.compareTo(BigDecimal.ZERO) < 0) {
            return "DEFICIT";
        }

        BigDecimal margemAntesDaReserva = rendaTotal.subtract(despesasPlanejadas);

        if (margemAntesDaReserva.compareTo(BigDecimal.ZERO) < 0) {
            return "DESPESAS_ACIMA_DA_RENDA";
        }

        if (valorPlanejadoGuardar.compareTo(margemAntesDaReserva) > 0) {
            return "RESERVA_INVIAVEL";
        }

        BigDecimal margemLivre = margemAntesDaReserva.subtract(valorPlanejadoGuardar);

        if (margemLivre.compareTo(BigDecimal.ZERO) == 0) {
            return "EQUILIBRADA";
        }

        return "SAUDAVEL";
    }

    private BigDecimal somar(BigDecimal... valores) {
        BigDecimal total = BigDecimal.ZERO;

        for (BigDecimal valor : valores) {
            total = total.add(valor(valor));
        }

        return total;
    }

    private BigDecimal valor(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
