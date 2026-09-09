package com.joaovictor.exception;

public class IAIndisponivelException extends RuntimeException {

    public IAIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public IAIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
