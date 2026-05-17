package com.bootcamp.cepfinder.integration;

import com.bootcamp.cepfinder.exception.CepNaoEncontradoException;
import com.bootcamp.cepfinder.model.Endereco;
import com.bootcamp.cepfinder.service.ViaCepService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║       TESTE DE INTEGRAÇÃO — CepController (REST API)        ║
 * ║                                                              ║
 * ║  Valida o comportamento do endpoint HTTP /api/cep/{cep}     ║
 * ║  com toda a camada Spring ativa (contexto completo).        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@SpringBootTest
@AutoConfigureMockMvc
class CepControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ViaCepService viaCepService;

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 1 — GET /api/cep/{cep} com CEP válido → 200 OK + JSON
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/cep/{cep} deve retornar 200 com JSON quando o CEP existir")
    void deveRetornar200ComJson_ParaCepValido() throws Exception {
        // ARRANGE
        Endereco enderecoMock = criarEnderecoMock();
        when(viaCepService.buscarPorCep("01310100")).thenReturn(enderecoMock);

        // ACT & ASSERT
        mockMvc.perform(get("/api/cep/01310100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cep").value("01310-100"))
                .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 2 — GET /api/cep/{cep} com CEP inexistente → 404 Not Found
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/cep/{cep} deve retornar 404 quando o CEP não existir")
    void deveRetornar404_ParaCepInexistente() throws Exception {
        // ARRANGE
        when(viaCepService.buscarPorCep("00000000"))
                .thenThrow(new CepNaoEncontradoException("00000000"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/cep/00000000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 3 — GET /api/cep/{cep} com formato inválido → 400 Bad Request
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/cep/{cep} deve retornar 400 para formato de CEP inválido")
    void deveRetornar400_ParaFormatoInvalido() throws Exception {
        // ARRANGE
        when(viaCepService.buscarPorCep("123"))
                .thenThrow(new IllegalArgumentException("Formato de CEP inválido"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/cep/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 4 — GET / (página principal) → 200 OK + HTML
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET / deve retornar a página inicial com status 200")
    void deveRetornarPaginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private Endereco criarEnderecoMock() {
        Endereco e = new Endereco();
        e.setCep("01310-100");
        e.setLogradouro("Avenida Paulista");
        e.setComplemento("de 1 a 610 - lado par");
        e.setBairro("Bela Vista");
        e.setLocalidade("São Paulo");
        e.setUf("SP");
        e.setIbge("3550308");
        e.setDdd("11");
        e.setErro(false);
        return e;
    }
}
