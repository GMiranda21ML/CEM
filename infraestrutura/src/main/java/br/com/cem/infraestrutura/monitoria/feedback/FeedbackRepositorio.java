package br.com.cem.infraestrutura.monitoria.feedback;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.FeedbackIdentificador;
import br.com.cem.dominio.monitoria.feedback.FeedbackServico;
import br.com.cem.dominio.monitoria.feedback.spi.IFeedbackRepositorio;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.ISessaoRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FeedbackRepositorio
		extends AgregadoRepositorioAbstrato<FeedbackIdentificador, Feedback, Long, FeedbackJpa>
		implements IFeedbackRepositorio {
	private final IFeedbackRepositorioJpa repositorioJpa;
	private final ISessaoRepositorioJpa sessaoRepositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public FeedbackRepositorio(
			IFeedbackRepositorioJpa repositorioJpa,
			ISessaoRepositorioJpa sessaoRepositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, FeedbackServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.sessaoRepositorioJpa = Objects.requireNonNull(sessaoRepositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected FeedbackJpa paraJpa(Feedback a) {
		Objects.requireNonNull(a);
		return new FeedbackJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				sessaoRepositorioJpa.getReferenceById(Long.valueOf(a.getSessao().toString())),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getAluno().toString())),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getAutor().toString())),
				a.getNota(),
				a.getComentario().orElse(null),
				a.getCriadoEm(),
				a.getAtualizadoEm().orElse(null));
	}

	@Override
	protected Feedback paraAgregado(FeedbackJpa a) {
		Objects.requireNonNull(a);
		return new Feedback(
				Optional.of(
						new FeedbackIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				new SessaoIdentificador(
						Objects.requireNonNull(a.getSessao().getIdentificador()).toString()),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getAluno().getIdentificador()).toString()),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getAutor().getIdentificador()).toString()),
				a.getNota(),
				Optional.ofNullable(a.getComentario()),
				a.getCriadoEm(),
				Optional.ofNullable(a.getAtualizadoEm()));
	}

	@Override
	protected Long paraIdentificadorJpa(FeedbackIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					FeedbackServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(FeedbackIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean existePorSessaoEAluno(SessaoIdentificador sessao, UsuarioIdentificador aluno) {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(aluno);
		return repositorioJpa.existePorSessaoEAluno(
				Long.valueOf(sessao.toString()), Long.valueOf(aluno.toString()));
	}

	@Override
	public boolean existePorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplina);
		return repositorioJpa.existePorAlunoEDisciplina(
				Long.valueOf(aluno.toString()), Long.valueOf(disciplina.toString()));
	}

	@Override
	public List<Feedback> listarPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplina);
		return repositorioJpa
				.listarPorAlunoEDisciplina(
						Long.valueOf(aluno.toString()), Long.valueOf(disciplina.toString()))
				.stream()
				.map(this::paraAgregado)
				.toList();
	}
}
