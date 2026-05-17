package com.bootcamp.cepfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa o objeto de endereço retornado pela API ViaCEP.
 * Os campos seguem exatamente o contrato da API externa.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Endereco {

    @JsonProperty("cep")
    private String cep;

    @JsonProperty("logradouro")
    private String logradouro;

    @JsonProperty("complemento")
    private String complemento;

    @JsonProperty("bairro")
    private String bairro;

    @JsonProperty("localidade")
    private String localidade;

    @JsonProperty("uf")
    private String uf;

    @JsonProperty("ibge")
    private String ibge;

    @JsonProperty("ddd")
    private String ddd;

    @JsonProperty("erro")
    private boolean erro;

    // ── Getters e Setters ──────────────────────────────────────────────────────

    public String getCep()                      { return cep; }
    public void   setCep(String cep)            { this.cep = cep; }

    public String getLogradouro()               { return logradouro; }
    public void   setLogradouro(String l)       { this.logradouro = l; }

    public String getComplemento()              { return complemento; }
    public void   setComplemento(String c)      { this.complemento = c; }

    public String getBairro()                   { return bairro; }
    public void   setBairro(String b)           { this.bairro = b; }

    public String getLocalidade()               { return localidade; }
    public void   setLocalidade(String l)       { this.localidade = l; }

    public String getUf()                       { return uf; }
    public void   setUf(String uf)              { this.uf = uf; }

    public String getIbge()                     { return ibge; }
    public void   setIbge(String ibge)          { this.ibge = ibge; }

    public String getDdd()                      { return ddd; }
    public void   setDdd(String ddd)            { this.ddd = ddd; }

    public boolean isErro()                     { return erro; }
    public void    setErro(boolean erro)        { this.erro = erro; }

    /** Endereço formatado para exibição amigável. */
    public String getEnderecoFormatado() {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null && !logradouro.isBlank()) sb.append(logradouro).append(", ");
        if (bairro     != null && !bairro.isBlank())     sb.append(bairro).append(" — ");
        if (localidade != null && !localidade.isBlank()) sb.append(localidade);
        if (uf         != null && !uf.isBlank())         sb.append("/").append(uf);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Endereco{cep='" + cep + "', logradouro='" + logradouro +
               "', bairro='" + bairro + "', localidade='" + localidade +
               "', uf='" + uf + "'}";
    }
}
