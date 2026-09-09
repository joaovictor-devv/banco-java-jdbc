package com.joaovictor.service;

import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.repository.PerfilFinanceiroRepository;

import java.math.BigDecimal;

public class SituacaoFinanceiraService {

    private static final BigDecimal LIMITE_ATENCAO = new BigDecimal("0.80");

    private final PerfilFinanceiroRepository perfilRepository;
    private final AnaliseFinanceiraService analiseFinanceiraService;
    private final CompromissoMetasService compromissoMetasService;

    public SituacaoFinanceiraService() {
        this.perfilRepository = new PerfilFinanceiroRepository();
        this.analiseFinanceiraService = new AnaliseFinanceiraService();
        this.compromissoMetasService = new CompromissoMetasService();
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
        BigDecimal comprometimentoMensalMetas = compromissoMetasService.calcularComprometimentoMensalTotal();
        BigDecimal margemDisponivelAposMetas = margemLivre.subtract(comprometimentoMensalMetas);
        BigDecimal saldoAtual = valor(perfil.getSaldoAtual());

        ResumoFinanceiro resumo = analiseFinanceiraService.gerarResumoDoMesAtual();

        String classificacao = classificar(
                rendaTotal,
                despesasPlanejadas,
                valorPlanejadoGuardar,
                margemLivre,
                comprometimentoMensalMetas,
                saldoAtual
        );

        return new SituacaoFinanceira(
                rendaMensal,
                rendaExtra,
                rendaTotal,
                despesasPlanejadas,
                valorPlanejadoGuardar,
                margemLivre,
                comprometimentoMensalMetas,
                margemDisponivelAposMetas,
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
                               BigDecimal comprometimentoMensalMetas,
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

        if (comprometimentoMensalMetas.compareTo(margemLivre) > 0) {
            return "METAS_ACIMA_DA_CAPACIDADE";
        }

        if (margemLivre.compareTo(BigDecimal.ZERO) == 0) {
            return "EQUILIBRADA";
        }

        if (comprometimentoMensalMetas.compareTo(BigDecimal.ZERO) > 0
                && comprometimentoMensalMetas.compareTo(margemLivre.multiply(LIMITE_ATENCAO)) > 0) {
            return "APERTADA_POR_METAS";
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
