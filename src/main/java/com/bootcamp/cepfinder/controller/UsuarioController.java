package com.bootcamp.cepfinder.controller;

import com.bootcamp.cepfinder.model.Usuario;
import com.bootcamp.cepfinder.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controlador de cadastro de usuários.
 *
 * Superfícies:
 *  - GET  /cadastro         → formulário HTML (Thymeleaf)
 *  - POST /cadastro         → processa formulário e salva no H2
 *  - GET  /api/usuarios     → endpoint REST que retorna JSON
 */
@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ── Página Web ─────────────────────────────────────────────────────────────

    @GetMapping("/cadastro")
    public String exibirFormulario(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processar(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "cadastro";
        }

        try {
            usuarioService.cadastrar(usuario);
            redirectAttributes.addFlashAttribute("sucesso",
                    "Cadastro realizado com sucesso! Bem-vindo(a), " + usuario.getNome() + "!");
            return "redirect:/cadastro";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
    }

    // ── API REST (JSON) ────────────────────────────────────────────────────────

    @GetMapping("/api/usuarios")
    @ResponseBody
    public ResponseEntity<?> listarJson() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(Map.of(
                "total", usuarios.size(),
                "usuarios", usuarios
        ));
    }
}
