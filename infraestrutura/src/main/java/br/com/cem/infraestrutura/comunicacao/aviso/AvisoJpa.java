package br.com.cem.infraestrutura.comunicacao.aviso;

import br.com.cem.dominio.comunicacao.aviso.Aviso;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "AVS_AVISO")
public class AvisoJpa implements IAgregadoJpa<Aviso> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AVS_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "AVS_TITULO")
	@NotNull
	@Size(max = Aviso.TITULO_TAMANHO)
	private String titulo;

	@Column(name = "AVS_TEXTO")
	@NotNull
	@Size(max = Aviso.TEXTO_TAMANHO)
	private String texto;

	@Column(name = "AVS_VALIDADE")
	@NotNull
	private LocalDate validade;

	@Column(name = "AVS_ATIVO")
	private boolean ativo;

	@Column(name = "AVS_PUBLICADO_EM")
	@NotNull
	private LocalDateTime publicadoEm;

	protected AvisoJpa() {}

	public AvisoJpa(
			@Nullable Long identificador,
			String titulo,
			String texto,
			LocalDate validade,
			boolean ativo,
			LocalDateTime publicadoEm) {
		this.identificador = identificador;
		this.titulo = titulo;
		this.texto = texto;
		this.validade = validade;
		this.ativo = ativo;
		this.publicadoEm = publicadoEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getTexto() {
		return texto;
	}

	public LocalDate getValidade() {
		return validade;
	}

	public boolean getAtivo() {
		return ativo;
	}

	public LocalDateTime getPublicadoEm() {
		return publicadoEm;
	}
}
