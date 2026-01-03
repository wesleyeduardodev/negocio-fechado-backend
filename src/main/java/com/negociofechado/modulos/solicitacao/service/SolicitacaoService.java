package com.negociofechado.modulos.solicitacao.service;

import com.negociofechado.comum.entity.Endereco;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.categoria.entity.Categoria;
import com.negociofechado.modulos.categoria.repository.CategoriaRepository;
import com.negociofechado.modulos.solicitacao.dto.CriarSolicitacaoRequest;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoDetalheResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoResumoResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacoesStatsResponse;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public SolicitacaoDetalheResponse criar(Long clienteId, CriarSolicitacaoRequest request) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", clienteId));

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", request.categoriaId()));

        if (!categoria.getAtivo()) {
            throw new NegocioException("Categoria não está disponível");
        }

        Endereco enderecoCliente = cliente.getEndereco();
        Endereco enderecoSolicitacao = Endereco.builder()
                .uf(enderecoCliente.getUf())
                .cidadeIbgeId(enderecoCliente.getCidadeIbgeId())
                .cidadeNome(enderecoCliente.getCidadeNome())
                .bairro(enderecoCliente.getBairro())
                .build();

        Solicitacao solicitacao = Solicitacao.builder()
                .cliente(cliente)
                .categoria(categoria)
                .titulo(request.titulo())
                .descricao(request.descricao())
                .endereco(enderecoSolicitacao)
                .fotos(request.fotos() != null ? new ArrayList<>(request.fotos()) : new ArrayList<>())
                .status(StatusSolicitacao.ABERTA)
                .build();

        solicitacaoRepository.save(solicitacao);
        return toDetalheResponse(solicitacao);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResumoResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        return solicitacaoRepository.findByClienteIdOrderByCriadoEmDesc(clienteId, pageable)
                .map(this::toResumoResponse);
    }

    @Transactional(readOnly = true)
    public SolicitacaoDetalheResponse buscarPorId(Long clienteId, Long solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndClienteId(solicitacaoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação", solicitacaoId));

        return toDetalheResponse(solicitacao);
    }

    @Transactional
    public void cancelar(Long clienteId, Long solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndClienteId(solicitacaoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação", solicitacaoId));

        if (solicitacao.getStatus() != StatusSolicitacao.ABERTA) {
            throw new NegocioException("Apenas solicitações abertas podem ser canceladas");
        }

        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        solicitacao.setAtualizadoEm(LocalDateTime.now());
        solicitacaoRepository.save(solicitacao);
    }

    @Transactional(readOnly = true)
    public SolicitacoesStatsResponse getStats(Long clienteId) {
        List<Object[]> results = solicitacaoRepository.countByClienteIdGroupByStatus(clienteId);

        long abertas = 0, emAndamento = 0, concluidas = 0, canceladas = 0;

        for (Object[] row : results) {
            StatusSolicitacao status = (StatusSolicitacao) row[0];
            long count = (Long) row[1];

            switch (status) {
                case ABERTA -> abertas = count;
                case EM_ANDAMENTO -> emAndamento = count;
                case CONCLUIDA -> concluidas = count;
                case CANCELADA -> canceladas = count;
            }
        }

        long total = abertas + emAndamento + concluidas + canceladas;
        return new SolicitacoesStatsResponse(total, abertas, emAndamento, concluidas);
    }

    private SolicitacaoResumoResponse toResumoResponse(Solicitacao solicitacao) {
        Categoria categoria = solicitacao.getCategoria();
        Endereco endereco = solicitacao.getEndereco();

        return new SolicitacaoResumoResponse(
                solicitacao.getId(),
                solicitacao.getTitulo(),
                categoria.getNome(),
                categoria.getIcone(),
                solicitacao.getStatus().name(),
                endereco.getCidadeNome(),
                endereco.getUf(),
                solicitacao.getCriadoEm()
        );
    }

    private SolicitacaoDetalheResponse toDetalheResponse(Solicitacao solicitacao) {
        Categoria categoria = solicitacao.getCategoria();
        Endereco endereco = solicitacao.getEndereco();

        return new SolicitacaoDetalheResponse(
                solicitacao.getId(),
                solicitacao.getTitulo(),
                solicitacao.getDescricao(),
                categoria.getId(),
                categoria.getNome(),
                categoria.getIcone(),
                solicitacao.getStatus().name(),
                endereco.getUf(),
                endereco.getCidadeIbgeId(),
                endereco.getCidadeNome(),
                endereco.getBairro(),
                solicitacao.getFotos(),
                0, // TODO: contar orçamentos quando implementar
                solicitacao.getCriadoEm(),
                solicitacao.getAtualizadoEm()
        );
    }

}
