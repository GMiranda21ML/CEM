package br.com.cem.dominio.monitoria.disponibilidade;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.disponibilidade.spi.IDisponibilidadeRepositorio;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.Optional;

public final class DisponibilidadeServico
		extends AgregadoServicoAbstrato<
				DisponibilidadeIdentificador,
				Disponibilidade,
				AgregadoAutorizacoes<Disponibilidade>> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe disponibilidade com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe disponibilidade com o identificador %s";
	private final IDisponibilidadeRepositorio repositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final ICampusRepositorio campusRepositorio;

	public DisponibilidadeServico(
			ISegurancaServico seguranca,
			IDisponibilidadeRepositorio repositorio,
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
	public AgregadoAutorizacoes<Disponibilidade> criarAutorizacoes(
			Optional<DisponibilidadeIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean coord = papel(Autorizacao.COORDENADOR);
		boolean proprio =
				identificador.isEmpty()
						|| !repositorio.existePorIdentificador(identificador.get())
						|| repositorio
								.buscarPorIdentificador(identificador.get())
								.getMonitor()
								.equals(usuarioAtual());
		return new AgregadoAutorizacoes<>(
				coord || papel(Autorizacao.ALUNO),
				coord || proprio,
				coord || proprio,
				coord || proprio);
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
	protected void validarInclusao(Disponibilidade a) {
		Objects.requireNonNull(a);
		Validate.isTrue(a.getIdentificador().isEmpty(), "Identificador gerado automaticamente");
	}

	@Override
	protected void validarInclusaoAlteracao(Disponibilidade a) {
		Objects.requireNonNull(a);
		Validate.isTrue(
				papel(Autorizacao.COORDENADOR) || a.getMonitor().equals(usuarioAtual()),
				"Somente o monitor pode manter sua disponibilidade");
		Validate.isTrue(
				usuarioRepositorio.buscarPorIdentificador(a.getMonitor()).getPapel()
						== Autorizacao.ALUNO,
				"Monitor deve ser aluno");
		Validate.validState(
				campusRepositorio.buscarPorIdentificador(a.getCampus()).estaAtivo(),
				"Campus inativo");
	}
}
