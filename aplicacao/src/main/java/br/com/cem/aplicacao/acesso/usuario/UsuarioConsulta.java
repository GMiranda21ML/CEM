package br.com.cem.aplicacao.acesso.usuario;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class UsuarioConsulta extends Consulta<UsuarioOrdenacao> {
	private final Optional<String> nome;
	private final Optional<Autorizacao> papel;
	private final Optional<UsuarioSituacao> situacao;

	public UsuarioConsulta(Optional<UsuarioOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina, Optional<String> nome,
			Optional<Autorizacao> papel, Optional<UsuarioSituacao> situacao) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);

		this.nome = Objects.requireNonNull(nome);
		this.papel = Objects.requireNonNull(papel);
		this.situacao = Objects.requireNonNull(situacao);
	}

	public Optional<String> getNome() {
		return nome;
	}

	public Optional<Autorizacao> getPapel() {
		return papel;
	}

	public Optional<UsuarioSituacao> getSituacao() {
		return situacao;
	}
}
