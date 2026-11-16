package br.com.cem.infraestrutura.acesso.curso;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.Curso;
import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "CRS_CURSO", uniqueConstraints = {
		@UniqueConstraint(name = "UK_CRS_INSTITUICAO_NOME", columnNames = { "CRS_INSTITUICAO", "CRS_NOME" }),
		@UniqueConstraint(name = "UK_CRS_INSTITUICAO_SIGLA", columnNames = { "CRS_INSTITUICAO", "CRS_SIGLA" }) })
public class CursoJpa implements IAgregadoJpa<Curso> {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CRS_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "CRS_INSTITUICAO", updatable = false)
	@NotNull @Size(max = InstituicaoIdentificador.TAMANHO)
	private String instituicao;

	@Column(name = "CRS_NOME")
	@NotNull @Size(max = Curso.NOME_TAMANHO)
	private String nome;

	@Column(name = "CRS_SIGLA")
	@NotNull @Size(max = Curso.SIGLA_TAMANHO)
	private String sigla;

	protected CursoJpa() {}

	public CursoJpa(@Nullable Long identificador, String instituicao, String nome, String sigla) {
		this.identificador = identificador;
		this.instituicao = instituicao;
		this.nome = nome;
		this.sigla = sigla;
	}

	public @Nullable Long getIdentificador() { return identificador; }
	public String getInstituicao() { return instituicao; }
	public String getNome() { return nome; }
	public String getSigla() { return sigla; }
}
