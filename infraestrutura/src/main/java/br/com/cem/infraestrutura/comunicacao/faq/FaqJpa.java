package br.com.cem.infraestrutura.comunicacao.faq;

import br.com.cem.dominio.comunicacao.faq.Faq;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.util.Locale;

@Entity
@Table(
		name = "FAQ_FAQ",
		uniqueConstraints = @UniqueConstraint(name = "UK_FAQ_PERGUNTA", columnNames = "FAQ_CHAVE"))
public class FaqJpa implements IAgregadoJpa<Faq> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FAQ_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "FAQ_PERGUNTA")
	@NotNull
	@Size(max = Faq.PERGUNTA_TAMANHO)
	private String pergunta;

	@Column(name = "FAQ_RESPOSTA")
	@NotNull
	@Size(max = Faq.RESPOSTA_TAMANHO)
	private String resposta;

	@Column(name = "FAQ_ATIVO")
	private boolean ativo;

	@NotNull
	@Size(max = 200)
	@Column(name = "FAQ_CHAVE")
	private String chave;

	protected FaqJpa() {}

	public FaqJpa(@Nullable Long identificador, String pergunta, String resposta, boolean ativo) {
		this.identificador = identificador;
		this.pergunta = pergunta;
		this.resposta = resposta;
		this.ativo = ativo;
		this.chave = pergunta.strip().toLowerCase(Locale.ROOT);
	}

	public @Nullable Long getIdentificador() {
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
