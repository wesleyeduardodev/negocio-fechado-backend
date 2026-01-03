package com.negociofechado.modulos.interesse.service;

import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.interesse.dto.CriarInteresseRequest;
import com.negociofechado.modulos.interesse.dto.InteresseResponse;
import com.negociofechado.modulos.interesse.dto.ProfissionalStatsResponse;
import com.negociofechado.modulos.interesse.entity.Interesse;
import com.negociofechado.modulos.interesse.entity.StatusInteresse;
import com.negociofechado.modulos.interesse.repository.InteresseRepository;
import com.negociofechado.modulos.profissional.entity.PerfilProfissional;
import com.negociofechado.modulos.profissional.repository.PerfilProfissionalRepository;
import com.negociofechado.modulos.solicitacao.entity.Solicitacao;
import com.negociofechado.modulos.solicitacao.entity.StatusSolicitacao;
import com.negociofechado.modulos.solicitacao.repository.SolicitacaoRepository;
import com.negociofechado.modulos.categoria.entity.Categoria;
import com.negociofechado.modulos.usuario.entity.Usuario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteresseService {

    private final InteresseRepository interesseRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final PerfilProfissionalRepository perfilProfissionalRepository;

    @Transactional
    public InteresseResponse criar(Long usuarioId, CriarInteresseRequest request) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Voce nao possui um perfil profissional"));

        if (!perfil.getAtivo()) {
            throw new NegocioException("Seu perfil profissional esta inativo");
        }

        Solicitacao solicitacao = solicitacaoRepository.findById(request.solicitacaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao", request.solicitacaoId()));

        if (solicitacao.getStatus() != StatusSolicitacao.ABERTA) {
            throw new NegocioException("Esta solicitacao nao esta mais disponivel");
        }

        // Verifica se a solicitacao e da mesma cidade do profissional
        Usuario profissionalUsuario = perfil.getUsuario();
        if (!solicitacao.getEndereco().getCidadeIbgeId().equals(profissionalUsuario.getEndereco().getCidadeIbgeId())) {
            throw new NegocioException("Esta solicitacao nao esta disponivel para sua regiao");
        }

        // Verifica se a categoria esta nas categorias do profissional
        Set<Long> categoriasIds = perfil.getCategorias().stream()
                .map(Categoria::getId)
                .collect(Collectors.toSet());

        if (!categoriasIds.contains(solicitacao.getCategoria().getId())) {
            throw new NegocioException("Esta solicitacao nao esta disponivel para sua categoria");
        }

        // Verifica se nao e uma solicitacao propria
        if (solicitacao.getCliente().getId().equals(usuarioId)) {
            throw new NegocioException("Voce nao pode demonstrar interesse na sua propria solicitacao");
        }

        // Verifica se ja demonstrou interesse
        if (interesseRepository.existsBySolicitacaoIdAndProfissionalId(request.solicitacaoId(), perfil.getId())) {
            throw new NegocioException("Voce ja demonstrou interesse nesta solicitacao");
        }

        Interesse interesse = Interesse.builder()
                .solicitacao(solicitacao)
                .profissional(perfil)
                .mensagem(request.mensagem())
                .status(StatusInteresse.PENDENTE)
                .build();

        interesseRepository.save(interesse);

        return toResponse(interesse);
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarPorSolicitacao(Long clienteId, Long solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndClienteId(solicitacaoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao", solicitacaoId));

        return interesseRepository.findBySolicitacaoIdWithProfissional(solicitacaoId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void marcarComoVisualizado(Long clienteId, Long interesseId) {
        Interesse interesse = interesseRepository.findById(interesseId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interesse", interesseId));

        if (!interesse.getSolicitacao().getCliente().getId().equals(clienteId)) {
            throw new NegocioException("Voce nao tem permissao para atualizar este interesse");
        }

        if (interesse.getStatus() == StatusInteresse.PENDENTE) {
            interesse.setStatus(StatusInteresse.VISUALIZADO);
            interesseRepository.save(interesse);
        }
    }

    @Transactional
    public void marcarComoContratado(Long clienteId, Long interesseId) {
        Interesse interesse = interesseRepository.findById(interesseId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interesse", interesseId));

        if (!interesse.getSolicitacao().getCliente().getId().equals(clienteId)) {
            throw new NegocioException("Voce nao tem permissao para atualizar este interesse");
        }

        interesse.setStatus(StatusInteresse.CONTRATADO);
        interesseRepository.save(interesse);

        // Atualiza o status da solicitacao para EM_ANDAMENTO
        Solicitacao solicitacao = interesse.getSolicitacao();
        if (solicitacao.getStatus() == StatusSolicitacao.ABERTA) {
            solicitacao.setStatus(StatusSolicitacao.EM_ANDAMENTO);
            solicitacaoRepository.save(solicitacao);
        }
    }

    public int contarPorSolicitacao(Long solicitacaoId) {
        return interesseRepository.countBySolicitacaoId(solicitacaoId);
    }

    @Transactional(readOnly = true)
    public ProfissionalStatsResponse getStatsProfissional(Long usuarioId) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Voce nao possui um perfil profissional"));

        int interessesEnviados = interesseRepository.countByProfissionalId(perfil.getId());
        int contratados = interesseRepository.countContratadosByProfissionalId(perfil.getId());
        int emNegociacao = interesseRepository.countEmNegociacaoByProfissionalId(perfil.getId());

        return new ProfissionalStatsResponse(interessesEnviados, contratados, emNegociacao);
    }

    private InteresseResponse toResponse(Interesse interesse) {
        PerfilProfissional perfil = interesse.getProfissional();
        Usuario usuario = perfil.getUsuario();

        return new InteresseResponse(
                interesse.getId(),
                perfil.getId(),
                usuario.getNome(),
                usuario.getCelular(),
                perfil.getBio(),
                interesse.getMensagem(),
                interesse.getStatus().name(),
                interesse.getCriadoEm()
        );
    }
}
