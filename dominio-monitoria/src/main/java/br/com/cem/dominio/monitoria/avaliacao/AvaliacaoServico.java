package br.com.cem.dominio.monitoria.avaliacao;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.avaliacao.spi.IAvaliacaoRepositorio;
import br.com.cem.dominio.monitoria.avaliacao.spi.ISessaoAvaliacaoServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class AvaliacaoServico
		extends AgregadoServicoAbstrato<AvaliacaoIdentificador, Avaliacao, AvaliacaoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO = "Não existe avaliação com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO = "Já existe uma avaliação com o identificador %s";
	public static final String AVALIACAO_EXISTENTE_MODELO = "O participante já avaliou a sessão %s";
	public static final String SESSAO_NAO_REALIZADA = "Somente sessões realizadas podem ser avaliadas";
	public static final String SEM_PARTICIPACAO = "Somente participantes da sessão podem avaliá-la";
	public static final String PRAZO_ENCERRADO = "O prazo de sete dias para avaliar a sessão foi encerrado";
	public static final String OUTRO_AUTOR = "Somente o autor pode editar a avaliação";

	private final IAvaliacaoRepositorio repositorio;
	private final ISessaoAvaliacaoServico sessaoServico;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogioServico;

	public AvaliacaoServico(ISegurancaServico segurancaServico, IAvaliacaoRepositorio repositorio,
			ISessaoAvaliacaoServico sessaoServico, IUsuarioRepositorio usuarioRepositorio,
			IRelogioServico relogioServico) {
		super(segurancaServico, repositorio, IDENTIFICADOR_EXISTENTE_MODELO, IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.sessaoServico = Objects.requireNonNull(sessaoServico);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogioServico = Objects.requireNonNull(relogioServico);
	}

	@Override
	public AvaliacaoAutorizacoes criarAutorizacoes(Optional<AvaliacaoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		var participante = getSegurancaServico().possuiAutorizacao(Autorizacao.ALUNO.name())
				|| getSegurancaServico().possuiAutorizacao(Autorizacao.PROFESSOR.name());
		var autoria = identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())
				|| repositorio.buscarPorIdentificador(identificador.get()).getAutor().equals(usuarioAtual());
		return new AvaliacaoAutorizacoes(false, participante && autoria, false, false, participante,
				participante && autoria);
	}

	public Avaliacao registrar(SessaoIdentificador sessao, int nota, @Nullable String comentario) {
		Objects.requireNonNull(sessao);
		validarAutorizacao(Optional.empty(), AvaliacaoAutorizacoes::getRegistrar);
		var autor = usuarioAtual();
		validarInexistencia(chave -> repositorio.existePorSessaoEAutor(chave, autor), sessao,
				AVALIACAO_EXISTENTE_MODELO);
		var agora = relogioServico.agora();
		var sessaoAvaliavel = sessaoServico.buscarParaAvaliacao(sessao, autor);
		validarSessao(sessaoAvaliavel, agora, true);
		var avaliacao = new Avaliacao(Optional.empty(), sessao, autor, sessaoAvaliavel.perfil(), nota, comentario, agora,
				agora);
		return repositorio.incluir(avaliacao);
	}

	public void editar(AvaliacaoIdentificador identificador, int nota, @Nullable String comentario) {
		Objects.requireNonNull(identificador);
		validarAutorizacao(Optional.of(identificador), AvaliacaoAutorizacoes::getEditar);
		validarExistencia(identificador);
		var avaliacao = repositorio.buscarPorIdentificador(identificador);
		Validate.validState(avaliacao.getAutor().equals(usuarioAtual()), OUTRO_AUTOR);
		var agora = relogioServico.agora();
		var sessao = sessaoServico.buscarParaAvaliacao(avaliacao.getSessao(), avaliacao.getAutor());
		validarSessao(sessao, agora, true);
		repositorio.alterar(avaliacao.editar(nota, comentario, agora));
	}

	public void validarConsultaDaSessao(SessaoIdentificador sessao) {
		Objects.requireNonNull(sessao);
		validarAutorizacao(Optional.empty(), AvaliacaoAutorizacoes::getRegistrar);
		validarSessao(sessaoServico.buscarParaAvaliacao(sessao, usuarioAtual()), relogioServico.agora(), false);
	}

	private void validarSessao(SessaoAvaliavel sessao, LocalDateTime agora, boolean validarPrazo) {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(agora);
		Validate.validState(sessao.realizada() && !sessao.realizadaEm().isAfter(agora), SESSAO_NAO_REALIZADA);
		Validate.validState(sessao.participou(), SEM_PARTICIPACAO);
		if (validarPrazo) {
			Validate.validState(!agora.isAfter(sessao.realizadaEm().plusDays(7)), PRAZO_ENCERRADO);
		}
	}

	private UsuarioIdentificador usuarioAtual() {
		return usuarioRepositorio.buscarPorEmail(new UsuarioEmail(getSegurancaServico().getUsuario())).getIdentificador()
				.orElseThrow();
	}
}
