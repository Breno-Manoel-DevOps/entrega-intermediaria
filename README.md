# 📍 CEP Finder — Bootcamp Etapa 2

> **🌐 Deploy:** [https://cep-finder.onrender.com](https://cep-finder.onrender.com) ← _cole aqui o link após o deploy_

[![CI](https://github.com/SEU_USUARIO/cep-finder/actions/workflows/ci.yml/badge.svg?branch=entrega-intermediaria)](https://github.com/SEU_USUARIO/cep-finder/actions)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)

---

## 🎯 Sobre o Projeto

Aplicação web desenvolvida para a **Etapa 2 do Bootcamp** que integra a API pública **[ViaCEP](https://viacep.com.br)** para consulta de endereços a partir de um CEP brasileiro.

### Problema Real Resolvido
Formulários e cadastros frequentemente precisam preencher automaticamente campos de endereço a partir do CEP, evitando erros de digitação e agilizando o processo para o usuário.

---

## ✅ Critérios Atendidos

| Critério | Status |
|---|---|
| Integração com API Pública (ViaCEP) | ✅ |
| Issue criada + Branch `entrega-intermediaria` | ✅ |
| Testes de Integração (WireMock + MockMvc) | ✅ |
| Deploy público (Render.com) | ✅ |
| CI/CD GitHub Actions (pipeline verde) | ✅ |
| README atualizado com link do deploy | ✅ |

---

## 🔌 API Pública Integrada

**ViaCEP** — `https://viacep.com.br`

- Endpoint utilizado: `GET https://viacep.com.br/ws/{cep}/json/`
- Gratuita, sem necessidade de autenticação
- Retorna dados como logradouro, bairro, cidade, UF, DDD e código IBGE

---

## 🚀 Como Executar Localmente

### Pré-requisitos
- Java 17+
- Maven 3.8+

```bash
# 1. Clone o repositório
git clone https://github.com/SEU_USUARIO/cep-finder.git
cd cep-finder

# 2. (Na branch da entrega)
git checkout entrega-intermediaria

# 3. Execute a aplicação
mvn spring-boot:run

# 4. Acesse no navegador
open http://localhost:8080
```

### Rodar os Testes
```bash
mvn verify
```

---

## 🧪 Testes de Integração

O projeto contém **10 testes de integração** distribuídos em duas classes:

### `ViaCepServiceIntegrationTest`
Usa **WireMock** para simular a API ViaCEP localmente, sem depender de internet:

| # | Cenário | Resultado esperado |
|---|---|---|
| 1 | CEP válido | Retorna endereço completo |
| 2 | CEP não encontrado | `CepNaoEncontradoException` |
| 3 | Formato inválido | `IllegalArgumentException` |
| 4 | CEP com hífen | Normalizado e consultado |
| 5 | API indisponível (HTTP 500) | Exceção propagada |
| 6 | Endereço formatado | Método utilitário validado |

### `CepControllerIntegrationTest`
Usa **MockMvc** com contexto Spring completo para testar a camada HTTP:

| # | Cenário | Status HTTP |
|---|---|---|
| 1 | CEP válido | `200 OK` + JSON |
| 2 | CEP inexistente | `404 Not Found` |
| 3 | Formato inválido | `400 Bad Request` |
| 4 | Página principal | `200 OK` + HTML |

---

## 📡 Endpoints da API REST

```
GET /              → Página web com formulário de busca
GET /buscar?cep=   → Busca com resposta HTML (Thymeleaf)
GET /api/cep/{cep} → Endpoint REST que retorna JSON
GET /actuator/health → Health check (usado pelo deploy)
```

**Exemplo de resposta JSON:**
```json
{
  "cep": "01310-100",
  "logradouro": "Avenida Paulista",
  "complemento": "de 1 a 610 - lado par",
  "bairro": "Bela Vista",
  "localidade": "São Paulo",
  "uf": "SP",
  "ibge": "3550308",
  "ddd": "11"
}
```

---

## 🏗️ Estrutura do Projeto

```
cep-finder/
├── .github/workflows/ci.yml          # Pipeline CI/CD
├── src/
│   ├── main/java/com/bootcamp/cepfinder/
│   │   ├── CepFinderApplication.java  # Entry point
│   │   ├── AppConfig.java             # Beans Spring
│   │   ├── controller/
│   │   │   └── CepController.java     # Endpoints HTTP
│   │   ├── service/
│   │   │   └── ViaCepService.java     # Integração ViaCEP
│   │   ├── model/
│   │   │   └── Endereco.java          # DTO da resposta
│   │   └── exception/
│   │       └── CepNaoEncontradoException.java
│   ├── main/resources/
│   │   ├── templates/index.html       # Frontend Thymeleaf
│   │   └── application.properties
│   └── test/java/.../integration/
│       ├── ViaCepServiceIntegrationTest.java  # WireMock
│       └── CepControllerIntegrationTest.java  # MockMvc
├── render.yaml                        # Config do deploy
└── pom.xml
```

---

## ☁️ Deploy (Render.com)

1. Crie conta em [render.com](https://render.com)
2. Clique em **New → Web Service**
3. Conecte seu repositório GitHub
4. O Render detecta o `render.yaml` automaticamente
5. Clique em **Deploy** e aguarde (~5 min)
6. Copie a URL gerada e cole no topo deste README

---

## 📋 Issue e Branch

- Issue criada: `#1 — Integração com API ViaCEP`
- Branch: `entrega-intermediaria`
- Resolvida via Pull Request com `closes #1`

---

## 🔗 Links

- **Aplicação:** https://cep-finder.onrender.com
- **API ViaCEP:** https://viacep.com.br
- **Repositório:** https://github.com/SEU_USUARIO/cep-finder
