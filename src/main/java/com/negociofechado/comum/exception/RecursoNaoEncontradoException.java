package com.negociofechado.comum.exception;

public class RecursoNaoEncontradoException extends NegocioException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " não encontrado com id: " + id);
    }

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }

}
