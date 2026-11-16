package br.com.cem.infraestrutura.acesso.disciplina;

import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "DSC_DISCIPLINA", uniqueConstraints = {
		@UniqueConstraint(name = "UK_DSC_INSTITUICAO_CODIGO", columnNames = { "DSC_INSTITUICAO", "DSC_CODIGO" }),
		@UniqueConstraint(name = "UK_DSC_CURSO_NOME", columnNames = { "CRS_ID", "DSC_NOME" }) })
public class DisciplinaJpa implements IAgregadoJpa<Disciplina> {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DSC_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "DSC_INSTITUICAO", updatable = false)
	@NotNull @Size(max = InstituicaoIdentificador.TAMANHO)
	private String instituicao;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "CRS_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_DSC_CURSO"))
	private CursoJpa curso;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "USR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_DSC_PROFESSOR"))
	private UsuarioJpa professor;

	@Column(name = "DSC_NOME")
	@NotNull @Size(max = Disciplina.NOME_TAMANHO)
	private String nome;

	@Column(name = "DSC_CODIGO")
	@NotNull @Size(max = DisciplinaCodigo.TAMANHO)
	private String codigo;

	@Column(name = "DSC_AREA")
	@NotNull @Size(max = Disciplina.AREA_TAMANHO)
	private String area;

	protected DisciplinaJpa() {}

	public DisciplinaJpa(@Nullable Long identificador, String instituicao, CursoJpa curso, UsuarioJpa professor,
			String nome, String codigo, String area) {
		this.identificador = identificador;
		this.instituicao = instituicao;
		this.curso = curso;
		this.professor = professor;
		this.nome = nome;
		this.codigo = codigo;
		this.area = area;
	}

	public @Nullable Long getIdentificador() { return identificador; }
	public String getInstituicao() { return instituicao; }
	public CursoJpa getCurso() { return curso; }
	public UsuarioJpa getProfessor() { return professor; }
	public String getNome() { return nome; }
	public String getCodigo() { return codigo; }
	public String getArea() { return area; }
}
