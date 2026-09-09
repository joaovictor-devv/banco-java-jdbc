package com.joaovictor.service;

import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.PerfilFinanceiro;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MotorFinanceiroServiceTest {

    private final MotorFinanceiroService service = new MotorFinanceiroService();

    @Test
    void deveCalcularCapacidadeComMetas() {
        PerfilFinanceiro perfil = perfil("1200", "2800", "1400", "400");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, dinheiro("450"));

        assertThat(resultado.getTotalCompromissosMensais()).isEqualByComparingTo("2250");
        assertThat(resultado.getMargemAntesMetas()).isEqualByComparingTo("1000");
        assertThat(resultado.getMargemAposMetas()).isEqualByComparingTo("550");
        assertThat(resultado.getCapacidadeGastoMensal()).isEqualByComparingTo("550");
        assertThat(resultado.getCapacidadeGastoImediato()).isEqualByComparingTo("550");
        assertThat(resultado.getPercentualRendaComprometida()).isEqualByComparingTo("80.36");
        assertThat(resultado.getClassificacao()).isEqualTo("APERTADA");
    }

    @Test
    void deveLimitarGastoImediatoAoSaldoAtual() {
        PerfilFinanceiro perfil = perfil("200", "3000", "1000", "300");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, dinheiro("200"));

        assertThat(resultado.getCapacidadeGastoMensal()).isEqualByComparingTo("1500");
        assertThat(resultado.getCapacidadeGastoImediato()).isEqualByComparingTo("200");
        assertThat(resultado.getClassificacao()).isEqualTo("SAUDAVEL");
    }

    @Test
    void deveClassificarGastosAcimaDaRenda() {
        PerfilFinanceiro perfil = perfil("500", "2000", "2200", "0");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, BigDecimal.ZERO);

        assertThat(resultado.getClassificacao()).isEqualTo("GASTOS_ACIMA_DA_RENDA");
        assertThat(resultado.getCapacidadeGastoMensal()).isZero();
    }

    @Test
    void deveClassificarReservaInviavel() {
        PerfilFinanceiro perfil = perfil("500", "2000", "1600", "500");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, BigDecimal.ZERO);

        assertThat(resultado.getClassificacao()).isEqualTo("RESERVA_INVIAVEL");
        assertThat(resultado.getMargemAntesMetas()).isEqualByComparingTo("-100");
    }

    @Test
    void deveClassificarMetasAcimaDaCapacidade() {
        PerfilFinanceiro perfil = perfil("1000", "2000", "1000", "300");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, dinheiro("800"));

        assertThat(resultado.getClassificacao()).isEqualTo("METAS_ACIMA_DA_CAPACIDADE");
        assertThat(resultado.getMargemAposMetas()).isEqualByComparingTo("-100");
        assertThat(resultado.getCapacidadeGastoMensal()).isZero();
    }

    @Test
    void deveClassificarOrcamentoExatamenteComprometidoComoEquilibrado() {
        PerfilFinanceiro perfil = perfil("1000", "2000", "1000", "400");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, dinheiro("600"));

        assertThat(resultado.getClassificacao()).isEqualTo("EQUILIBRADA");
        assertThat(resultado.getMargemAposMetas()).isZero();
    }

    @Test
    void deveClassificarSituacaoSaudavel() {
        PerfilFinanceiro perfil = perfil("3000", "3000", "1000", "300");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, dinheiro("200"));

        assertThat(resultado.getClassificacao()).isEqualTo("SAUDAVEL");
        assertThat(resultado.getMargemAposMetas()).isEqualByComparingTo("1500");
        assertThat(resultado.getPercentualRendaComprometida()).isEqualByComparingTo("50.00");
    }

    @Test
    void deveClassificarAusenciaDeRenda() {
        PerfilFinanceiro perfil = perfil("100", "0", "0", "0");

        CapacidadeFinanceira resultado = service.calcularCapacidade(perfil, BigDecimal.ZERO);

        assertThat(resultado.getClassificacao()).isEqualTo("SEM_RENDA");
        assertThat(resultado.getCapacidadeGastoMensal()).isZero();
    }

    private PerfilFinanceiro perfil(String saldo, String renda, String gastos, String reserva) {
        return new PerfilFinanceiro(
                "Teste",
                dinheiro(saldo),
                dinheiro(renda),
                dinheiro(gastos),
                dinheiro(reserva)
        );
    }

    private BigDecimal dinheiro(String valor) {
        return new BigDecimal(valor);
    }
}
