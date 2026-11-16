package br.com.cem.aplicacao.acesso.disciplina;

import java.util.Objects;

import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class DisciplinaProjecao implements IProjecao<Disciplina> {
	private final DisciplinaIdentificador identificador;
	private final CursoIdentificador curso;
	private final String cursoNome;
	private final UsuarioIdentificador professor;
	private final String professorNome;
	private final String nome;
	private final DisciplinaCodigo codigo;
	private final String area;
	private final long quantidadeVinculos;

	public DisciplinaProjecao(DisciplinaIdentificador identificador, CursoIdentificador curso, String cursoNome,
			UsuarioIdentificador professor, String professorNome, String nome, DisciplinaCodigo codigo, String area,
			long quantidadeVinculos) {
		this.identificador = Objects.requireNonNull(identificador);
		this.curso = Objects.requireNonNull(curso);
		this.cursoNome = Objects.requireNonNull(cursoNome);
		this.professor = Objects.requireNonNull(professor);
		this.professorNome = Objects.requireNonNull(professorNome);
		this.nome = Objects.requireNonNull(nome);
		this.codigo = Objects.requireNonNull(codigo);
		this.area = Objects.requireNonNull(area);
		this.quantidadeVinculos = quantidadeVinculos;
	}

	public DisciplinaProjecao(Long identificador, Long curso, String cursoNome, Long professor,
			String professorNome, String nome, String codigo, String area, Long quantidadeVinculos) {
		this(new DisciplinaIdentificador(identificador.toString()), new CursoIdentificador(curso.toString()), cursoNome,
				new UsuarioIdentificador(professor.toString()), professorNome, nome, new DisciplinaCodigo(codigo), area,
				quantidadeVinculos);
	}

	public DisciplinaIdentificador getIdentificador() { return identificador; }
	public CursoIdentificador getCurso() { return curso; }
	public String getCursoNome() { return cursoNome; }
	public UsuarioIdentificador getProfessor() { return professor; }
	public String getProfessorNome() { return professorNome; }
	public String getNome() { return nome; }
	public DisciplinaCodigo getCodigo() { return codigo; }
	public String getArea() { return area; }
	public long getQuantidadeVinculos() { return quantidadeVinculos; }
	public boolean isExclusaoPermitida() { return quantidadeVinculos == 0; }
}
