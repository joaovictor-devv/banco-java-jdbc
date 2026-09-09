package com.joaovictor.service;

import com.joaovictor.exception.RecursoNaoEncontradoException;
import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.repository.PerfilFinanceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MotorFinanceiroService {

    private static final BigDecimal CEM = new BigDecimal("100");
    private static final BigDecimal LIMITE_ATENCAO_PERCENTUAL = new BigDecimal("80");

    private final PerfilFinanceiroRepository perfilRepository;
    private final CompromissoMetasService compromissoMetasService;

    public MotorFinanceiroService() {
        this(new PerfilFinanceiroRepository(), new CompromissoMetasService());
    }

    @Autowired
    public MotorFinanceiroService(PerfilFinanceiroRepository perfilRepository,
                                  CompromissoMetasService compromissoMetasService) {
        this.perfilRepository = perfilRepository;
        this.compromissoMetasService = compromissoMetasService;
    }

    public CapacidadeFinanceira calcularCapacidade() {
        PerfilFinanceiro perfil = perfilRepository.buscarUltimoPerfil();

        if (perfil == null) {
            throw new RecursoNaoEncontradoException("Nenhum perfil financeiro foi cadastrado ainda.");
        }

        BigDecimal comprometimentoMensalMetas = compromissoMetasService.calcularComprometimentoMensalTotal();
        return calcularCapacidade(perfil, comprometimentoMensalMetas);
    }

    public CapacidadeFinanceira calcularCapacidade(PerfilFinanceiro perfil,
                                                    BigDecimal comprometimentoMensalMetas) {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil financeiro é obrigatório para o cálculo.");
        }

        BigDecimal rendaMensal = valor(perfil.getRendaMensal());
        BigDecimal gastosMensais = valor(perfil.getGastosMensais());
        BigDecimal reservaPlanejada = valor(perfil.getValorPlanejadoGuardar());
        BigDecimal metas = valor(comprometimentoMensalMetas);
        BigDecimal saldoAtual = valor(perfil.getSaldoAtual());

        BigDecimal totalCompromissosMensais = gastosMensais
                .add(reservaPlanejada)
                .add(metas);

        BigDecimal margemAntesMetas = rendaMensal
                .subtract(gastosMensais)
                .subtract(reservaPlanejada);

        BigDecimal margemAposMetas = rendaMensal.subtract(totalCompromissosMensais);
        BigDecimal capacidadeGastoMensal = maxZero(margemAposMetas);
        BigDecimal capacidadeGastoImediato = menor(maxZero(saldoAtual), capacidadeGastoMensal);
        BigDecimal percentualRendaComprometida = calcularPercentual(totalCompromissosMensais, rendaMensal);

        String classificacao = classificar(
                rendaMensal,
                gastosMensais,
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
                gastosMensais,
                reservaPlanejada,
                metas,
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

    private String classificar(BigDecimal rendaMensal,
                               BigDecimal gastosMensais,
                               BigDecimal reservaPlanejada,
                               BigDecimal totalCompromissosMensais,
                               BigDecimal saldoAtual,
                               BigDecimal percentualRendaComprometida) {

        if (rendaMensal.compareTo(BigDecimal.ZERO) <= 0) {
            return "SEM_RENDA";
        }

        if (saldoAtual.compareTo(BigDecimal.ZERO) < 0) {
            return "DEFICIT";
        }

        if (gastosMensais.compareTo(rendaMensal) > 0) {
            return "GASTOS_ACIMA_DA_RENDA";
        }

        if (gastosMensais.add(reservaPlanejada).compareTo(rendaMensal) > 0) {
            return "RESERVA_INVIAVEL";
        }

        if (totalCompromissosMensais.compareTo(rendaMensal) > 0) {
            return "METAS_ACIMA_DA_CAPACIDADE";
        }

        if (totalCompromissosMensais.compareTo(rendaMensal) == 0) {
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
            case "GASTOS_ACIMA_DA_RENDA" -> "Os gastos mensais já ultrapassam a renda mensal.";
            case "RESERVA_INVIAVEL" -> "Gastos e valor planejado para guardar ultrapassam a renda mensal.";
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

    private BigDecimal menor(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    private BigDecimal maxZero(BigDecimal numero) {
        return numero.compareTo(BigDecimal.ZERO) > 0 ? numero : BigDecimal.ZERO;
    }

    private BigDecimal valor(BigDecimal numero) {
        return numero != null ? numero : BigDecimal.ZERO;
    }
}
