package com.joaovictor.service;

import com.joaovictor.model.AnaliseGasto;
import com.joaovictor.model.CapacidadeFinanceira;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnaliseGastoServiceTest {

    private final AnaliseGastoService service = new AnaliseGastoService();

    @Test
    void deveRecomendarGastoDentroDaCapacidade() {
        AnaliseGasto resultado = service.analisar(dinheiro("200"), capacidade("1200", "550", "550"));

        assertThat(resultado.isSaldoSuficiente()).isTrue();
        assertThat(resultado.isRecomendado()).isTrue();
        assertThat(resultado.getClassificacao()).isEqualTo("RECOMENDADO");
        assertThat(resultado.getMargemLivreAposGasto()).isEqualByComparingTo("350");
    }

    @Test
    void deveSinalizarAtencaoQuandoGastoConsomeMaisDeOitentaPorCento() {
        AnaliseGasto resultado = service.analisar(dinheiro("500"), capacidade("1200", "550", "550"));

        assertThat(resultado.isRecomendado()).isTrue();
        assertThat(resultado.getClassificacao()).isEqualTo("ATENCAO");
    }

    @Test
    void deveRejeitarGastoQueCabeNoSaldoMasUltrapassaCapacidade() {
        AnaliseGasto resultado = service.analisar(dinheiro("600"), capacidade("1200", "550", "550"));

        assertThat(resultado.isSaldoSuficiente()).isTrue();
        assertThat(resultado.isRecomendado()).isFalse();
        assertThat(resultado.getClassificacao()).isEqualTo("NAO_RECOMENDADO");
    }

    @Test
    void deveRejeitarGastoMaiorQueSaldo() {
        AnaliseGasto resultado = service.analisar(dinheiro("1300"), capacidade("1200", "550", "550"));

        assertThat(resultado.isSaldoSuficiente()).isFalse();
        assertThat(resultado.isRecomendado()).isFalse();
        assertThat(resultado.getClassificacao()).isEqualTo("SALDO_INSUFICIENTE");
    }

    @Test
    void deveRejeitarNovoGastoQuandoNaoHaMargemAposMetas() {
        AnaliseGasto resultado = service.analisar(dinheiro("50"), capacidade("1000", "0", "0"));

        assertThat(resultado.isSaldoSuficiente()).isTrue();
        assertThat(resultado.isRecomendado()).isFalse();
        assertThat(resultado.getClassificacao()).isEqualTo("NAO_RECOMENDADO");
    }

    @Test
    void deveValidarValorDoGasto() {
        assertThatThrownBy(() -> service.analisar(BigDecimal.ZERO, capacidade("1000", "500", "500")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maior que zero");
    }

    private CapacidadeFinanceira capacidade(String saldo, String margemAposMetas, String limiteImediato) {
        return new CapacidadeFinanceira(
                dinheiro("2800"),
                dinheiro("1400"),
                dinheiro("400"),
                dinheiro("450"),
                dinheiro("2250"),
                dinheiro("1000"),
                dinheiro(margemAposMetas),
                dinheiro(margemAposMetas),
                dinheiro(saldo),
                dinheiro(limiteImediato),
                dinheiro("80.36"),
                "APERTADA",
                "teste"
        );
    }

    private BigDecimal dinheiro(String valor) {
        return new BigDecimal(valor);
    }
}
