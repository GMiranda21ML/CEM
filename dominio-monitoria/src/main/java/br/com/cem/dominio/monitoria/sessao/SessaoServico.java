package br.com.cem.dominio.monitoria.sessao;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.apache.commons.lang3.Validate;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public final class SessaoServico
		extends AgregadoServicoAbstrato<SessaoIdentificador, Sessao, SessaoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe sessao com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe sessao com o identificador %s";
	private final ISessaoRepositorio repositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final ICampusRepositorio campusRepositorio;

	public SessaoServico(
			ISegurancaServico seguranca,
			ISessaoRepositorio repositorio,
			IUsuarioRepositorio usuarioRepositorio,
			ICampusRepositorio campusRepositorio) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.campusRepositorio = Objects.requireNonNull(campusRepositorio);
	}

	@Override
	public SessaoAutorizacoes criarAutorizacoes(Optional<SessaoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean coord = papel(Autorizacao.COORDENADOR);
		boolean proprio =
				identificador.isEmpty()
						|| !repositorio.existePorIdentificador(identificador.get())
						|| repositorio
								.buscarPorIdentificador(identificador.get())
								.participa(usuarioAtual());
		return new SessaoAutorizacoes(
				coord,
				coord || proprio,
				false,
				false,
				coord
						|| (proprio && papel(Autorizacao.PROFESSOR))
						|| (identificador.isPresent()
								&& repositorio.existePorIdentificador(identificador.get())
								&& repositorio
										.buscarPorIdentificador(identificador.get())
										.getMonitor()
										.equals(usuarioAtual())));
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

	@Override
	protected void validarInclusao(Sessao a) {
		Objects.requireNonNull(a);
		Validate.isTrue(
				a.getIdentificador().isEmpty() && !a.getCancelada(), "Dados iniciais inválidos");
	}

	@Override
	protected void validarInclusaoAlteracao(Sessao a) {
		Objects.requireNonNull(a);
		Validate.validState(
				campusRepositorio.buscarPorIdentificador(a.getCampus()).estaAtivo(),
				"Campus inativo");
		Validate.isTrue(
				usuarioRepositorio.buscarPorIdentificador(a.getProfessor()).getPapel()
						== Autorizacao.PROFESSOR,
				"Professor inválido");
		Validate.isTrue(
				usuarioRepositorio.buscarPorIdentificador(a.getMonitor()).getPapel()
						== Autorizacao.ALUNO,
				"Monitor deve ser aluno");
		java.util.stream.Stream.concat(
						java.util.stream.Stream.of(a.getMonitor()), a.getAlunos().stream())
				.distinct()
				.sorted(Comparator.comparing(UsuarioIdentificador::toString))
				.forEach(repositorio::bloquearAgenda);
		for (var aluno : a.getAlunos()) {
			Validate.isTrue(
					usuarioRepositorio.buscarPorIdentificador(aluno).getPapel()
							== Autorizacao.ALUNO,
					"Participante deve ser aluno");
			Validate.validState(
					!repositorio.conflita(a.getMonitor(), aluno, a.getInicio(), a.getFim()),
					"Conflito na agenda");
		}
	}

	public void cancelar(SessaoIdentificador id) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), SessaoAutorizacoes::getCancelar);
		validarExistencia(id);
		var a = repositorio.buscarPorIdentificador(id);
		repositorio.bloquearAgenda(a.getMonitor());
		repositorio.alterar(a.cancelar());
	}
}
