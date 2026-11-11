package br.com.cem.aplicacao.acesso.usuario;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class UsuarioProjecao implements IProjecao<Usuario> {
	private final UsuarioIdentificador identificador;
	private final String nome;
	private final UsuarioEmail email;
	private final Autorizacao papel;
	private final @Nullable String curso;
	private final @Nullable String campus;
	private final UsuarioSituacao situacao;

	public UsuarioProjecao(UsuarioIdentificador identificador, String nome, UsuarioEmail email, Autorizacao papel,
			@Nullable String curso, @Nullable String campus, UsuarioSituacao situacao) {
		this.identificador = Objects.requireNonNull(identificador);
		this.nome = Objects.requireNonNull(nome);
		this.email = Objects.requireNonNull(email);
		this.papel = Objects.requireNonNull(papel);
		this.curso = curso;
		this.campus = campus;
		this.situacao = Objects.requireNonNull(situacao);
	}

	public UsuarioProjecao(Long identificador, String nome, String email, Autorizacao papel, @Nullable String curso,
			@Nullable String campus, UsuarioSituacao situacao) {
		this(new UsuarioIdentificador(identificador.toString()), nome, new UsuarioEmail(email), papel, curso, campus,
				situacao);
	}

	public UsuarioIdentificador getIdentificador() {
		return identificador;
	}

	public String getNome() {
		return nome;
	}

	public UsuarioEmail getEmail() {
		return email;
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
