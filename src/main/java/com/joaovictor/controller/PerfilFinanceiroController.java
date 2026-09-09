package com.joaovictor.controller;

import com.joaovictor.dto.ApiResponse;
import com.joaovictor.dto.PerfilFinanceiroRequest;
import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.service.PerfilFinanceiroService;
import com.joaovictor.service.SituacaoFinanceiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil-financeiro")
public class PerfilFinanceiroController {

    private final PerfilFinanceiroService service = new PerfilFinanceiroService();
    private final SituacaoFinanceiraService situacaoService = new SituacaoFinanceiraService();

    @PostMapping
    public ResponseEntity<ApiResponse> cadastrar(@RequestBody PerfilFinanceiroRequest request) {
        service.cadastrarPerfil(
                request.getRendaMensal(),
                request.getRendaExtra(),
                request.getGastoMoradia(),
                request.getGastoAgua(),
                request.getGastoEnergia(),
                request.getGastoInternet(),
                request.getGastoTransporte(),
                request.getGastoAlimentacao(),
                request.getOutrasDespesas(),
                request.getValorPlanejadoGuardar(),
                request.getObjetivoPrincipal()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Perfil financeiro cadastrado com sucesso."));
    }

    @GetMapping
    public ResponseEntity<PerfilFinanceiro> buscarUltimo() {
        return ResponseEntity.ok(service.buscarUltimoPerfil());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilFinanceiro> buscarPorId(@PathVariable long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/situacao")
    public ResponseEntity<SituacaoFinanceira> situacao() {
        return ResponseEntity.ok(situacaoService.analisar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> atualizar(@PathVariable long id, @RequestBody PerfilFinanceiroRequest request) {
        service.atualizarPerfil(
                id,
                request.getRendaMensal(),
                request.getRendaExtra(),
                request.getGastoMoradia(),
                request.getGastoAgua(),
                request.getGastoEnergia(),
                request.getGastoInternet(),
                request.getGastoTransporte(),
                request.getGastoAlimentacao(),
                request.getOutrasDespesas(),
                request.getValorPlanejadoGuardar(),
                request.getObjetivoPrincipal()
        );

        return ResponseEntity.ok(new ApiResponse(true, "Perfil financeiro atualizado com sucesso."));
    }
}