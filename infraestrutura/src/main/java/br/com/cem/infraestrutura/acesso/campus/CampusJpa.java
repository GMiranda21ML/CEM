package br.com.cem.infraestrutura.acesso.campus;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusSituacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CAM_CAMPUS", uniqueConstraints = @UniqueConstraint(name = "UK_CAM_INSTITUICAO_NOME", columnNames = {
		"CAM_INSTITUICAO", "CAM_NOME" }))
public class CampusJpa implements IAgregadoJpa<Campus> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CAM_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "CAM_INSTITUICAO", updatable = false)
	@NotNull
	@Size(max = InstituicaoIdentificador.TAMANHO)
	private String instituicao;

	@Column(name = "CAM_NOME")
	@NotNull
	@Size(max = Campus.NOME_TAMANHO)
	private String nome;

	@Column(name = "CAM_ENDERECO")
	@Size(max = Campus.ENDERECO_TAMANHO)
	private @Nullable String endereco;

	@Column(name = "CAM_SITUACAO")
	@Enumerated(EnumType.STRING)
	@NotNull
	private CampusSituacao situacao;

	protected CampusJpa() {
	}

	public CampusJpa(@Nullable Long identificador, String instituicao, String nome, @Nullable String endereco,
			CampusSituacao situacao) {
		this.identificador = identificador;
		this.instituicao = instituicao;
		this.nome = nome;
		this.endereco = endereco;
		this.situacao = situacao;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public String getNome() {
		return nome;
	}

	public @Nullable String getEndereco() {
		return endereco;
	}

	public CampusSituacao getSituacao() {
		return situacao;
	}
}
