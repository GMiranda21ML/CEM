package br.com.cem.infraestrutura.monitoria.candidatura;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaIdentificador;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaServico;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.selecao.ISelecaoRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class CandidaturaRepositorio
		extends AgregadoRepositorioAbstrato<
				CandidaturaIdentificador, Candidatura, Long, CandidaturaJpa>
		implements ICandidaturaRepositorio {
	private final ICandidaturaRepositorioJpa repositorioJpa;
	private final ISelecaoRepositorioJpa selecaoRepositorioJpa;
	private final IDisciplinaRepositorioJpa disciplinaRepositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public CandidaturaRepositorio(
			ICandidaturaRepositorioJpa repositorioJpa,
			ISelecaoRepositorioJpa selecaoRepositorioJpa,
			IDisciplinaRepositorioJpa disciplinaRepositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, CandidaturaServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.selecaoRepositorioJpa = Objects.requireNonNull(selecaoRepositorioJpa);
		this.disciplinaRepositorioJpa = Objects.requireNonNull(disciplinaRepositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected CandidaturaJpa paraJpa(Candidatura a) {
		Objects.requireNonNull(a);
		return new CandidaturaJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				selecaoRepositorioJpa.getReferenceById(Long.valueOf(a.getSelecao().toString())),
				disciplinaRepositorioJpa.getReferenceById(
						Long.valueOf(a.getDisciplina().toString())),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getAluno().toString())),
				a.getCr(),
				a.getSituacao(),
				a.getNotaEntrevista().orElse(null),
				a.getComentarioEntrevista().orElse(null),
				a.getResultadoFinal().orElse(null),
				a.getMotivoDecisao().orElse(null),
				a.getCriadaEm(),
				a.getDecididaEm().orElse(null));
	}

	@Override
	protected Candidatura paraAgregado(CandidaturaJpa a) {
		Objects.requireNonNull(a);
		return new Candidatura(
				Optional.of(
						new CandidaturaIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				new SelecaoIdentificador(
						Objects.requireNonNull(a.getSelecao().getIdentificador()).toString()),
				new DisciplinaIdentificador(
						Objects.requireNonNull(a.getDisciplina().getIdentificador()).toString()),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getAluno().getIdentificador()).toString()),
				a.getCr(),
				a.getSituacao(),
				Optional.ofNullable(a.getNotaEntrevista()),
				Optional.ofNullable(a.getComentarioEntrevista()),
				Optional.ofNullable(a.getResultadoFinal()),
				Optional.ofNullable(a.getMotivoDecisao()),
				a.getCriadaEm(),
				Optional.ofNullable(a.getDecididaEm()));
	}

	@Override
	protected Long paraIdentificadorJpa(CandidaturaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					CandidaturaServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(CandidaturaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean existePorSelecao(SelecaoIdentificador selecao) {
		Objects.requireNonNull(selecao);
		return repositorioJpa.existePorSelecao(Long.valueOf(selecao.toString()));
	}

	@Override
	public boolean existeAtivaPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplina);
		return repositorioJpa.existeAtivaPorAlunoEDisciplina(
				Long.valueOf(aluno.toString()), Long.valueOf(disciplina.toString()));
	}

	@Override
	public long contarMonitoriasAtivasNoSemestre(
			UsuarioIdentificador aluno,
			String semestre,
			DisciplinaIdentificador disciplinaIgnorada) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(semestre);
		Objects.requireNonNull(disciplinaIgnorada);
		return repositorioJpa.contarMonitoriasAtivasNoSemestre(
				Long.valueOf(aluno.toString()),
				semestre,
				Long.valueOf(disciplinaIgnorada.toString()));
	}

	@Override
	public boolean ehMonitorAtivo(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplina);
		return repositorioJpa.ehMonitorAtivo(
				Long.valueOf(aluno.toString()), Long.valueOf(disciplina.toString()));
	}

	@Override
	public boolean ehMonitorEmAlgumaDisciplina(UsuarioIdentificador aluno) {
		Objects.requireNonNull(aluno);
		return repositorioJpa.ehMonitorEmAlgumaDisciplina(Long.valueOf(aluno.toString()));
	}

	@Override
	public Collection<Candidatura> listarPorSelecao(SelecaoIdentificador selecao) {
		Objects.requireNonNull(selecao);
		return repositorioJpa.listarPorSelecao(Long.valueOf(selecao.toString())).stream()
				.map(this::paraAgregado)
				.toList();
	}
}
