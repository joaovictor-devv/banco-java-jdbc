package com.joaovictor.service;

import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.repository.PerfilFinanceiroRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PerfilFinanceiroServiceTest {

    @Test
    void putDePerfilDeveCriarQuandoAindaNaoExiste() {
        PerfilFinanceiroRepository repository = mock(PerfilFinanceiroRepository.class);
        PerfilFinanceiro criado = perfil(1, "João", "1200", "0", "0", "0");
        when(repository.buscarUltimoPerfil()).thenReturn(null, criado);
        PerfilFinanceiroService service = new PerfilFinanceiroService(repository);

        PerfilFinanceiro resultado = service.atualizarPerfil("João", dinheiro("1200"));

        ArgumentCaptor<PerfilFinanceiro> captor = ArgumentCaptor.forClass(PerfilFinanceiro.class);
        verify(repository).salvar(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("João");
        assertThat(captor.getValue().getSaldoAtual()).isEqualByComparingTo("1200");
        assertThat(resultado).isSameAs(criado);
    }

    @Test
    void atualizarPerfilExistenteDevePreservarOrcamento() {
        PerfilFinanceiroRepository repository = mock(PerfilFinanceiroRepository.class);
        PerfilFinanceiro existente = perfil(7, "Antigo", "1000", "2500", "1300", "300");
        when(repository.buscarUltimoPerfil()).thenReturn(existente);
        PerfilFinanceiroService service = new PerfilFinanceiroService(repository);

        PerfilFinanceiro resultado = service.atualizarPerfil("Novo nome", dinheiro("1500"));

        verify(repository).atualizar(eq(7L), any(PerfilFinanceiro.class));
        assertThat(resultado.getNome()).isEqualTo("Novo nome");
        assertThat(resultado.getSaldoAtual()).isEqualByComparingTo("1500");
        assertThat(resultado.getRendaMensal()).isEqualByComparingTo("2500");
        assertThat(resultado.getGastosMensais()).isEqualByComparingTo("1300");
        assertThat(resultado.getValorPlanejadoGuardar()).isEqualByComparingTo("300");
    }

    @Test
    void atualizarSaldoDeveCriarPerfilPadraoQuandoAindaNaoExiste() {
        PerfilFinanceiroRepository repository = mock(PerfilFinanceiroRepository.class);
        PerfilFinanceiro criado = perfil(1, "Usuário", "900", "0", "0", "0");
        when(repository.buscarUltimoPerfil()).thenReturn(null, criado);
        PerfilFinanceiroService service = new PerfilFinanceiroService(repository);

        PerfilFinanceiro resultado = service.atualizarSaldoAtual(dinheiro("900"));

        verify(repository).salvar(any(PerfilFinanceiro.class));
        assertThat(resultado.getSaldoAtual()).isEqualByComparingTo("900");
    }

    @Test
    void deveRejeitarSaldoNegativo() {
        PerfilFinanceiroRepository repository = mock(PerfilFinanceiroRepository.class);
        PerfilFinanceiroService service = new PerfilFinanceiroService(repository);

        assertThatThrownBy(() -> service.atualizarSaldoAtual(dinheiro("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pode ser negativo");

        verifyNoInteractions(repository);
    }

    private PerfilFinanceiro perfil(long id,
                                    String nome,
                                    String saldo,
                                    String renda,
                                    String gastos,
                                    String reserva) {
        return new PerfilFinanceiro(
                id,
                nome,
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
