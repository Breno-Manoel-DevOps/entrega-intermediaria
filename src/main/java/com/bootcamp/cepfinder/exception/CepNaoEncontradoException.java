package com.bootcamp.cepfinder.exception;

/**
 * Lançada quando o CEP informado não é encontrado pela API ViaCEP
 * ou quando o formato é inválido.
 */
public class CepNaoEncontradoException extends RuntimeException {

    public CepNaoEncontradoException(String cep) {
        super("CEP não encontrado: " + cep);
    }
}
