package br.com.cem.dominio.monitoria.sessao;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class RespostaSessao {
	public static final int MOTIVO_TAMANHO = 1000;

	private final UsuarioIdentificador aluno;
	private final SituacaoRespostaSessao situacao;
	private final Optional<String> motivo;
	private final Optional<LocalDateTime> inicioSugerido;

	public RespostaSessao(
			UsuarioIdentificador aluno,
			SituacaoRespostaSessao situacao,
			Optional<String> motivo,
			Optional<LocalDateTime> inicioSugerido) {
		this.aluno = Objects.requireNonNull(aluno);
		this.situacao = Objects.requireNonNull(situacao);
		this.motivo = Objects.requireNonNull(motivo).map(String::strip);
		this.inicioSugerido = Objects.requireNonNull(inicioSugerido);
		this.motivo.ifPresent(
				valor -> Validate.isTrue(valor.length() <= MOTIVO_TAMANHO, "Motivo deve ter até 1000 caracteres"));
		Validate.isTrue(
				situacao == SituacaoRespostaSessao.NAO_VAI_PARTICIPAR
						|| this.motivo.isEmpty() && this.inicioSugerido.isEmpty(),
				"Motivo e sugestão só podem acompanhar uma resposta negativa");
	}

	public static RespostaSessao semResposta(UsuarioIdentificador aluno) {
		return new RespostaSessao(
				aluno,
				SituacaoRespostaSessao.SEM_RESPOSTA,
				Optional.empty(),
				Optional.empty());
	}

	public UsuarioIdentificador getAluno() {
		return aluno;
	}

	public SituacaoRespostaSessao getSituacao() {
		return situacao;
	}

	public Optional<String> getMotivo() {
		return motivo;
	}

	public Optional<LocalDateTime> getInicioSugerido() {
		return inicioSugerido;
	}
}
