package com.bootcamp.cepfinder.controller;

import com.bootcamp.cepfinder.exception.CepNaoEncontradoException;
import com.bootcamp.cepfinder.model.Endereco;
import com.bootcamp.cepfinder.service.ViaCepService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador principal da aplicação CEP Finder.
 *
 * Expõe duas superfícies:
 *  - GET  /           → página HTML (Thymeleaf)
 *  - GET  /api/cep/{cep} → endpoint REST que retorna JSON
 */
@Controller
public class CepController {

    private final ViaCepService viaCepService;

    public CepController(ViaCepService viaCepService) {
        this.viaCepService = viaCepService;
    }

    // ── Página Web ─────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String cep, Model model) {
        try {
            Endereco endereco = viaCepService.buscarPorCep(cep);
            model.addAttribute("endereco", endereco);
        } catch (CepNaoEncontradoException e) {
            model.addAttribute("erro", "CEP não encontrado: " + cep);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("cep", cep);
        return "index";
    }

    // ── API REST (JSON) ────────────────────────────────────────────────────────

    @GetMapping("/api/cep/{cep}")
    @ResponseBody
    public ResponseEntity<?> buscarJson(@PathVariable String cep) {
        try {
            Endereco endereco = viaCepService.buscarPorCep(cep);
            return ResponseEntity.ok(endereco);
        } catch (CepNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
