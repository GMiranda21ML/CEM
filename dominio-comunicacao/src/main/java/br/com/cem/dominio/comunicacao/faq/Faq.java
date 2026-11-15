package br.com.cem.dominio.comunicacao.faq;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.Optional;

public final class Faq implements IAgregado<FaqIdentificador> {
	private final Optional<FaqIdentificador> identificador;
	public static final int PERGUNTA_TAMANHO = 200;
	private final String pergunta;
	public static final int RESPOSTA_TAMANHO = 1000;
	private final String resposta;
	private final boolean ativo;

	public Faq(
			Optional<FaqIdentificador> identificador,
			String pergunta,
			String resposta,
			boolean ativo) {
		this.identificador = Objects.requireNonNull(identificador);
		this.pergunta = Validate.notBlank(pergunta, "Pergunta obrigatória").strip();
		Validate.isTrue(this.pergunta.length() <= 200, "Pergunta deve ter até 200 caracteres");
		this.resposta = Validate.notBlank(resposta, "Resposta obrigatória").strip();
		Validate.isTrue(this.resposta.length() <= 1000, "Resposta deve ter até 1000 caracteres");
		this.ativo = ativo;
	}

	@Override
	public Optional<FaqIdentificador> getIdentificador() {
		return identificador;
	}

	public String getPergunta() {
		return pergunta;
	}

	public String getResposta() {
		return resposta;
	}

	public boolean getAtivo() {
		return ativo;
	}
}
