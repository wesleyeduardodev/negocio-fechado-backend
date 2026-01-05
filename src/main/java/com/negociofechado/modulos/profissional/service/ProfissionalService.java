package com.negociofechado.modulos.profissional.service;
import com.negociofechado.comum.entity.Endereco;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.avaliacao.repository.AvaliacaoRepository;
import com.negociofechado.modulos.categoria.entity.Categoria;
import com.negociofechado.modulos.categoria.repository.CategoriaRepository;
import com.negociofechado.modulos.profissional.dto.AtualizarPerfilProfissionalRequest;
import com.negociofechado.modulos.profissional.dto.CategoriaResumoResponse;
import com.negociofechado.modulos.profissional.dto.CriarPerfilProfissionalRequest;
import com.negociofechado.modulos.profissional.dto.PerfilProfissionalResponse;
import com.negociofechado.modulos.profissional.entity.PerfilProfissional;
import com.negociofechado.modulos.profissional.repository.PerfilProfissionalRepository;
import com.negociofechado.modulos.usuario.entity.Usuario;
import com.negociofechado.modulos.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfissionalService {

    private final PerfilProfissionalRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    @Transactional
    public PerfilProfissionalResponse criar(Long usuarioId, CriarPerfilProfissionalRequest request) {
        if (perfilRepository.existsByUsuarioId(usuarioId)) {
            throw new NegocioException("Usuário já possui perfil profissional");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", usuarioId));

        Set<Categoria> categorias = buscarCategorias(request.categoriasIds());

        PerfilProfissional perfil = PerfilProfissional.builder()
                .usuario(usuario)
                .bio(request.bio())
                .categorias(categorias)
                .build();

        perfilRepository.save(perfil);
        return toResponse(perfil);
    }

    @Transactional(readOnly = true)
    public PerfilProfissionalResponse buscarMeuPerfil(Long usuarioId) {
        PerfilProfissional perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Você ainda não possui perfil profissional"));

        return toResponse(perfil);
    }

    @Transactional
    public PerfilProfissionalResponse atualizar(Long usuarioId, AtualizarPerfilProfissionalRequest request) {
        PerfilProfissional perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Você ainda não possui perfil profissional"));

        Set<Categoria> categorias = buscarCategorias(request.categoriasIds());

        perfil.setBio(request.bio());
        perfil.setCategorias(categorias);
        if (request.ativo() != null) {
            perfil.setAtivo(request.ativo());
        }
        perfil.setAtualizadoEm(LocalDateTime.now());

        perfilRepository.save(perfil);
        return toResponse(perfil);
    }

    @Transactional(readOnly = true)
    public PerfilProfissionalResponse buscarPorId(Long perfilId) {
        PerfilProfissional perfil = perfilRepository.findByIdAndAtivoTrue(perfilId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil profissional", perfilId));

        return toResponse(perfil);
    }

    @Transactional(readOnly = true)
    public boolean isProfissional(Long usuarioId) {
        return perfilRepository.existsByUsuarioId(usuarioId);
    }

    private Set<Categoria> buscarCategorias(Set<Long> categoriasIds) {
        List<Categoria> categorias = categoriaRepository.findAllById(categoriasIds);

        if (categorias.size() != categoriasIds.size()) {
            throw new NegocioException("Uma ou mais categorias não foram encontradas");
        }

        for (Categoria categoria : categorias) {
            if (!categoria.getAtivo()) {
                throw new NegocioException("Categoria '" + categoria.getNome() + "' não está disponível");
            }
        }

        return new HashSet<>(categorias);
    }

    private PerfilProfissionalResponse toResponse(PerfilProfissional perfil) {
        Usuario usuario = perfil.getUsuario();
        Endereco endereco = usuario.getEndereco();

        List<CategoriaResumoResponse> categoriasResponse = perfil.getCategorias().stream()
                .map(c -> new CategoriaResumoResponse(c.getId(), c.getNome(), c.getIcone()))
                .toList();

        Double media = avaliacaoRepository.calcularMediaPorProfissional(perfil.getId());
        Long total = avaliacaoRepository.contarPorProfissional(perfil.getId());

        return new PerfilProfissionalResponse(
                perfil.getId(),
                usuario.getId(),
                usuario.getNome(),
                usuario.getFotoUrl(),
                perfil.getBio(),
                endereco.getUf(),
                endereco.getCidadeNome(),
                endereco.getBairro(),
                categoriasResponse,
                media != null ? media : 0.0,
                total != null ? total.intValue() : 0,
                perfil.getAtivo(),
                perfil.getCriadoEm()
        );
    }
}
