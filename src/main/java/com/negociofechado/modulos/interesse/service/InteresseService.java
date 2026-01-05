package com.negociofechado.modulos.interesse.service;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.comum.exception.RecursoNaoEncontradoException;
import com.negociofechado.modulos.interesse.dto.CriarInteresseRequest;
import com.negociofechado.modulos.interesse.dto.InteresseResponse;
import com.negociofechado.modulos.interesse.dto.MeuTrabalhoResponse;
import com.negociofechado.modulos.interesse.dto.ProfissionalStatsResponse;
import com.negociofechado.comum.entity.Endereco;
import com.negociofechado.modulos.avaliacao.entity.Avaliacao;
import com.negociofechado.modulos.avaliacao.repository.AvaliacaoRepository;
import com.negociofechado.modulos.avaliacao.service.AvaliacaoFotoService;
import com.negociofechado.modulos.avaliacao.service.AvaliacaoService;
import com.negociofechado.modulos.notificacao.entity.TipoNotificacao;
import com.negociofechado.modulos.notificacao.service.NotificacaoService;
import com.negociofechado.modulos.profissional.service.PerfilFotoService;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteresseService {

    private final InteresseRepository interesseRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final PerfilProfissionalRepository perfilProfissionalRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AvaliacaoFotoService avaliacaoFotoService;
    private final AvaliacaoService avaliacaoService;
    private final PerfilFotoService perfilFotoService;
    private final NotificacaoService notificacaoService;

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

        Usuario profissionalUsuario = perfil.getUsuario();
        if (!solicitacao.getEndereco().getCidadeIbgeId().equals(profissionalUsuario.getEndereco().getCidadeIbgeId())) {
            throw new NegocioException("Esta solicitacao nao esta disponivel para sua regiao");
        }

        Set<Long> categoriasIds = perfil.getCategorias().stream()
                .map(Categoria::getId)
                .collect(Collectors.toSet());

        if (!categoriasIds.contains(solicitacao.getCategoria().getId())) {
            throw new NegocioException("Esta solicitacao nao esta disponivel para sua categoria");
        }

        if (solicitacao.getCliente().getId().equals(usuarioId)) {
            throw new NegocioException("Voce nao pode demonstrar interesse na sua propria solicitacao");
        }

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

        notificarCliente(solicitacao, perfil);

        return toResponse(interesse);
    }

    private void notificarCliente(Solicitacao solicitacao, PerfilProfissional profissional) {
        Long clienteId = solicitacao.getCliente().getId();
        String nomeProfissional = profissional.getUsuario().getNome();

        Double mediaAvaliacao = avaliacaoService.calcularMediaPorProfissional(profissional.getId());
        String avaliacaoStr = mediaAvaliacao != null ? String.format(" (%.1f)", mediaAvaliacao) : "";

        String titulo = "Novo interesse na sua solicitacao";
        String corpo = nomeProfissional + avaliacaoStr + " quer fazer seu servico!";

        notificacaoService.enviarParaUsuario(
            clienteId,
            TipoNotificacao.NOVO_INTERESSE,
            titulo,
            corpo,
            solicitacao.getId()
        );
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

    @Transactional(readOnly = true)
    public List<MeuTrabalhoResponse> listarMeusTrabalhos(Long usuarioId) {
        PerfilProfissional perfil = perfilProfissionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("Voce nao possui um perfil profissional"));

        return interesseRepository.findTrabalhosByProfissionalId(perfil.getId())
                .stream()
                .map(this::toMeuTrabalhoResponse)
                .collect(Collectors.toList());
    }

    private MeuTrabalhoResponse toMeuTrabalhoResponse(Interesse interesse) {
        Solicitacao solicitacao = interesse.getSolicitacao();
        Usuario cliente = solicitacao.getCliente();
        Endereco endereco = solicitacao.getEndereco();

        Integer avaliacaoNota = null;
        String avaliacaoComentario = null;
        java.time.LocalDateTime avaliacaoData = null;
        List<String> avaliacaoFotos = Collections.emptyList();

        if (solicitacao.getStatus() == StatusSolicitacao.CONCLUIDA) {
            var avaliacaoOpt = avaliacaoRepository.findBySolicitacaoId(solicitacao.getId());
            if (avaliacaoOpt.isPresent()) {
                Avaliacao avaliacao = avaliacaoOpt.get();
                avaliacaoNota = avaliacao.getNota();
                avaliacaoComentario = avaliacao.getComentario();
                avaliacaoData = avaliacao.getCriadoEm();
                avaliacaoFotos = avaliacaoFotoService.listarUrlsFotos(avaliacao.getId());
            }
        }

        return new MeuTrabalhoResponse(
                interesse.getId(),
                solicitacao.getId(),
                solicitacao.getTitulo(),
                solicitacao.getDescricao(),
                solicitacao.getCategoria().getNome(),
                solicitacao.getCategoria().getIcone(),
                solicitacao.getStatus().name(),
                cliente.getNome(),
                cliente.getCelular(),
                endereco.getBairro(),
                endereco.getCidadeNome(),
                endereco.getUf(),
                interesse.getAtualizadoEm(),
                avaliacaoNota,
                avaliacaoComentario,
                avaliacaoData,
                avaliacaoFotos
        );
    }

    private InteresseResponse toResponse(Interesse interesse) {
        PerfilProfissional perfil = interesse.getProfissional();
        Usuario usuario = perfil.getUsuario();
        int quantidadeFotos = perfilFotoService.contarFotos(perfil.getId());
        Double mediaAvaliacao = avaliacaoService.calcularMediaPorProfissional(perfil.getId());
        Long totalAvaliacoes = avaliacaoService.contarPorProfissional(perfil.getId());
        return new InteresseResponse(
                interesse.getId(),
                perfil.getId(),
                usuario.getNome(),
                usuario.getCelular(),
                perfil.getBio(),
                usuario.getFotoUrl(),
                quantidadeFotos,
                mediaAvaliacao,
                totalAvaliacoes,
                interesse.getMensagem(),
                interesse.getStatus().name(),
                interesse.getCriadoEm()
        );
    }
}
