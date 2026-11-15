package br.com.cem.infraestrutura.monitoria.alerta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.alerta.Alerta;
import br.com.cem.dominio.monitoria.alerta.AlertaIdentificador;
import br.com.cem.dominio.monitoria.alerta.AlertaServico;
import br.com.cem.dominio.monitoria.alerta.spi.IAlertaRepositorio;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Objects;
import java.util.Optional;

public class AlertaRepositorio
		extends AgregadoRepositorioAbstrato<AlertaIdentificador, Alerta, Long, AlertaJpa>
		implements IAlertaRepositorio {
	private final IAlertaRepositorioJpa repositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public AlertaRepositorio(
			IAlertaRepositorioJpa repositorioJpa, IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, AlertaServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected AlertaJpa paraJpa(Alerta a) {
		Objects.requireNonNull(a);
		return new AlertaJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getPessoa().toString())),
				a.getPapel(),
				a.getDisciplina(),
				a.getAcompanhado(),
				a.getCriadoEm());
	}

	@Override
	protected Alerta paraAgregado(AlertaJpa a) {
		Objects.requireNonNull(a);
		return new Alerta(
				Optional.of(
						new AlertaIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getPessoa().getIdentificador()).toString()),
				a.getPapel(),
				a.getDisciplina(),
				a.getAcompanhado(),
				a.getCriadoEm());
	}

	@Override
	protected Long paraIdentificadorJpa(AlertaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					AlertaServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(AlertaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean existeNovo(UsuarioIdentificador pessoa, String papel, String disciplina) {
		return repositorioJpa.existeNovo(
				Long.valueOf(pessoa.toString()),
				Objects.requireNonNull(papel),
				Objects.requireNonNull(disciplina));
	}
}
