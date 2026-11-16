package br.com.cem.dominio.acesso.disciplina;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class Disciplina implements IAgregado<DisciplinaIdentificador> {
	public static final int NOME_TAMANHO = 150;
	public static final int AREA_TAMANHO = 100;

	private final Optional<DisciplinaIdentificador> identificador;
	private final InstituicaoIdentificador instituicao;
	private final CursoIdentificador curso;
	private final UsuarioIdentificador professor;
	private final String nome;
	private final DisciplinaCodigo codigo;
	private final String area;

	public Disciplina(Optional<DisciplinaIdentificador> identificador, InstituicaoIdentificador instituicao,
			CursoIdentificador curso, UsuarioIdentificador professor, String nome, DisciplinaCodigo codigo,
			String area) {
		this.identificador = Objects.requireNonNull(identificador, "Identificador não pode ser nulo");
		this.instituicao = Objects.requireNonNull(instituicao, "Instituição não pode ser nula");
		this.curso = Objects.requireNonNull(curso, "Curso não pode ser nulo");
		this.professor = Objects.requireNonNull(professor, "Professor não pode ser nulo");
		this.nome = Validate.notBlank(nome, "Nome da disciplina em branco");
		Validate.isTrue(nome.length() <= NOME_TAMANHO, "Nome da disciplina maior que %d caracteres", NOME_TAMANHO);
		this.codigo = Objects.requireNonNull(codigo, "Código não pode ser nulo");
		this.area = Validate.notBlank(area, "Área da disciplina em branco");
		Validate.isTrue(area.length() <= AREA_TAMANHO, "Área da disciplina maior que %d caracteres", AREA_TAMANHO);
	}

	@Override
	public Optional<DisciplinaIdentificador> getIdentificador() {
		return identificador;
	}

	public InstituicaoIdentificador getInstituicao() {
		return instituicao;
	}

	public CursoIdentificador getCurso() {
		return curso;
	}

	public UsuarioIdentificador getProfessor() {
		return professor;
	}

	public String getNome() {
		return nome;
	}

	public DisciplinaCodigo getCodigo() {
		return codigo;
	}

	public String getArea() {
		return area;
	}

	public Disciplina alterarDados(String novoNome, String novaArea, UsuarioIdentificador novoProfessor) {
		return new Disciplina(identificador, instituicao, curso, novoProfessor, novoNome, codigo, novaArea);
	}
}
