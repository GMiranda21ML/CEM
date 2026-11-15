package br.com.cem.dominio.monitoria.alerta;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.monitoria.alerta.spi.IAlertaRepositorio;
import br.com.cem.dominio.monitoria.alerta.spi.ICoordenadoresAlertaServico;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import java.util.Objects;
import java.util.Optional;

public final class AlertaServico
		extends AgregadoServicoAbstrato<AlertaIdentificador, Alerta, AlertaAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe alerta com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe alerta com o identificador %s";
	private final IAlertaRepositorio repositorio;
	private final IFaltaRepositorio faltas;
	private final ICoordenadoresAlertaServico coordenadores;

	public AlertaServico(
			ISegurancaServico seguranca, IAlertaRepositorio repositorio, IFaltaRepositorio faltas,
			ICoordenadoresAlertaServico coordenadores) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.faltas = Objects.requireNonNull(faltas);
		this.coordenadores = Objects.requireNonNull(coordenadores);
	}

	@Override
	public AlertaAutorizacoes criarAutorizacoes(Optional<AlertaIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean coord = papel(Autorizacao.COORDENADOR)
				&& identificador.filter(repositorio::existePorIdentificador)
						.map(repositorio::buscarPorIdentificador)
						.map(a -> coordenadores.buscarPorPessoa(a.getPessoa()).stream()
								.anyMatch(u -> u.getEmailInstitucional().toString()
										.equalsIgnoreCase(getSegurancaServico().getUsuario())))
						.orElse(true);
		return new AlertaAutorizacoes(false, coord, false, false, coord);
	}

	private boolean papel(Autorizacao papel) {
		return getSegurancaServico().possuiAutorizacao(papel.name());
	}

	public void acompanhar(AlertaIdentificador id) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), AlertaAutorizacoes::getAcompanhar);
		validarExistencia(id);
		repositorio.alterar(repositorio.buscarPorIdentificador(id).acompanhar());
	}

	public long contar(AlertaIdentificador id) {
		var a = buscar(id);
		return faltas.contar(a.getPessoa(), a.getPapel(), a.getDisciplina());
	}
}
