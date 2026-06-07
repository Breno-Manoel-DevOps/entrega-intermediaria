package com.bootcamp.cepfinder.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário cadastrado no sistema.
 * Inclui dados pessoais e endereço (preenchido via CEP).
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Column(nullable = false)
    private String senha;

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    // ── Getters e Setters ──────────────────────────────────────────────────────

    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }

    public String getNome()                         { return nome; }
    public void   setNome(String nome)              { this.nome = nome; }

    public String getEmail()                        { return email; }
    public void   setEmail(String email)            { this.email = email; }

    public String getSenha()                        { return senha; }
    public void   setSenha(String senha)            { this.senha = senha; }

    public String getCep()                          { return cep; }
    public void   setCep(String cep)                { this.cep = cep; }

    public String getLogradouro()                   { return logradouro; }
    public void   setLogradouro(String logradouro)  { this.logradouro = logradouro; }

    public String getComplemento()                  { return complemento; }
    public void   setComplemento(String complemento){ this.complemento = complemento; }

    public String getBairro()                       { return bairro; }
    public void   setBairro(String bairro)          { this.bairro = bairro; }

    public String getLocalidade()                   { return localidade; }
    public void   setLocalidade(String localidade)  { this.localidade = localidade; }

    public String getUf()                           { return uf; }
    public void   setUf(String uf)                  { this.uf = uf; }

    public LocalDateTime getCriadoEm()              { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
