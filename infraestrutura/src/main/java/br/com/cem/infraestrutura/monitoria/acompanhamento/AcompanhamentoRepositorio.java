package br.com.cem.infraestrutura.monitoria.acompanhamento;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoServico;
import br.com.cem.dominio.monitoria.acompanhamento.spi.IAcompanhamentoRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Objects;
import java.util.Optional;

public class AcompanhamentoRepositorio
		extends AgregadoRepositorioAbstrato<
				AcompanhamentoIdentificador, Acompanhamento, Long, AcompanhamentoJpa>
		implements IAcompanhamentoRepositorio {
	private final IAcompanhamentoRepositorioJpa repositorioJpa;
	private final IDisciplinaRepositorioJpa disciplinaRepositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public AcompanhamentoRepositorio(
			IAcompanhamentoRepositorioJpa repositorioJpa,
			IDisciplinaRepositorioJpa disciplinaRepositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, AcompanhamentoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.disciplinaRepositorioJpa = Objects.requireNonNull(disciplinaRepositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected AcompanhamentoJpa paraJpa(Acompanhamento a) {
		Objects.requireNonNull(a);
		return new AcompanhamentoJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getAluno().toString())),
				disciplinaRepositorioJpa.getReferenceById(
						Long.valueOf(a.getDisciplina().toString())),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getMonitor().toString())),
				a.getSemestre(),
				a.getMetaNota(),
				a.getMetaFrequencia(),
				a.getSituacao(),
				a.getAbertoEm(),
				a.getEncerradoEm().orElse(null),
				a.getParecerFinal().orElse(null));
	}

	@Override
	protected Acompanhamento paraAgregado(AcompanhamentoJpa a) {
		Objects.requireNonNull(a);
		return new Acompanhamento(
				Optional.of(
						new AcompanhamentoIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getAluno().getIdentificador()).toString()),
				new DisciplinaIdentificador(
						Objects.requireNonNull(a.getDisciplina().getIdentificador()).toString()),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getMonitor().getIdentificador()).toString()),
				a.getSemestre(),
				a.getMetaNota(),
				a.getMetaFrequencia(),
				a.getSituacao(),
				a.getAbertoEm(),
				Optional.ofNullable(a.getEncerradoEm()),
				Optional.ofNullable(a.getParecerFinal()));
	}

	@Override
	protected Long paraIdentificadorJpa(AcompanhamentoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					AcompanhamentoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(AcompanhamentoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean existeAtivoPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplina);
		return repositorioJpa.existeAtivoPorAlunoEDisciplina(
				Long.valueOf(aluno.toString()), Long.valueOf(disciplina.toString()));
	}

	@Override
	public boolean existeEncerradoPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplina);
		return repositorioJpa.existeEncerradoPorAlunoEDisciplina(
				Long.valueOf(aluno.toString()), Long.valueOf(disciplina.toString()));
	}

	@Override
	public boolean existeBloqueioPorEncerramento(
			UsuarioIdentificador aluno,
			String disciplinaNomeOuCodigo,
			UsuarioIdentificador professor) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplinaNomeOuCodigo);
		Objects.requireNonNull(professor);
		return repositorioJpa.existeBloqueioPorEncerramento(
				Long.valueOf(aluno.toString()),
				disciplinaNomeOuCodigo,
				Long.valueOf(professor.toString()));
	}
}
