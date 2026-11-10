package br.com.cem.infraestrutura.acesso.usuario;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "USR_USUARIO")
public class UsuarioJpa implements IAgregadoJpa<Usuario> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USR_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "USR_NOME")
	@NotNull
	@Size(max = Usuario.NOME_TAMANHO)
	private String nome;

	@Column(name = "USR_EMAIL", unique = true)
	@NotNull
	@Size(max = UsuarioEmail.TAMANHO)
	private String email;

	@Column(name = "USR_SENHA")
	@NotNull
	@Size(max = UsuarioSenha.TAMANHO)
	private String senha;

	@Column(name = "USR_PAPEL")
	@Enumerated(EnumType.STRING)
	@NotNull
	private Autorizacao papel;

	@Column(name = "USR_CURSO")
	@Size(max = Usuario.CURSO_TAMANHO)
	private @Nullable String curso;

	@Column(name = "USR_CAMPUS")
	@Size(max = Usuario.CAMPUS_TAMANHO)
	private @Nullable String campus;

	@Column(name = "USR_SITUACAO")
	@Enumerated(EnumType.STRING)
	@NotNull
	private UsuarioSituacao situacao;

	protected UsuarioJpa() {
	}

	public UsuarioJpa(@Nullable Long identificador, String nome, String email, String senha, Autorizacao papel,
			@Nullable String curso, @Nullable String campus, UsuarioSituacao situacao) {
		this.identificador = identificador;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.papel = papel;
		this.curso = curso;
		this.campus = campus;
		this.situacao = situacao;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getSenha() {
		return senha;
	}

	public Autorizacao getPapel() {
		return papel;
	}

	public @Nullable String getCurso() {
		return curso;
	}

	public @Nullable String getCampus() {
		return campus;
	}

	public UsuarioSituacao getSituacao() {
		return situacao;
	}
}
