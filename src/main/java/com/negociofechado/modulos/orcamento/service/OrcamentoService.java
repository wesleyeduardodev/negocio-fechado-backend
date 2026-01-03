package com.negociofechado.modulos.orcamento.service;

import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.categoria.entity.Categoria;
import com.negociofechado.modulos.orcamento.dto.OrcamentoEnviadoResponse;
import com.negociofechado.modulos.orcamento.dto.OrcamentoRequest;
import com.negociofechado.modulos.orcamento.dto.OrcamentoResumoResponse;
import com.negociofechado.modulos.orcamento.entity.Orcamento;
import com.negociofechado.modulos.orcamento.entity.StatusOrcamento;
import com.negociofechado.modulos.orcamento.repository.OrcamentoRepository;
import com.negociofechado.modulos.profissional.entity.PerfilProfissional;
import com.negociofechado.modulos.profissional.repository.PerfilProfissionalRepository;
import com.negociofechado.modulos.solicitacao.entity.Solicitacao;
import com.negociofechado.modulos.solicitacao.entity.StatusSolicitacao;
import com.negociofechado.modulos.solicitacao.repository.SolicitacaoRepository;
import com.negociofechado.modulos.usuario.entity.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final PerfilProfissionalRepository perfilProfissionalRepository;

    @Transactional
    public OrcamentoResumoResponse enviar(Long usuarioId, Long solicitacaoId, OrcamentoRequest request) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Voce nao possui um perfil profissional"));

        if (!perfil.getAtivo()) {
            throw new NegocioException("Seu perfil profissional esta inativo");
        }

        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao", solicitacaoId));

        // Validacoes de negocio
        validarEnvioOrcamento(perfil, solicitacao, usuarioId);

        // Verifica se ja enviou orcamento para esta solicitacao
        if (orcamentoRepository.existsBySolicitacaoIdAndProfissionalId(solicitacaoId, perfil.getId())) {
            throw new NegocioException("Voce ja enviou um orcamento para esta solicitacao");
        }

        Orcamento orcamento = Orcamento.builder()
                .solicitacao(solicitacao)
                .profissional(perfil)
                .valor(request.valor())
                .prazoEstimado(request.prazoEstimado())
                .mensagem(request.mensagem())
                .status(StatusOrcamento.PENDENTE)
                .build();

        orcamentoRepository.save(orcamento);

        return OrcamentoResumoResponse.fromEntity(orcamento);
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResumoResponse> listarPorSolicitacao(Long clienteId, Long solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndClienteId(solicitacaoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao", solicitacaoId));

        return orcamentoRepository.findBySolicitacaoIdWithProfissional(solicitacaoId)
                .stream()
                .map(OrcamentoResumoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrcamentoEnviadoResponse> listarEnviados(Long usuarioId, Pageable pageable) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Voce nao possui um perfil profissional"));

        return orcamentoRepository.findByProfissionalIdWithSolicitacao(perfil.getId(), pageable)
                .map(OrcamentoEnviadoResponse::fromEntity);
    }

    @Transactional
    public void aceitar(Long clienteId, Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findByIdWithDetails(orcamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento", orcamentoId));

        Solicitacao solicitacao = orcamento.getSolicitacao();

        // Verifica se o cliente e o dono da solicitacao
        if (!solicitacao.getCliente().getId().equals(clienteId)) {
            throw new NegocioException("Voce nao tem permissao para aceitar este orcamento");
        }

        // Verifica se a solicitacao ainda esta aberta
        if (solicitacao.getStatus() != StatusSolicitacao.ABERTA) {
            throw new NegocioException("Esta solicitacao nao esta mais disponivel para aceitar orcamentos");
        }

        // Verifica se o orcamento esta pendente
        if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
            throw new NegocioException("Este orcamento nao pode ser aceito");
        }

        // Aceita o orcamento
        orcamento.setStatus(StatusOrcamento.ACEITO);
        orcamentoRepository.save(orcamento);

        // Recusa automaticamente os outros orcamentos
        orcamentoRepository.recusarOutrosOrcamentos(
                solicitacao.getId(),
                orcamentoId,
                StatusOrcamento.RECUSADO
        );

        // Atualiza status da solicitacao para EM_ANDAMENTO
        solicitacao.setStatus(StatusSolicitacao.EM_ANDAMENTO);
        solicitacao.setAtualizadoEm(LocalDateTime.now());
        solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public void recusar(Long clienteId, Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findByIdWithDetails(orcamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento", orcamentoId));

        Solicitacao solicitacao = orcamento.getSolicitacao();

        // Verifica se o cliente e o dono da solicitacao
        if (!solicitacao.getCliente().getId().equals(clienteId)) {
            throw new NegocioException("Voce nao tem permissao para recusar este orcamento");
        }

        // Verifica se o orcamento esta pendente
        if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
            throw new NegocioException("Este orcamento nao pode ser recusado");
        }

        orcamento.setStatus(StatusOrcamento.RECUSADO);
        orcamentoRepository.save(orcamento);
    }

    @Transactional(readOnly = true)
    public Integer contarPorSolicitacao(Long solicitacaoId) {
        return orcamentoRepository.countBySolicitacaoId(solicitacaoId);
    }

    private void validarEnvioOrcamento(PerfilProfissional perfil, Solicitacao solicitacao, Long usuarioId) {
        // Verifica se a solicitacao esta aberta
        if (solicitacao.getStatus() != StatusSolicitacao.ABERTA) {
            throw new NegocioException("Esta solicitacao nao esta mais disponivel");
        }

        // Verifica se nao e a propria solicitacao
        if (solicitacao.getCliente().getId().equals(usuarioId)) {
            throw new NegocioException("Voce nao pode enviar orcamento para sua propria solicitacao");
        }

        // Verifica se a solicitacao e da mesma cidade
        Usuario profissionalUsuario = perfil.getUsuario();
        if (!solicitacao.getEndereco().getCidadeIbgeId().equals(profissionalUsuario.getEndereco().getCidadeIbgeId())) {
            throw new NegocioException("Esta solicitacao nao esta disponivel na sua regiao");
        }

        // Verifica se a categoria esta nas categorias do profissional
        Set<Long> categoriasIds = perfil.getCategorias().stream()
                .map(Categoria::getId)
                .collect(Collectors.toSet());

        if (!categoriasIds.contains(solicitacao.getCategoria().getId())) {
            throw new NegocioException("Voce nao atua nesta categoria de servico");
        }
    }
}
