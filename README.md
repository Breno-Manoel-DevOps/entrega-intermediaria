# 📍 CEP Finder 

> **🌐 Deploy:** [https://cep-finder.onrender.com](https://entrega-intermediaria.onrender.com)

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

## ☁️ Deploy no Render com banco Supabase (passo-a-passo)

1. Crie um projeto no Supabase e anote as credenciais (host, porta, database, usuário, senha). Use a connection string JDBC com `?sslmode=require`.

2. No Render, crie um novo `Web Service` conectando seu repositório GitHub e escolha o branch `entrega-intermediaria`.

3. Deixe `Runtime` como `Docker` (o `render.yaml` + `Dockerfile` já estão configurados).

4. Em `Environment` → `Environment Variables`, adicione as variáveis (marque como secret onde aplicável):

  - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<HOST>:<PORT>/postgres?sslmode=require`
  - `SPRING_DATASOURCE_USERNAME` = `<SEU_USUARIO>`
  - `SPRING_DATASOURCE_PASSWORD` = `<SUA_SENHA>`

  Observação: o arquivo `render.yaml` contém placeholders para estas chaves para facilitar deploy automático; não comite credenciais.

5. Ative `Auto Deploy` (opcional) para que cada push ao branch gere novo deploy.

6. Após o deploy, verifique o endpoint de saúde: `https://<sua-app>.onrender.com/actuator/health`.

Se quiser, eu posso criar um `README.deploy.md` com capturas e comandos prontos para colar no painel do Render.

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

## 📋 Issue e Branch

- Issue criada: `#1 — Integração com API ViaCEP`
- Branch: `entrega-intermediaria`
- Resolvida via Pull Request com `closes #1`

---

## 🔗 Links

- **Aplicação:** https://entrega-intermediaria.onrender.com
- **API ViaCEP:** https://viacep.com.br
- **Repositório:** https://github.com/Breno-Manoel-DevOps/entrega-intermediaria#
