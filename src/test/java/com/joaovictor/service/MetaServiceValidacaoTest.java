package com.joaovictor.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MetaServiceValidacaoTest {

    private final MetaService service = new MetaService();

    @Test
    void deveAceitarPrioridadesValidas() {
        assertThatCode(() -> service.validarMeta("Notebook", dinheiro("5000"), 12, BigDecimal.ZERO, "alta"))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.validarMeta("Notebook", dinheiro("5000"), 12, BigDecimal.ZERO, "Média"))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.validarMeta("Notebook", dinheiro("5000"), 12, BigDecimal.ZERO, "baixa"))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarPrioridadeDesconhecida() {
        assertThatThrownBy(() -> service.validarMeta("Notebook", dinheiro("5000"), 12, BigDecimal.ZERO, "urgente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("baixa, media ou alta");
    }

    @Test
    void deveRejeitarPrazoMaiorQueLimiteDoProduto() {
        assertThatThrownBy(() -> service.validarMeta("Casa", dinheiro("500000"), 601, BigDecimal.ZERO, "alta"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no máximo 600 meses");
    }

    @Test
    void deveRejeitarValorInicialMaiorQueAlvo() {
        assertThatThrownBy(() -> service.validarMeta("Notebook", dinheiro("5000"), 12, dinheiro("5001"), "alta"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maior que o valor alvo");
    }

    private BigDecimal dinheiro(String valor) {
        return new BigDecimal(valor);
    }
}
