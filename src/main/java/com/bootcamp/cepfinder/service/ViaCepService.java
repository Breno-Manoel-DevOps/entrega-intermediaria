package com.bootcamp.cepfinder.service;

import com.bootcamp.cepfinder.exception.CepNaoEncontradoException;
import com.bootcamp.cepfinder.model.Endereco;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Serviço responsável por consumir a API pública ViaCEP
 * (https://viacep.com.br) e retornar os dados de endereço.
 *
 * A URL base é configurável via propriedade, o que permite
 * que os testes de integração apontem para um servidor mock (WireMock).
 */
@Service
public class ViaCepService {

    private final RestTemplate restTemplate;
    private final String viaCepBaseUrl;

    public ViaCepService(
            RestTemplate restTemplate,
            @Value("${viacep.base-url:https://viacep.com.br}") String viaCepBaseUrl) {
        this.restTemplate  = restTemplate;
        this.viaCepBaseUrl = viaCepBaseUrl;
    }

    /**
     * Consulta o endereço correspondente ao CEP informado.
     *
     * @param cep CEP no formato "00000000" ou "00000-000"
     * @return {@link Endereco} preenchido com os dados da API
     * @throws CepNaoEncontradoException se o CEP não existir ou a API retornar erro
     * @throws IllegalArgumentException  se o formato do CEP for inválido
     */
    public Endereco buscarPorCep(String cep) {
        String cepLimpo = sanitizarCep(cep);
        validarFormato(cepLimpo);

        String url = viaCepBaseUrl + "/ws/" + cepLimpo + "/json/";

        try {
            Endereco endereco = restTemplate.getForObject(url, Endereco.class);

            if (endereco == null || endereco.isErro()) {
                throw new CepNaoEncontradoException(cep);
            }

            return endereco;

        } catch (HttpClientErrorException.NotFound e) {
            throw new CepNaoEncontradoException(cep);
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    /** Remove hífen e espaços do CEP. */
    private String sanitizarCep(String cep) {
        if (cep == null) return "";
        return cep.replaceAll("[^0-9]", "");
    }

    /** Garante que o CEP tenha exatamente 8 dígitos numéricos. */
    private void validarFormato(String cepLimpo) {
        if (!cepLimpo.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "Formato de CEP inválido. Use 8 dígitos numéricos. Recebido: " + cepLimpo);
        }
    }
}
