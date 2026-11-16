package br.com.cem.infraestrutura.monitoria.disponibilidade;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeIdentificador;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeServico;
import br.com.cem.dominio.monitoria.disponibilidade.spi.IDisponibilidadeRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DisponibilidadeRepositorio
		extends AgregadoRepositorioAbstrato<
				DisponibilidadeIdentificador, Disponibilidade, Long, DisponibilidadeJpa>
		implements IDisponibilidadeRepositorio {
	private final IDisponibilidadeRepositorioJpa repositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;
	private final ICampusRepositorioJpa campusRepositorioJpa;

	public DisponibilidadeRepositorio(
			IDisponibilidadeRepositorioJpa repositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa,
			ICampusRepositorioJpa campusRepositorioJpa) {
		super(repositorioJpa, DisponibilidadeServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
		this.campusRepositorioJpa = Objects.requireNonNull(campusRepositorioJpa);
	}

	@Override
	protected DisponibilidadeJpa paraJpa(Disponibilidade a) {
		Objects.requireNonNull(a);
		return new DisponibilidadeJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				a.getDisciplina(),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getMonitor().toString())),
				campusRepositorioJpa.getReferenceById(Long.valueOf(a.getCampus().toString())),
				a.getDiaSemana(),
				a.getInicio(),
				a.getFim());
	}

	@Override
	protected Disponibilidade paraAgregado(DisponibilidadeJpa a) {
		Objects.requireNonNull(a);
		return new Disponibilidade(
				Optional.of(
						new DisponibilidadeIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				a.getDisciplina(),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getMonitor().getIdentificador()).toString()),
				new CampusIdentificador(
						Objects.requireNonNull(a.getCampus().getIdentificador()).toString()),
				a.getDiaSemana(),
				a.getInicio(),
				a.getFim());
	}

	@Override
	protected Long paraIdentificadorJpa(DisponibilidadeIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					DisponibilidadeServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(DisponibilidadeIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public List<Disponibilidade> buscarDoMonitor(UsuarioIdentificador monitor, String disciplina) {
		return repositorioJpa
				.buscarDoMonitor(
						Long.valueOf(monitor.toString()), Objects.requireNonNull(disciplina))
				.stream()
				.map(this::paraAgregado)
				.toList();
	}

	@Override
	public List<Disponibilidade> listarDoMonitor(UsuarioIdentificador monitor) {
		return repositorioJpa.listarDoMonitor(Long.valueOf(monitor.toString())).stream()
				.map(this::paraAgregado)
				.toList();
	}
}
