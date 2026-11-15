package br.com.cem.dominio.comunicacao.aviso;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class Aviso implements IAgregado<AvisoIdentificador> {
	private final Optional<AvisoIdentificador> identificador;
	public static final int TITULO_TAMANHO = 100;
	private final String titulo;
	public static final int TEXTO_TAMANHO = 1000;
	private final String texto;
	private final LocalDate validade;
	private final boolean ativo;
	private final LocalDateTime publicadoEm;

	public Aviso(
			Optional<AvisoIdentificador> identificador,
			String titulo,
			String texto,
			LocalDate validade,
			boolean ativo,
			LocalDateTime publicadoEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.titulo = Validate.notBlank(titulo, "Título obrigatório").strip();
		Validate.isTrue(this.titulo.length() <= 100, "Título deve ter até 100 caracteres");
		this.texto = Validate.notBlank(texto, "Texto obrigatório").strip();
		Validate.isTrue(this.texto.length() <= 1000, "Texto deve ter até 1000 caracteres");
		this.validade = Objects.requireNonNull(validade);
		this.ativo = ativo;
		this.publicadoEm = Objects.requireNonNull(publicadoEm);
	}

	@Override
	public Optional<AvisoIdentificador> getIdentificador() {
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

	public void validarValidade(LocalDate hoje) {
		Validate.isTrue(
				!validade.isBefore(Objects.requireNonNull(hoje)),
				"A validade não pode ser anterior a hoje");
	}

	public boolean visivel(LocalDate hoje) {
		return ativo && !validade.isBefore(Objects.requireNonNull(hoje));
	}

	public Aviso alternar() {
		return new Aviso(identificador, titulo, texto, validade, !ativo, publicadoEm);
	}
}
