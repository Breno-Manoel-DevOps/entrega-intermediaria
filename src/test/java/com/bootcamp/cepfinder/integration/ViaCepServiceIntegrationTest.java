package com.bootcamp.cepfinder.integration;

import com.bootcamp.cepfinder.exception.CepNaoEncontradoException;
import com.bootcamp.cepfinder.model.Endereco;
import com.bootcamp.cepfinder.service.ViaCepService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║          TESTE DE INTEGRAÇÃO — ViaCepService                 ║
 * ║                                                              ║
 * ║  Valida a comunicação da aplicação com o serviço externo     ║
 * ║  ViaCEP. A API real é substituída por um servidor WireMock   ║
 * ║  que simula as respostas HTTP, tornando o teste determinístico║
 * ║  e independente de rede.                                     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ViaCepServiceIntegrationTest {

    private static WireMockServer wireMockServer;
    private ViaCepService viaCepService;

    // ── CEP de exemplo (Av. Paulista, SP) ─────────────────────────────────────
    private static final String CEP_VALIDO       = "01310100";
    private static final String CEP_INVALIDO     = "00000000";
    private static final String CEP_MAL_FORMADO  = "123";

    // ── Payload JSON que simula a resposta real da ViaCEP ─────────────────────
    private static final String RESPOSTA_SUCESSO = """
            {
              "cep": "01310-100",
              "logradouro": "Avenida Paulista",
              "complemento": "de 1 a 610 - lado par",
              "bairro": "Bela Vista",
              "localidade": "São Paulo",
              "uf": "SP",
              "ibge": "3550308",
              "ddd": "11",
              "erro": false
            }
            """;

    private static final String RESPOSTA_ERRO = """
            { "erro": true }
            """;

    // ── Ciclo de vida ─────────────────────────────────────────────────────────

    @BeforeAll
    static void iniciarWireMock() {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void pararWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void configurarServico() {
        wireMockServer.resetAll();
        // Aponta o serviço para o servidor mock ao invés da API real
        String urlMock = "http://localhost:" + wireMockServer.port();
        viaCepService = new ViaCepService(new RestTemplate(), urlMock);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 1 — CEP válido: deve retornar endereço completo
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("Deve retornar endereço completo para um CEP válido")
    void deveRetornarEnderecoParaCepValido() {
        // ARRANGE — configura o mock para responder com sucesso
        wireMockServer.stubFor(
                get(urlEqualTo("/ws/" + CEP_VALIDO + "/json/"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(RESPOSTA_SUCESSO)));

        // ACT — chama o serviço como a aplicação faria
        Endereco endereco = viaCepService.buscarPorCep(CEP_VALIDO);

        // ASSERT — valida os dados retornados
        assertThat(endereco).isNotNull();
        assertThat(endereco.getCep()).isEqualTo("01310-100");
        assertThat(endereco.getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(endereco.getBairro()).isEqualTo("Bela Vista");
        assertThat(endereco.getLocalidade()).isEqualTo("São Paulo");
        assertThat(endereco.getUf()).isEqualTo("SP");
        assertThat(endereco.getDdd()).isEqualTo("11");
        assertThat(endereco.isErro()).isFalse();

        // Verifica que a requisição foi feita exatamente uma vez
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/ws/" + CEP_VALIDO + "/json/")));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 2 — CEP não encontrado: API retorna { "erro": true }
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("Deve lançar CepNaoEncontradoException quando a API retornar erro")
    void deveLancarExcecaoParaCepNaoEncontrado() {
        // ARRANGE
        wireMockServer.stubFor(
                get(urlEqualTo("/ws/" + CEP_INVALIDO + "/json/"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(RESPOSTA_ERRO)));

        // ACT & ASSERT
        assertThatThrownBy(() -> viaCepService.buscarPorCep(CEP_INVALIDO))
                .isInstanceOf(CepNaoEncontradoException.class)
                .hasMessageContaining(CEP_INVALIDO);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 3 — CEP com formato inválido: deve rejeitar antes de chamar API
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("Deve lançar IllegalArgumentException para CEP com formato inválido")
    void deveLancarExcecaoParaFormatoInvalido() {
        // Nenhum stub necessário — a validação acontece localmente
        assertThatThrownBy(() -> viaCepService.buscarPorCep(CEP_MAL_FORMADO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");

        // Garante que a API externa NÃO foi chamada
        wireMockServer.verify(0, getRequestedFor(anyUrl()));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 4 — CEP com hífen: deve normalizar e consultar corretamente
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("Deve aceitar CEP com hífen e normalizar para 8 dígitos")
    void deveAceitarCepComHifen() {
        // ARRANGE
        wireMockServer.stubFor(
                get(urlEqualTo("/ws/" + CEP_VALIDO + "/json/"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(RESPOSTA_SUCESSO)));

        // ACT — passa com hífen
        Endereco endereco = viaCepService.buscarPorCep("01310-100");

        // ASSERT
        assertThat(endereco).isNotNull();
        assertThat(endereco.getLocalidade()).isEqualTo("São Paulo");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 5 — API indisponível: deve propagar exceção
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("Deve propagar exceção quando a API estiver indisponível (HTTP 500)")
    void devePropagar_QuandoApiRetornarErroServidor() {
        // ARRANGE — simula falha no servidor externo
        wireMockServer.stubFor(
                get(urlEqualTo("/ws/" + CEP_VALIDO + "/json/"))
                        .willReturn(aResponse().withStatus(500)));

        // ACT & ASSERT — a aplicação não deve engolir erros silenciosamente
        assertThatThrownBy(() -> viaCepService.buscarPorCep(CEP_VALIDO))
                .isInstanceOf(Exception.class);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENÁRIO 6 — Endereço formatado: valida método utilitário do model
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("Deve retornar endereço formatado corretamente pelo método utilitário")
    void deveRetornarEnderecoFormatado() {
        // ARRANGE
        wireMockServer.stubFor(
                get(urlEqualTo("/ws/" + CEP_VALIDO + "/json/"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(RESPOSTA_SUCESSO)));

        // ACT
        Endereco endereco = viaCepService.buscarPorCep(CEP_VALIDO);

        // ASSERT — verifica o método de conveniência do model
        String formatado = endereco.getEnderecoFormatado();
        assertThat(formatado).contains("Avenida Paulista");
        assertThat(formatado).contains("São Paulo");
        assertThat(formatado).contains("SP");
    }
}
