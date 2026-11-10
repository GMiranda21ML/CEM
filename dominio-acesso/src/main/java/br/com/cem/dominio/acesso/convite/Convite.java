package br.com.cem.dominio.acesso.convite;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.Autorizacao;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class Convite implements IAgregado<ConviteIdentificador> {
	public static final int CURSO_TAMANHO = 150;
	public static final int VALIDADE_DIAS = 7;

	private final Optional<ConviteIdentificador> identificador;
	private final ConviteEmail emailInstitucional;
	private final Autorizacao papel;
	private final String curso;
	private final ConviteCodigo codigo;
	private final LocalDateTime enviadoEm;
	private final LocalDateTime validoAte;
	private final ConviteSituacao situacao;

	public Convite(Optional<ConviteIdentificador> identificador, ConviteEmail emailInstitucional, Autorizacao papel,
			String curso, ConviteCodigo codigo, LocalDateTime enviadoEm, LocalDateTime validoAte,
			ConviteSituacao situacao) {
		this.identificador = Objects.requireNonNull(identificador);
		this.emailInstitucional = Objects.requireNonNull(emailInstitucional);
		this.papel = Objects.requireNonNull(papel);

		this.curso = Validate.notBlank(curso, "Curso do convite em branco");
		Validate.isTrue(curso.length() <= CURSO_TAMANHO, "Curso maior que %d caracteres", CURSO_TAMANHO);

		this.codigo = Objects.requireNonNull(codigo);
		this.enviadoEm = Objects.requireNonNull(enviadoEm);
		this.validoAte = Objects.requireNonNull(validoAte);
		this.situacao = Objects.requireNonNull(situacao);
	}

	@Override
	public Optional<ConviteIdentificador> getIdentificador() {
		return identificador;
	}

	public ConviteEmail getEmailInstitucional() {
		return emailInstitucional;
	}

	public Autorizacao getPapel() {
		return papel;
	}

	public String getCurso() {
		return curso;
	}

	public ConviteCodigo getCodigo() {
		return codigo;
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

	public boolean estaAguardandoAceite() {
		return situacao == ConviteSituacao.AGUARDANDO_ACEITE;
	}

	public boolean estaExpirado(LocalDateTime agora) {
		Objects.requireNonNull(agora);
		return agora.isAfter(validoAte);
	}

	public Convite reenviar(ConviteCodigo novoCodigo, LocalDateTime enviadoEm) {
		Validate.validState(estaAguardandoAceite(), "Só é possível reenviar um convite aguardando aceite");
		Objects.requireNonNull(novoCodigo);
		Objects.requireNonNull(enviadoEm);

		return new Convite(identificador, emailInstitucional, papel, curso, novoCodigo, enviadoEm,
				enviadoEm.plusDays(VALIDADE_DIAS), ConviteSituacao.AGUARDANDO_ACEITE);
	}

	public Convite cancelar() {
		Validate.validState(estaAguardandoAceite(), "Só é possível cancelar um convite aguardando aceite");

		return new Convite(identificador, emailInstitucional, papel, curso, codigo, enviadoEm, validoAte,
				ConviteSituacao.CANCELADO);
	}

	public Convite aceitar() {
		Validate.validState(estaAguardandoAceite(), "Só é possível aceitar um convite aguardando aceite");

		return new Convite(identificador, emailInstitucional, papel, curso, codigo, enviadoEm, validoAte,
				ConviteSituacao.ACEITO);
	}
}
