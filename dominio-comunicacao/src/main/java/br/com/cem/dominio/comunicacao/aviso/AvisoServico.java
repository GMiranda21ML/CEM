package br.com.cem.dominio.comunicacao.aviso;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.comunicacao.aviso.spi.IAvisoRepositorio;
import br.com.cem.dominio.comunicacao.aviso.spi.IMonitorAvisoServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class AvisoServico
		extends AgregadoServicoAbstrato<AvisoIdentificador, Aviso, AvisoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe aviso com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe aviso com o identificador %s";
	private final IAvisoRepositorio repositorio;
	private final IRelogioServico relogio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IMonitorAvisoServico monitorServico;

	public AvisoServico(
			ISegurancaServico seguranca,
			IAvisoRepositorio repositorio,
			IRelogioServico relogio,
			IUsuarioRepositorio usuarioRepositorio,
			IMonitorAvisoServico monitorServico) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.relogio = Objects.requireNonNull(relogio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.monitorServico = Objects.requireNonNull(monitorServico);
	}

	@Override
	public AvisoAutorizacoes criarAutorizacoes(Optional<AvisoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean coord = papel(Autorizacao.COORDENADOR);
		boolean publicar = coord || papel(Autorizacao.PROFESSOR) || monitorAutenticado();
		boolean visivel =
				identificador.isEmpty()
						|| !repositorio.existePorIdentificador(identificador.get())
						|| repositorio
								.buscarPorIdentificador(identificador.get())
								.visivel(relogio.agora().toLocalDate());
		return new AvisoAutorizacoes(false, coord || visivel, coord, false, publicar, coord);
	}

	private boolean monitorAutenticado() {
		if (!papel(Autorizacao.ALUNO)) {
			return false;
		}
		var usuario =
				usuarioRepositorio.buscarPorEmail(
						new UsuarioEmail(getSegurancaServico().getUsuario()));
		return monitorServico.eMonitor(usuario.getIdentificador().orElseThrow());
	}

	private boolean papel(Autorizacao papel) {
		return getSegurancaServico().possuiAutorizacao(papel.name());
	}

	public Aviso publicar(String titulo, String texto, LocalDate validade) {
		validarAutorizacao(Optional.empty(), AvisoAutorizacoes::getPublicar);
		var a = new Aviso(Optional.empty(), titulo, texto, validade, true, relogio.agora());
		a.validarValidade(relogio.agora().toLocalDate());
		return repositorio.incluir(a);
	}

	@Override
	protected void validarAlteracao(Aviso a) {
		Objects.requireNonNull(a);
		a.validarValidade(relogio.agora().toLocalDate());
		var atual = repositorio.buscarPorIdentificador(a.getIdentificador().orElseThrow());
		Validate.isTrue(
				a.getPublicadoEm().equals(atual.getPublicadoEm()),
				"A data de publicação é preservada");
	}

	public void alternar(AvisoIdentificador id) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), AvisoAutorizacoes::getAlternar);
		validarExistencia(id);
		repositorio.alterar(repositorio.buscarPorIdentificador(id).alternar());
	}
}
