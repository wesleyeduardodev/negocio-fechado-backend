package com.negociofechado.modulos.solicitacao.service;

import com.negociofechado.comum.entity.Endereco;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.avaliacao.entity.Avaliacao;
import com.negociofechado.modulos.avaliacao.repository.AvaliacaoRepository;
import com.negociofechado.modulos.avaliacao.service.AvaliacaoFotoService;
import com.negociofechado.modulos.categoria.entity.Categoria;
import com.negociofechado.modulos.categoria.repository.CategoriaRepository;
import com.negociofechado.modulos.interesse.entity.Interesse;
import com.negociofechado.modulos.interesse.repository.InteresseRepository;
import com.negociofechado.modulos.notificacao.entity.TipoNotificacao;
import com.negociofechado.modulos.notificacao.service.NotificacaoService;
import com.negociofechado.modulos.profissional.entity.PerfilProfissional;
import com.negociofechado.modulos.profissional.repository.PerfilProfissionalRepository;
import com.negociofechado.modulos.solicitacao.dto.AtualizarSolicitacaoRequest;
import com.negociofechado.modulos.solicitacao.dto.CriarSolicitacaoRequest;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoDetalheResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoParaProfissionalResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacaoResumoResponse;
import com.negociofechado.modulos.solicitacao.dto.SolicitacoesStatsResponse;
import com.negociofechado.modulos.solicitacao.entity.Solicitacao;
import com.negociofechado.modulos.solicitacao.entity.SolicitacaoFoto;
import com.negociofechado.modulos.solicitacao.entity.StatusSolicitacao;
import com.negociofechado.modulos.solicitacao.repository.SolicitacaoFotoRepository;
import com.negociofechado.modulos.solicitacao.repository.SolicitacaoRepository;
import com.negociofechado.modulos.usuario.entity.Usuario;
import com.negociofechado.modulos.usuario.repository.UsuarioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final SolicitacaoFotoRepository solicitacaoFotoRepository;
    private final SolicitacaoFotoService solicitacaoFotoService;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PerfilProfissionalRepository perfilProfissionalRepository;
    private final InteresseRepository interesseRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AvaliacaoFotoService avaliacaoFotoService;
    private final NotificacaoService notificacaoService;

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
                .urgencia(request.urgencia())
                .status(StatusSolicitacao.ABERTA)
                .build();

        solicitacaoRepository.save(solicitacao);

        notificarProfissionais(solicitacao);

        return toDetalheResponse(solicitacao);
    }

    private void notificarProfissionais(Solicitacao solicitacao) {
        Integer cidadeIbgeId = solicitacao.getEndereco().getCidadeIbgeId();
        Long categoriaId = solicitacao.getCategoria().getId();
        Long clienteId = solicitacao.getCliente().getId();

        List<Long> profissionaisIds = perfilProfissionalRepository
            .findUsuarioIdsByCidadeAndCategoriaAndAtivoExcluindoUsuario(cidadeIbgeId, categoriaId, clienteId);

        if (!profissionaisIds.isEmpty()) {
            String titulo = "Nova solicitacao disponivel";
            String corpo = solicitacao.getTitulo() + " - " + solicitacao.getEndereco().getBairro();

            notificacaoService.enviarParaUsuarios(
                profissionaisIds,
                TipoNotificacao.NOVA_SOLICITACAO,
                titulo,
                corpo,
                solicitacao.getId()
            );
        }
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
    public SolicitacaoDetalheResponse atualizar(Long clienteId, Long solicitacaoId, AtualizarSolicitacaoRequest request) {
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndClienteId(solicitacaoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação", solicitacaoId));

        if (solicitacao.getStatus() != StatusSolicitacao.ABERTA) {
            throw new NegocioException("Apenas solicitações abertas podem ser editadas");
        }

        solicitacao.setTitulo(request.titulo());
        solicitacao.setDescricao(request.descricao());
        solicitacao.setUrgencia(request.urgencia());
        solicitacao.setAtualizadoEm(LocalDateTime.now());

        if (request.fotos() != null) {
            List<SolicitacaoFoto> fotosExistentes = solicitacaoFotoRepository.findBySolicitacaoIdOrderByOrdem(solicitacaoId);
            Set<String> fotosParaManter = new HashSet<>(request.fotos());

            for (SolicitacaoFoto foto : fotosExistentes) {
                if (!fotosParaManter.contains(foto.getUrl())) {
                    solicitacaoFotoService.deletarFoto(foto.getId());
                }
            }
        }

        solicitacaoRepository.save(solicitacao);
        return toDetalheResponse(solicitacao);
    }

    public void verificarProprietario(Long solicitacaoId, Long usuarioId) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação", solicitacaoId));

        if (!solicitacao.getCliente().getId().equals(usuarioId)) {
            throw new NegocioException("Você não tem permissão para acessar esta solicitação");
        }
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

    @Transactional
    public void concluir(Long clienteId, Long solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndClienteId(solicitacaoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação", solicitacaoId));

        if (solicitacao.getStatus() != StatusSolicitacao.EM_ANDAMENTO) {
            throw new NegocioException("Apenas solicitações em andamento podem ser concluídas");
        }

        solicitacao.setStatus(StatusSolicitacao.CONCLUIDA);
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

    @Transactional(readOnly = true)
    public Page<SolicitacaoParaProfissionalResponse> listarDisponiveisParaProfissional(Long usuarioId, Pageable pageable) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Você não possui um perfil profissional"));

        if (!perfil.getAtivo()) {
            throw new NegocioException("Seu perfil profissional está inativo");
        }

        Usuario usuario = perfil.getUsuario();
        Integer cidadeIbgeId = usuario.getEndereco().getCidadeIbgeId();

        Set<Long> categoriasIds = perfil.getCategorias().stream()
                .map(Categoria::getId)
                .collect(Collectors.toSet());

        if (categoriasIds.isEmpty()) {
            throw new NegocioException("Você não possui categorias cadastradas");
        }

        return solicitacaoRepository.findDisponiveisParaProfissional(
                cidadeIbgeId,
                categoriasIds,
                usuarioId,
                pageable
        ).map(this::toParaProfissionalResponse);
    }

    @Transactional(readOnly = true)
    public SolicitacaoParaProfissionalResponse buscarPorIdParaProfissional(Long usuarioId, Long solicitacaoId) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Você não possui um perfil profissional"));

        if (!perfil.getAtivo()) {
            throw new NegocioException("Seu perfil profissional está inativo");
        }

        Solicitacao solicitacao = solicitacaoRepository.findByIdAndStatusAberta(solicitacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação", solicitacaoId));

        Usuario profissionalUsuario = perfil.getUsuario();
        if (!solicitacao.getEndereco().getCidadeIbgeId().equals(profissionalUsuario.getEndereco().getCidadeIbgeId())) {
            throw new NegocioException("Esta solicitação não está disponível para você");
        }

        Set<Long> categoriasIds = perfil.getCategorias().stream()
                .map(Categoria::getId)
                .collect(Collectors.toSet());

        if (!categoriasIds.contains(solicitacao.getCategoria().getId())) {
            throw new NegocioException("Esta solicitação não está disponível para você");
        }

        if (solicitacao.getCliente().getId().equals(usuarioId)) {
            throw new NegocioException("Você não pode ver sua própria solicitação como profissional");
        }

        return toParaProfissionalResponse(solicitacao);
    }

    private SolicitacaoParaProfissionalResponse toParaProfissionalResponse(Solicitacao solicitacao) {
        Categoria categoria = solicitacao.getCategoria();
        Endereco endereco = solicitacao.getEndereco();
        Usuario cliente = solicitacao.getCliente();

        List<String> fotosUrls = solicitacaoFotoRepository.findBySolicitacaoIdOrderByOrdem(solicitacao.getId())
                .stream()
                .map(SolicitacaoFoto::getUrl)
                .toList();

        return new SolicitacaoParaProfissionalResponse(
                solicitacao.getId(),
                solicitacao.getTitulo(),
                solicitacao.getDescricao(),
                cliente.getNome(),
                cliente.getCelular(),
                categoria.getNome(),
                categoria.getIcone(),
                endereco.getBairro(),
                endereco.getCidadeNome(),
                endereco.getUf(),
                solicitacao.getUrgencia().name(),
                fotosUrls,
                solicitacao.getCriadoEm()
        );
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

        List<String> fotosUrls = solicitacaoFotoRepository.findBySolicitacaoIdOrderByOrdem(solicitacao.getId())
                .stream()
                .map(SolicitacaoFoto::getUrl)
                .toList();

        // Buscar profissional contratado (se houver)
        Long profissionalContratadoId = null;
        String profissionalContratadoNome = null;
        String profissionalContratadoFotoUrl = null;

        Optional<Interesse> interesseContratado = interesseRepository.findContratadoBySolicitacaoId(solicitacao.getId());
        if (interesseContratado.isPresent()) {
            PerfilProfissional profissional = interesseContratado.get().getProfissional();
            profissionalContratadoId = profissional.getId();
            profissionalContratadoNome = profissional.getUsuario().getNome();
            profissionalContratadoFotoUrl = profissional.getUsuario().getFotoUrl();
        }

        // Buscar avaliação (se houver)
        Long avaliacaoId = null;
        Integer avaliacaoNota = null;
        String avaliacaoComentario = null;
        List<String> avaliacaoFotos = Collections.emptyList();
        LocalDateTime avaliacaoData = null;

        Optional<Avaliacao> avaliacao = avaliacaoRepository.findBySolicitacaoId(solicitacao.getId());
        if (avaliacao.isPresent()) {
            Avaliacao av = avaliacao.get();
            avaliacaoId = av.getId();
            avaliacaoNota = av.getNota();
            avaliacaoComentario = av.getComentario();
            avaliacaoData = av.getCriadoEm();
            avaliacaoFotos = avaliacaoFotoService.listarUrlsFotos(av.getId());
        }

        return new SolicitacaoDetalheResponse(
                solicitacao.getId(),
                solicitacao.getTitulo(),
                solicitacao.getDescricao(),
                categoria.getId(),
                categoria.getNome(),
                categoria.getIcone(),
                solicitacao.getStatus().name(),
                solicitacao.getUrgencia().name(),
                endereco.getUf(),
                endereco.getCidadeIbgeId(),
                endereco.getCidadeNome(),
                endereco.getBairro(),
                fotosUrls,
                interesseRepository.countBySolicitacaoId(solicitacao.getId()),
                solicitacao.getCriadoEm(),
                solicitacao.getAtualizadoEm(),
                profissionalContratadoId,
                profissionalContratadoNome,
                profissionalContratadoFotoUrl,
                avaliacaoId,
                avaliacaoNota,
                avaliacaoComentario,
                avaliacaoFotos,
                avaliacaoData
        );
    }

}
