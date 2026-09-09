package com.joaovictor.service;

import com.joaovictor.model.Meta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CompromissoMetasServiceTest {

    private final CompromissoMetasService service = new CompromissoMetasService();

    @Test
    void deveCalcularAporteMensalDaMeta() {
        Meta meta = meta("Notebook", "5000", "1000", 10);

        assertThat(service.calcularValorMensal(meta)).isEqualByComparingTo("400.00");
    }

    @Test
    void deveArredondarParaCimaSemSubestimarAporte() {
        Meta meta = meta("Curso", "1000", "0", 3);

        assertThat(service.calcularValorMensal(meta)).isEqualByComparingTo("333.34");
    }

    @Test
    void deveRetornarZeroParaMetaConcluida() {
        Meta meta = meta("Celular", "2000", "2000", 6);

        assertThat(service.calcularValorMensal(meta)).isZero();
    }

    @Test
    void deveRetornarZeroParaPrazoInvalidoNaCamadaDeCalculo() {
        Meta meta = meta("Teste", "1000", "0", 0);

        assertThat(service.calcularValorMensal(meta)).isZero();
    }

    private Meta meta(String nome, String alvo, String atual, int prazo) {
        return new Meta(
                nome,
                new BigDecimal(alvo),
                prazo,
                new BigDecimal(atual),
                "media",
                null
        );
    }
}
