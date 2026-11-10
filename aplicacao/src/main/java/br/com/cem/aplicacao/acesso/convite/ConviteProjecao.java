package br.com.cem.aplicacao.acesso.convite;

import java.time.LocalDateTime;
import java.util.Objects;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteEmail;
import br.com.cem.dominio.acesso.convite.ConviteIdentificador;
import br.com.cem.dominio.acesso.convite.ConviteSituacao;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class ConviteProjecao implements IProjecao<Convite> {
	private final ConviteIdentificador identificador;
	private final ConviteEmail email;
	private final Autorizacao papel;
	private final String curso;
	private final LocalDateTime enviadoEm;
	private final LocalDateTime validoAte;
	private final ConviteSituacao situacao;

	public ConviteProjecao(ConviteIdentificador identificador, ConviteEmail email, Autorizacao papel, String curso,
			LocalDateTime enviadoEm, LocalDateTime validoAte, ConviteSituacao situacao) {
		this.identificador = Objects.requireNonNull(identificador);
		this.email = Objects.requireNonNull(email);
		this.papel = Objects.requireNonNull(papel);
		this.curso = Objects.requireNonNull(curso);
		this.enviadoEm = Objects.requireNonNull(enviadoEm);
		this.validoAte = Objects.requireNonNull(validoAte);
		this.situacao = Objects.requireNonNull(situacao);
	}

	public ConviteProjecao(Long identificador, String email, Autorizacao papel, String curso,
			LocalDateTime enviadoEm, LocalDateTime validoAte, ConviteSituacao situacao) {
		this(new ConviteIdentificador(identificador.toString()), new ConviteEmail(email), papel, curso, enviadoEm,
				validoAte, situacao);
	}

	public ConviteIdentificador getIdentificador() {
		return identificador;
	}

	public ConviteEmail getEmail() {
		return email;
	}

	public Autorizacao getPapel() {
		return papel;
	}

	public String getCurso() {
		return curso;
	}

	public LocalDateTime getEnviadoEm() {
		return enviadoEm;
	}

	public LocalDateTime getValidoAte() {
		return validoAte;
	}

	public ConviteSituacao getSituacao() {
		return situacao;
	}
}
