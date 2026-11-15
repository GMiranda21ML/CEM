package br.com.cem.dominio.comunicacao.pergunta;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.comunicacao.faq.Faq;
import br.com.cem.dominio.comunicacao.faq.FaqServico;
import br.com.cem.dominio.comunicacao.pergunta.spi.IPerguntaRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.Optional;

public final class PerguntaServico
		extends AgregadoServicoAbstrato<PerguntaIdentificador, Pergunta, PerguntaAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe pergunta com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe pergunta com o identificador %s";
	private final IPerguntaRepositorio repositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogio;
	private final FaqServico faqServico;
	private final IEnviadorEmailServico emailServico;

	public PerguntaServico(
			ISegurancaServico seguranca,
			IPerguntaRepositorio repositorio,
			IUsuarioRepositorio usuarioRepositorio,
			IRelogioServico relogio,
			FaqServico faqServico,
			IEnviadorEmailServico emailServico) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogio = Objects.requireNonNull(relogio);
		this.faqServico = Objects.requireNonNull(faqServico);
		this.emailServico = Objects.requireNonNull(emailServico);
	}

	@Override
	public PerguntaAutorizacoes criarAutorizacoes(Optional<PerguntaIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean coord = papel(Autorizacao.COORDENADOR);
		var pergunta =
				identificador
						.filter(repositorio::existePorIdentificador)
						.map(repositorio::buscarPorIdentificador);
		boolean participante = papel(Autorizacao.ALUNO) || papel(Autorizacao.PROFESSOR);
		boolean proprio = pergunta.map(a -> a.getAutor().equals(usuarioAtual())).orElse(true);
		boolean pendente = pergunta.map(Pergunta::pendente).orElse(true);
		return new PerguntaAutorizacoes(
				false,
				coord || participante && proprio,
				false,
				participante && proprio && pendente,
				participante,
				participante && proprio && pendente,
				coord && pendente,
				coord && pendente);
	}

	private boolean papel(Autorizacao papel) {
		return getSegurancaServico().possuiAutorizacao(papel.name());
	}

	private UsuarioIdentificador usuarioAtual() {
		return usuarioRepositorio
				.buscarPorEmail(new UsuarioEmail(getSegurancaServico().getUsuario()))
				.getIdentificador()
				.orElseThrow();
	}

	public Pergunta enviar(String texto) {
		validarAutorizacao(Optional.empty(), PerguntaAutorizacoes::getEnviar);
		return repositorio.incluir(
				new Pergunta(
						Optional.empty(),
						usuarioAtual(),
						texto,
						"PENDENTE",
						Optional.empty(),
						relogio.agora()));
	}

	public void editar(PerguntaIdentificador id, String texto) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), PerguntaAutorizacoes::getEditar);
		validarExistencia(id);
		repositorio.bloquear(id);
		var a = repositorio.buscarPorIdentificador(id);
		repositorio.alterar(a.editar(texto));
	}

	@Override
	protected void validarExclusao(PerguntaIdentificador id) {
		Objects.requireNonNull(id);
		repositorio.bloquear(id);
		Validate.validState(
				repositorio.buscarPorIdentificador(id).pendente(),
				"Somente perguntas pendentes podem ser excluídas");
	}

	public void responder(
			PerguntaIdentificador id, String pergunta, String resposta, boolean ativo) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), PerguntaAutorizacoes::getResponder);
		validarExistencia(id);
		repositorio.bloquear(id);
		var a = repositorio.buscarPorIdentificador(id);
		var respondida = a.responder(resposta);
		faqServico.incluir(new Faq(Optional.empty(), pergunta, resposta, ativo));
		repositorio.alterar(respondida);
		var autor = usuarioRepositorio.buscarPorIdentificador(a.getAutor());
		emailServico.enviar(
				autor.getEmailInstitucional().toString(), "Sua dúvida foi respondida", resposta);
	}

	public void descartar(PerguntaIdentificador id) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), PerguntaAutorizacoes::getDescartar);
		validarExistencia(id);
		repositorio.bloquear(id);
		repositorio.alterar(repositorio.buscarPorIdentificador(id).descartar());
	}
}
