package com.joaovictor;

import com.joaovictor.controller.AnaliseController;
import com.joaovictor.controller.FiniaIAController;
import com.joaovictor.controller.MetaController;
import com.joaovictor.controller.OrcamentoController;
import com.joaovictor.controller.PerfilFinanceiroController;
import com.joaovictor.controller.SimulacaoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FiniaApplicationContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextoSpringDeveCarregarComControllersPrincipais() {
        assertNotNull(context.getBean(AnaliseController.class));
        assertNotNull(context.getBean(FiniaIAController.class));
        assertNotNull(context.getBean(MetaController.class));
        assertNotNull(context.getBean(OrcamentoController.class));
        assertNotNull(context.getBean(PerfilFinanceiroController.class));
        assertNotNull(context.getBean(SimulacaoController.class));
    }
}
