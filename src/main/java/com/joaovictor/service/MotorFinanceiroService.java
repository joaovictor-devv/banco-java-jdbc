package com.joaovictor.service;

import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.repository.PerfilFinanceiroRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MotorFinanceiroService {

    private static final BigDecimal CEM = new BigDecimal("100");
    private static final BigDecimal LIMITE_ATENCAO_PERCENTUAL = new BigDecimal("80");

    private final PerfilFinanceiroRepository perfilRepository;
    private final CompromissoMetasService compromissoMetasService;

    public MotorFinanceiroService() {
        this.perfilRepository = new PerfilFinanceiroRepository();
        this.compromissoMetasService = new CompromissoMetasService();
    }

    public CapacidadeFinanceira calcularCapacidade() {
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

        BigDecimal reservaPlanejada = valor(perfil.getValorPlanejadoGuardar());
        BigDecimal comprometimentoMensalMetas = compromissoMetasService.calcularComprometimentoMensalTotal();
        BigDecimal totalCompromissosMensais = despesasPlanejadas
                .add(reservaPlanejada)
                .add(comprometimentoMensalMetas);

        BigDecimal margemAntesMetas = rendaTotal
                .subtract(despesasPlanejadas)
                .subtract(reservaPlanejada);

        BigDecimal margemAposMetas = rendaTotal.subtract(totalCompromissosMensais);
        BigDecimal capacidadeGastoMensal = maxZero(margemAposMetas);
        BigDecimal saldoAtual = valor(perfil.getSaldoAtual());
        BigDecimal capacidadeGastoImediato = menor(maxZero(saldoAtual), capacidadeGastoMensal);
        BigDecimal percentualRendaComprometida = calcularPercentual(totalCompromissosMensais, rendaTotal);

        String classificacao = classificar(
                rendaTotal,
                despesasPlanejadas,
                reservaPlanejada,
                totalCompromissosMensais,
                saldoAtual,
                percentualRendaComprometida
        );

        String mensagem = gerarMensagem(
                classificacao,
                capacidadeGastoMensal,
                capacidadeGastoImediato,
                percentualRendaComprometida
        );

        return new CapacidadeFinanceira(
                rendaMensal,
                rendaExtra,
                rendaTotal,
                despesasPlanejadas,
                reservaPlanejada,
                comprometimentoMensalMetas,
                totalCompromissosMensais,
                margemAntesMetas,
                margemAposMetas,
                capacidadeGastoMensal,
                saldoAtual,
                capacidadeGastoImediato,
                percentualRendaComprometida,
                classificacao,
                mensagem
        );
    }

    private String classificar(BigDecimal rendaTotal,
                               BigDecimal despesasPlanejadas,
                               BigDecimal reservaPlanejada,
                               BigDecimal totalCompromissosMensais,
                               BigDecimal saldoAtual,
                               BigDecimal percentualRendaComprometida) {

        if (rendaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return "SEM_RENDA";
        }

        if (saldoAtual.compareTo(BigDecimal.ZERO) < 0) {
            return "DEFICIT";
        }

        if (despesasPlanejadas.compareTo(rendaTotal) > 0) {
            return "DESPESAS_ACIMA_DA_RENDA";
        }

        if (despesasPlanejadas.add(reservaPlanejada).compareTo(rendaTotal) > 0) {
            return "RESERVA_INVIAVEL";
        }

        if (totalCompromissosMensais.compareTo(rendaTotal) > 0) {
            return "METAS_ACIMA_DA_CAPACIDADE";
        }

        if (totalCompromissosMensais.compareTo(rendaTotal) == 0) {
            return "EQUILIBRADA";
        }

        if (percentualRendaComprometida.compareTo(LIMITE_ATENCAO_PERCENTUAL) >= 0) {
            return "APERTADA";
        }

        return "SAUDAVEL";
    }

    private String gerarMensagem(String classificacao,
                                 BigDecimal capacidadeGastoMensal,
                                 BigDecimal capacidadeGastoImediato,
                                 BigDecimal percentualRendaComprometida) {
        return switch (classificacao) {
            case "SEM_RENDA" -> "Não há renda disponível para calcular uma capacidade de gasto segura.";
            case "DEFICIT" -> "O saldo atual está negativo. Novos gastos não são recomendados.";
            case "DESPESAS_ACIMA_DA_RENDA" -> "As despesas planejadas já ultrapassam a renda mensal.";
            case "RESERVA_INVIAVEL" -> "Despesas e valor planejado para guardar ultrapassam a renda mensal.";
            case "METAS_ACIMA_DA_CAPACIDADE" -> "As metas, somadas aos demais compromissos, ultrapassam a renda mensal.";
            case "EQUILIBRADA" -> "Toda a renda mensal está comprometida. Não há margem para novos gastos.";
            case "APERTADA" -> "A renda está " + percentualRendaComprometida
                    + "% comprometida. Há margem, mas novos gastos exigem cautela.";
            default -> "A capacidade estimada para novos gastos é de até R$ "
                    + capacidadeGastoMensal + " por mês e até R$ "
                    + capacidadeGastoImediato + " considerando também o saldo atual.";
        };
    }

    private BigDecimal calcularPercentual(BigDecimal parte, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return parte.multiply(CEM)
                .divide(total, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal somar(BigDecimal... valores) {
        BigDecimal total = BigDecimal.ZERO;

        for (BigDecimal item : valores) {
            total = total.add(valor(item));
        }

        return total;
    }

    private BigDecimal menor(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    private BigDecimal maxZero(BigDecimal valor) {
        return valor.compareTo(BigDecimal.ZERO) > 0 ? valor : BigDecimal.ZERO;
    }

    private BigDecimal valor(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
