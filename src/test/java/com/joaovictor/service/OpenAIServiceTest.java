package com.joaovictor.service;

import com.joaovictor.exception.IAIndisponivelException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class OpenAIServiceTest {

    @Test
    void deveInformarQuandoChaveNaoEstaConfigurada() {
        ContextoFinanceiroIAService contexto = mock(ContextoFinanceiroIAService.class);
        OpenAIService service = new OpenAIService(contexto, "", "modelo-teste");

        assertThat(service.isConfigurada()).isFalse();
        verifyNoInteractions(contexto);
    }

    @Test
    void deveInformarQuandoChaveEstaConfiguradaSemFazerChamadaExterna() {
        ContextoFinanceiroIAService contexto = mock(ContextoFinanceiroIAService.class);
        OpenAIService service = new OpenAIService(contexto, "chave-ficticia", "modelo-teste");

        assertThat(service.isConfigurada()).isTrue();
        verifyNoInteractions(contexto);
    }

    @Test
    void deveFalharDeFormaControladaQuandoChaveNaoEstaConfigurada() {
        ContextoFinanceiroIAService contexto = mock(ContextoFinanceiroIAService.class);
        OpenAIService service = new OpenAIService(contexto, "", "modelo-teste");

        assertThatThrownBy(() -> service.perguntar("Como está minha situação?"))
                .isInstanceOf(IAIndisponivelException.class)
                .hasMessageContaining("OPENAI_API_KEY");

        verifyNoInteractions(contexto);
    }

    @Test
    void deveValidarPerguntaAntesDeConsultarIA() {
        ContextoFinanceiroIAService contexto = mock(ContextoFinanceiroIAService.class);
        OpenAIService service = new OpenAIService(contexto, "chave-ficticia", "modelo-teste");

        assertThatThrownBy(() -> service.perguntar("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pergunta é obrigatória");

        verifyNoInteractions(contexto);
    }
}
