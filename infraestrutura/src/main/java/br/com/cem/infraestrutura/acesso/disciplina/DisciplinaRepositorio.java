package br.com.cem.infraestrutura.acesso.disciplina;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.disciplina.DisciplinaServico;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class DisciplinaRepositorio extends AgregadoRepositorioAbstrato<DisciplinaIdentificador, Disciplina, Long, DisciplinaJpa>
		implements IDisciplinaRepositorio {
	private final IDisciplinaRepositorioJpa repositorioJpa;
	private final ICursoRepositorioJpa cursosJpa;
	private final IUsuarioRepositorioJpa usuariosJpa;

	public DisciplinaRepositorio(IDisciplinaRepositorioJpa repositorioJpa, ICursoRepositorioJpa cursosJpa,
			IUsuarioRepositorioJpa usuariosJpa) {
		super(repositorioJpa, DisciplinaServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.cursosJpa = Objects.requireNonNull(cursosJpa);
		this.usuariosJpa = Objects.requireNonNull(usuariosJpa);
	}

	@Override protected DisciplinaJpa paraJpa(Disciplina d) {
		Objects.requireNonNull(d);
		var id = d.getIdentificador().map(this::paraIdentificadorJpa).orElse(null);
		var cursoId = parseLong(d.getCurso().toString()).orElseThrow(() -> new AgregadoInexistenteExcecao(DisciplinaServico.CURSO_INEXISTENTE));
		var curso = cursosJpa.getReferenceById(cursoId);
		var professorId = parseLong(d.getProfessor().toString()).orElseThrow(() -> new AgregadoInexistenteExcecao(DisciplinaServico.PROFESSOR_INVALIDO));
		var professor = usuariosJpa.getReferenceById(professorId);
		return new DisciplinaJpa(id, d.getInstituicao().toString(), curso, professor, d.getNome(), d.getCodigo().toString(), d.getArea());
	}

	@Override protected Disciplina paraAgregado(DisciplinaJpa jpa) {
		Objects.requireNonNull(jpa);
		var id = new DisciplinaIdentificador(Objects.requireNonNull(jpa.getIdentificador()).toString());
		return new Disciplina(Optional.of(id), new InstituicaoIdentificador(jpa.getInstituicao()),
				new CursoIdentificador(Objects.requireNonNull(jpa.getCurso().getIdentificador()).toString()),
				new UsuarioIdentificador(Objects.requireNonNull(jpa.getProfessor().getIdentificador()).toString()),
				jpa.getNome(), new br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo(jpa.getCodigo()), jpa.getArea());
	}

	@Override protected Long paraIdentificadorJpa(DisciplinaIdentificador id) {
		Objects.requireNonNull(id);
		return parseLong(id.toString()).orElseThrow(() -> new AgregadoInexistenteExcecao(
				DisciplinaServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id)));
	}

	@Override public boolean existePorIdentificador(DisciplinaIdentificador id) {
		Objects.requireNonNull(id);
		return parseLong(id.toString()).isPresent() && super.existePorIdentificador(id);
	}

	@Override public boolean existePorCodigo(br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo codigo,
			InstituicaoIdentificador instituicao, Optional<DisciplinaIdentificador> ignorado) {
		Objects.requireNonNull(codigo); Objects.requireNonNull(instituicao); Objects.requireNonNull(ignorado);
		return repositorioJpa.existePorCodigo(codigo.toString(), instituicao.toString(), ignorado.flatMap(id -> parseLong(id.toString())).orElse(null));
	}

	@Override public boolean existePorNomeNoCurso(String nome, CursoIdentificador curso,
			Optional<DisciplinaIdentificador> ignorado) {
		Objects.requireNonNull(nome); Objects.requireNonNull(curso); Objects.requireNonNull(ignorado);
		var cursoId = parseLong(curso.toString());
		return cursoId.isPresent() && repositorioJpa.existePorNomeNoCurso(nome, cursoId.get(), ignorado.flatMap(id -> parseLong(id.toString())).orElse(null));
	}

	@Override public boolean possuiVinculosMonitoria(DisciplinaIdentificador id) {
		Objects.requireNonNull(id);
		return parseLong(id.toString()).map(idJpa -> repositorioJpa.possuiVinculosEmSessoes(idJpa)
				|| repositorioJpa.possuiVinculosEmDisponibilidades(idJpa)).orElse(false);
	}

	private static Optional<Long> parseLong(String valor) {
		try { return Optional.of(Long.valueOf(valor)); }
		catch (NumberFormatException excecao) { return Optional.empty(); }
	}
}
