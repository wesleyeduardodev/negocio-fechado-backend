package com.negociofechado.modulos.usuario.service;

import com.negociofechado.comum.entity.Endereco;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.usuario.dto.AlterarSenhaRequest;
import com.negociofechado.modulos.usuario.dto.AtualizarUsuarioRequest;
import com.negociofechado.modulos.usuario.dto.UsuarioResponse;
import com.negociofechado.modulos.usuario.entity.Usuario;
import com.negociofechado.modulos.usuario.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAvatarService avatarService;

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
        return toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, AtualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));

        usuario.setNome(request.nome());

        Endereco endereco = Endereco.builder()
                .uf(request.uf())
                .cidadeIbgeId(request.cidadeIbgeId())
                .cidadeNome(request.cidadeNome())
                .bairro(request.bairro())
                .build();
        usuario.setEndereco(endereco);

        usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    @Transactional
    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new NegocioException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarFoto(Long id, MultipartFile foto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));

        String fotoUrl = avatarService.uploadAvatar(id, foto);

        usuario.setFotoUrl(fotoUrl);
        usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    @Transactional
    public void removerFoto(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));

        if (usuario.getFotoUrl() != null) {
            avatarService.deletarAvatar(id);
        }

        usuario.setFotoUrl(null);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarModoPreferido(Long id, String modoPreferido) {
        if (!modoPreferido.equals("cliente") && !modoPreferido.equals("profissional")) {
            throw new NegocioException("Modo invalido. Use 'cliente' ou 'profissional'");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", id));

        usuario.setModoPreferido(modoPreferido);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        Endereco endereco = usuario.getEndereco();
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCelular(),
                usuario.getFotoUrl(),
                endereco.getUf(),
                endereco.getCidadeIbgeId(),
                endereco.getCidadeNome(),
                endereco.getBairro(),
                usuario.getCriadoEm()
        );
    }

}
