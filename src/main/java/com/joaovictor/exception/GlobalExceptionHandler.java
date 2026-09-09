package com.joaovictor.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarIllegalArgument(IllegalArgumentException e) {
        return resposta(HttpStatus.BAD_REQUEST, "DADOS_INVALIDOS", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarJsonInvalido(HttpMessageNotReadableException e) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "JSON_INVALIDO",
                "O corpo da requisição está ausente ou possui JSON inválido."
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> tratarParametroInvalido(MethodArgumentTypeMismatchException e) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "PARAMETRO_INVALIDO",
                "O parâmetro '" + e.getName() + "' possui um valor inválido."
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException e) {
        return resposta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METODO_NAO_PERMITIDO",
                "O método HTTP utilizado não é permitido para esta rota."
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> tratarRotaNaoEncontrada(NoResourceFoundException e) {
        return resposta(
                HttpStatus.NOT_FOUND,
                "ROTA_NAO_ENCONTRADA",
                "A rota solicitada não foi encontrada."
        );
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
