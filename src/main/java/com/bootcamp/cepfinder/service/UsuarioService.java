package com.bootcamp.cepfinder.service;

import com.bootcamp.cepfinder.model.Usuario;
import com.bootcamp.cepfinder.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pelo cadastro e consulta de usuários.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Cadastra um novo usuário.
     *
     * @throws IllegalArgumentException se o e-mail já estiver em uso
     */
    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

    /** Retorna todos os usuários cadastrados. */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}
