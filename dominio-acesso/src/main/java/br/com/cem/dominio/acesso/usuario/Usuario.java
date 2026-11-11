package br.com.cem.dominio.acesso.usuario;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.Autorizacao;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class Usuario implements IAgregado<UsuarioIdentificador> {
	public static final int NOME_TAMANHO = 150;
	public static final int CURSO_TAMANHO = 150;
	public static final int CAMPUS_TAMANHO = 150;

	private final Optional<UsuarioIdentificador> identificador;
	private final String nome;
	private final UsuarioEmail emailInstitucional;
	private final UsuarioSenha senha;
	private final Autorizacao papel;
	private final @Nullable String curso;
	private final @Nullable String campus;
	private final UsuarioSituacao situacao;

	public Usuario(Optional<UsuarioIdentificador> identificador, String nome, UsuarioEmail emailInstitucional,
			UsuarioSenha senha, Autorizacao papel, @Nullable String curso, @Nullable String campus,
			UsuarioSituacao situacao) {
		this.identificador = Objects.requireNonNull(identificador);

		this.nome = Validate.notBlank(nome, "Nome do usuário em branco");
		Validate.isTrue(nome.length() <= NOME_TAMANHO, "Nome do usuário maior que %d caracteres", NOME_TAMANHO);

		this.emailInstitucional = Objects.requireNonNull(emailInstitucional);
		this.senha = Objects.requireNonNull(senha);
		this.papel = Objects.requireNonNull(papel);

		if (curso != null) {
			Validate.isTrue(curso.length() <= CURSO_TAMANHO, "Curso maior que %d caracteres", CURSO_TAMANHO);
		}
		this.curso = curso;

		if (campus != null) {
			Validate.isTrue(campus.length() <= CAMPUS_TAMANHO, "Campus maior que %d caracteres", CAMPUS_TAMANHO);
		}
		this.campus = campus;

		this.situacao = Objects.requireNonNull(situacao);
	}

	@Override
	public Optional<UsuarioIdentificador> getIdentificador() {
		return identificador;
	}

	public String getNome() {
		return nome;
	}

	public UsuarioEmail getEmailInstitucional() {
		return emailInstitucional;
	}

	public UsuarioSenha getSenha() {
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

	public boolean estaAtivo() {
		return situacao == UsuarioSituacao.ATIVO;
	}

	public Usuario alterarDados(String nome, @Nullable String curso, @Nullable String campus) {
		return new Usuario(identificador, nome, emailInstitucional, senha, papel, curso, campus, situacao);
	}

	public Usuario inativar() {
		Validate.validState(estaAtivo(), "O usuário já está inativo");
		return new Usuario(identificador, nome, emailInstitucional, senha, papel, curso, campus,
				UsuarioSituacao.INATIVO);
	}

	public Usuario reativar() {
		Validate.validState(!estaAtivo(), "O usuário já está ativo");
		return new Usuario(identificador, nome, emailInstitucional, senha, papel, curso, campus,
				UsuarioSituacao.ATIVO);
	}
}
