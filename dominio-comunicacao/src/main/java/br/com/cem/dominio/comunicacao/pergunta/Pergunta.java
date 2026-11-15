package br.com.cem.dominio.comunicacao.pergunta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Pergunta implements IAgregado<PerguntaIdentificador> {
	private final Optional<PerguntaIdentificador> identificador;
	private final UsuarioIdentificador autor;
	public static final int TEXTO_TAMANHO = 300;
	private final String texto;
	public static final int SITUACAO_TAMANHO = 20;
	private final String situacao;
	public static final int RESPOSTA_TAMANHO = 1000;
	private final Optional<String> resposta;
	private final LocalDateTime criadaEm;

	public Pergunta(
			Optional<PerguntaIdentificador> identificador,
			UsuarioIdentificador autor,
			String texto,
			String situacao,
			Optional<String> resposta,
			LocalDateTime criadaEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.autor = Objects.requireNonNull(autor);
		this.texto = Validate.notBlank(texto, "Pergunta obrigatória").strip();
		Validate.inclusiveBetween(
				10, 300, this.texto.length(), "Pergunta deve ter entre 10 e 300 caracteres");
		Validate.isTrue(
				Set.of("PENDENTE", "RESPONDIDA", "DESCARTADA").contains(situacao),
				"Situação inválida");
		this.situacao = situacao;
		this.resposta =
				Objects.requireNonNull(resposta)
						.map(String::strip)
						.filter(textoResposta -> !textoResposta.isEmpty());
		this.resposta.ifPresent(
				textoResposta ->
						Validate.isTrue(
								textoResposta.length() <= RESPOSTA_TAMANHO,
								"Resposta deve ter até %d caracteres",
								RESPOSTA_TAMANHO));
		this.criadaEm = Objects.requireNonNull(criadaEm);
	}

	@Override
	public Optional<PerguntaIdentificador> getIdentificador() {
		return identificador;
	}

	public UsuarioIdentificador getAutor() {
		return autor;
	}

	public String getTexto() {
		return texto;
	}

	public String getSituacao() {
		return situacao;
	}

	public Optional<String> getResposta() {
		return resposta;
	}

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}

	public Pergunta editar(String texto) {
		var editada = new Pergunta(identificador, autor, texto, situacao, resposta, criadaEm);
		Validate.validState(pendente(), "Somente perguntas pendentes podem ser editadas");
		return editada;
	}

	public boolean pendente() {
		return situacao.equals("PENDENTE");
	}

	public Pergunta responder(String resposta) {
		Validate.notBlank(resposta, "Resposta obrigatória");
		var respondida =
				new Pergunta(
						identificador, autor, texto, "RESPONDIDA", Optional.of(resposta), criadaEm);
		Validate.validState(pendente(), "Pergunta já tratada");
		return respondida;
	}

	public Pergunta descartar() {
		Validate.validState(pendente(), "Pergunta já tratada");
		return new Pergunta(identificador, autor, texto, "DESCARTADA", resposta, criadaEm);
	}
}
