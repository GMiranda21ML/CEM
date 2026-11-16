package br.com.cem.infraestrutura.monitoria.sessao;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoServico;
import br.com.cem.dominio.monitoria.sessao.RespostaSessao;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class SessaoRepositorio
		extends AgregadoRepositorioAbstrato<SessaoIdentificador, Sessao, Long, SessaoJpa>
		implements ISessaoRepositorio {
	private final ISessaoRepositorioJpa repositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;
	private final ICampusRepositorioJpa campusRepositorioJpa;

	@Override
	public boolean existePorMonitor(UsuarioIdentificador monitor) {
		Objects.requireNonNull(monitor);
		return repositorioJpa.existePorMonitor(Long.valueOf(monitor.toString()));
	}

	public SessaoRepositorio(
			ISessaoRepositorioJpa repositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa,
			ICampusRepositorioJpa campusRepositorioJpa) {
		super(repositorioJpa, SessaoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
		this.campusRepositorioJpa = Objects.requireNonNull(campusRepositorioJpa);
	}

	@Override
	protected SessaoJpa paraJpa(Sessao a) {
		Objects.requireNonNull(a);
		return new SessaoJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				a.getDisciplina(),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getProfessor().toString())),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getMonitor().toString())),
				campusRepositorioJpa.getReferenceById(Long.valueOf(a.getCampus().toString())),
				Objects.requireNonNullElse(a.getSala(), "Sala não informada"),
				a.getAlunos().stream()
						.map(
								id ->
										usuarioRepositorioJpa.getReferenceById(
												Long.valueOf(id.toString())))
						.toList(),
				a.getRespostas().stream()
						.map(
							r ->
								new RespostaSessaoJpa(
										usuarioRepositorioJpa.getReferenceById(
												Long.valueOf(r.getAluno().toString())),
										r.getSituacao(),
										r.getMotivo().orElse(null),
										r.getInicioSugerido().orElse(null)))
						.toList(),
				a.getInicio(),
				a.getFim(),
				a.getInicioAnterior().orElse(null),
				a.getCancelada());
	}

	@Override
	protected Sessao paraAgregado(SessaoJpa a) {
		Objects.requireNonNull(a);
		return Sessao.restaurar(
				Optional.of(
						new SessaoIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				a.getDisciplina(),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getProfessor().getIdentificador()).toString()),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getMonitor().getIdentificador()).toString()),
				new CampusIdentificador(
						Objects.requireNonNull(a.getCampus().getIdentificador()).toString()),
				a.getSala(),
				a.getAlunos().stream()
						.map(
								u ->
										new UsuarioIdentificador(
												Objects.requireNonNull(u.getIdentificador())
														.toString()))
						.toList(),
				a.getRespostas().stream()
						.map(
							r ->
								new RespostaSessao(
										new UsuarioIdentificador(
												Objects.requireNonNull(r.getAluno().getIdentificador())
													.toString()),
										r.getSituacao(),
										Optional.ofNullable(r.getMotivo()),
										Optional.ofNullable(r.getInicioSugerido())))
						.toList(),
				a.getInicio(),
				a.getFim(),
				Optional.ofNullable(a.getInicioAnterior()),
				a.getCancelada());
	}

	@Override
	protected Long paraIdentificadorJpa(SessaoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					SessaoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(SessaoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean conflita(
			UsuarioIdentificador monitor,
			UsuarioIdentificador aluno,
			LocalDateTime inicio,
			LocalDateTime fim) {
		return conflita(monitor, aluno, inicio, fim, Optional.empty());
	}

	@Override
	public boolean conflita(
			UsuarioIdentificador monitor,
			UsuarioIdentificador aluno,
			LocalDateTime inicio,
			LocalDateTime fim,
			Optional<SessaoIdentificador> ignorada) {
		return repositorioJpa.conflita(
				Long.valueOf(monitor.toString()),
				Long.valueOf(aluno.toString()),
				Objects.requireNonNull(inicio),
				Objects.requireNonNull(fim),
				Objects.requireNonNull(ignorada).map(id -> Long.valueOf(id.toString())).orElse(null));
	}

	@Override
	public void bloquearAgenda(UsuarioIdentificador monitor) {
		usuarioRepositorioJpa.bloquearAgenda(Long.valueOf(monitor.toString()));
	}
}
