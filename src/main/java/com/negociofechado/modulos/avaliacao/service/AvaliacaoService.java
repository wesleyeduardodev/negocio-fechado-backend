package com.negociofechado.modulos.avaliacao.service;

import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.avaliacao.dto.AvaliacaoRequest;
import com.negociofechado.modulos.avaliacao.dto.AvaliacaoResponse;
import com.negociofechado.modulos.avaliacao.entity.Avaliacao;
import com.negociofechado.modulos.avaliacao.repository.AvaliacaoRepository;
import com.negociofechado.modulos.orcamento.entity.Orcamento;
import com.negociofechado.modulos.orcamento.repository.OrcamentoRepository;
import com.negociofechado.modulos.profissional.entity.PerfilProfissional;
import com.negociofechado.modulos.solicitacao.entity.Solicitacao;
import com.negociofechado.modulos.solicitacao.entity.StatusSolicitacao;
import com.negociofechado.modulos.solicitacao.repository.SolicitacaoRepository;
import com.negociofechado.modulos.usuario.entity.Usuario;
import com.negociofechado.modulos.usuario.repository.UsuarioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public AvaliacaoResponse criar(Long clienteId, Long solicitacaoId, AvaliacaoRequest request) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", clienteId));

        Solicitacao solicitacao = solicitacaoRepository.findByIdAndClienteId(solicitacaoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao", solicitacaoId));

        if (solicitacao.getStatus() != StatusSolicitacao.CONCLUIDA) {
            throw new NegocioException("Apenas solicitacoes concluidas podem ser avaliadas");
        }

        if (avaliacaoRepository.existsBySolicitacaoId(solicitacaoId)) {
            throw new NegocioException("Esta solicitacao ja foi avaliada");
        }

        Orcamento orcamentoAceito = orcamentoRepository.findAceitoBySolicitacaoId(solicitacaoId)
                .orElseThrow(() -> new NegocioException("Nenhum orcamento aceito encontrado para esta solicitacao"));

        PerfilProfissional profissional = orcamentoAceito.getProfissional();

        Avaliacao avaliacao = Avaliacao.builder()
                .solicitacao(solicitacao)
                .cliente(cliente)
                .profissional(profissional)
                .nota(request.nota())
                .comentario(request.comentario())
                .build();

        avaliacaoRepository.save(avaliacao);

        return AvaliacaoResponse.fromEntity(avaliacao);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponse> listarPorProfissional(Long profissionalId, Pageable pageable) {
        return avaliacaoRepository.findByProfissionalIdOrderByCriadoEmDesc(profissionalId, pageable)
                .map(AvaliacaoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Double calcularMediaPorProfissional(Long profissionalId) {
        Double media = avaliacaoRepository.calcularMediaPorProfissional(profissionalId);
        return media != null ? media : 0.0;
    }

    @Transactional(readOnly = true)
    public Long contarPorProfissional(Long profissionalId) {
        return avaliacaoRepository.contarPorProfissional(profissionalId);
    }

}
