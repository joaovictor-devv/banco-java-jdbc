package com.joaovictor.service;

import com.joaovictor.dto.OrcamentoRequest;
import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.OrcamentoResumo;
import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.repository.PerfilFinanceiroRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrcamentoServiceTest {

    @Test
    void deveCriarPerfilPadraoQuandoOrcamentoForPrimeiroDadoInformado() {
        PerfilFinanceiroRepository repository = mock(PerfilFinanceiroRepository.class);
        MotorFinanceiroService motor = mock(MotorFinanceiroService.class);
        when(repository.buscarUltimoPerfil()).thenReturn(null);
        when(motor.calcularCapacidade()).thenReturn(capacidade());
        OrcamentoService service = new OrcamentoService(repository, motor);

        OrcamentoResumo resultado = service.salvar(request("2500", "1300", "300"));

        ArgumentCaptor<PerfilFinanceiro> captor = ArgumentCaptor.forClass(PerfilFinanceiro.class);
        verify(repository).salvar(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Usuário");
        assertThat(captor.getValue().getSaldoAtual()).isZero();
        assertThat(captor.getValue().getRendaMensal()).isEqualByComparingTo("2500");
        assertThat(captor.getValue().getGastosMensais()).isEqualByComparingTo("1300");
        assertThat(captor.getValue().getValorPlanejadoGuardar()).isEqualByComparingTo("300");
        assertThat(resultado.getDisponivelAposMetas()).isEqualByComparingTo("900");
    }

    @Test
    void devePreservarNomeESaldoAoAlterarOrcamento() {
        PerfilFinanceiroRepository repository = mock(PerfilFinanceiroRepository.class);
        MotorFinanceiroService motor = mock(MotorFinanceiroService.class);
        PerfilFinanceiro existente = new PerfilFinanceiro(
                4,
                "João",
                dinheiro("1200"),
                dinheiro("2000"),
                dinheiro("1000"),
                dinheiro("100")
        );
        when(repository.buscarUltimoPerfil()).thenReturn(existente);
        when(motor.calcularCapacidade()).thenReturn(capacidade());
        OrcamentoService service = new OrcamentoService(repository, motor);

        service.salvar(request("2500", "1300", "300"));

        ArgumentCaptor<PerfilFinanceiro> captor = ArgumentCaptor.forClass(PerfilFinanceiro.class);
        verify(repository).atualizar(eq(4L), captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("João");
        assertThat(captor.getValue().getSaldoAtual()).isEqualByComparingTo("1200");
        assertThat(captor.getValue().getRendaMensal()).isEqualByComparingTo("2500");
    }

    @Test
    void deveRejeitarValoresNegativosAntesDeAcessarBanco() {
        PerfilFinanceiroRepository repository = mock(PerfilFinanceiroRepository.class);
        MotorFinanceiroService motor = mock(MotorFinanceiroService.class);
        OrcamentoService service = new OrcamentoService(repository, motor);

        assertThatThrownBy(() -> service.salvar(request("2500", "-1", "300")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("gastos mensais");

        verifyNoInteractions(repository, motor);
    }

    private OrcamentoRequest request(String renda, String gastos, String reserva) {
        OrcamentoRequest request = new OrcamentoRequest();
        request.setRendaMensal(dinheiro(renda));
        request.setGastosMensais(dinheiro(gastos));
        request.setValorPlanejadoGuardar(dinheiro(reserva));
        return request;
    }

    private CapacidadeFinanceira capacidade() {
        return new CapacidadeFinanceira(
                dinheiro("2500"),
                dinheiro("1300"),
                dinheiro("300"),
                BigDecimal.ZERO,
                dinheiro("1600"),
                dinheiro("900"),
                dinheiro("900"),
                dinheiro("900"),
                dinheiro("1200"),
                dinheiro("900"),
                dinheiro("64.00"),
                "SAUDAVEL",
                "teste"
        );
    }

    private BigDecimal dinheiro(String valor) {
        return new BigDecimal(valor);
    }
}
