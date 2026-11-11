package br.com.cem.dominio.comunicacao.certificado;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

public record EvidenciaCertificacao(String beneficiarioNome, String disciplinaNome, int quantidadeSessoes,
		long cargaHorariaMinutos) {
	public EvidenciaCertificacao {
		Validate.notBlank(beneficiarioNome, "Nome do beneficiário em branco");
		Validate.notBlank(disciplinaNome, "Nome da disciplina em branco");
		Validate.isTrue(quantidadeSessoes >= 0, "Quantidade de sessões negativa");
		Validate.isTrue(cargaHorariaMinutos >= 0, "Carga horária negativa");
		Objects.requireNonNull(beneficiarioNome);
		Objects.requireNonNull(disciplinaNome);
	}
}
