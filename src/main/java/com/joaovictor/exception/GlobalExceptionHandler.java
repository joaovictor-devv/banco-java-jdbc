package com.joaovictor.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> tratarSaldoInsuficiente(SaldoInsuficienteException e) {
        return resposta(HttpStatus.BAD_REQUEST, "SALDO_INSUFICIENTE", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarIllegalArgument(IllegalArgumentException e) {
        return resposta(HttpStatus.BAD_REQUEST, "DADOS_INVALIDOS", e.getMessage());
    }

    @ExceptionHandler(IAIndisponivelException.class)
    public ResponseEntity<Map<String, Object>> tratarIAIndisponivel(IAIndisponivelException e) {
        logger.warn("Falha na integração com IA: {}", e.getMessage(), e);
        return resposta(HttpStatus.SERVICE_UNAVAILABLE, "IA_INDISPONIVEL", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> tratarRuntime(RuntimeException e) {
        logger.error("Erro interno não tratado", e);
        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERRO_INTERNO",
                "Ocorreu um erro interno no servidor. Consulte os logs da aplicação."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarGenerico(Exception e) {
        logger.error("Erro inesperado não tratado", e);
        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERRO_NAO_ESPERADO",
                "Ocorreu um erro inesperado no servidor. Consulte os logs da aplicação."
        );
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status,
                                                          String codigo,
                                                          String mensagem) {
        Map<String, Object> erro = new LinkedHashMap<>();
        erro.put("sucesso", false);
        erro.put("erro", codigo);
        erro.put("mensagem", mensagem);
        erro.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(erro);
    }
}
