package br.com.cem.aplicacao.comunicacao.certificado;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class CertificadoConsulta extends Consulta<CertificadoOrdenacao> {
	private final Optional<CertificadoTipo> tipo;
	private final Optional<DisciplinaIdentificador> disciplina;

	public CertificadoConsulta(Optional<CertificadoOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina, Optional<CertificadoTipo> tipo,
			Optional<DisciplinaIdentificador> disciplina) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
		this.tipo = Objects.requireNonNull(tipo);
		this.disciplina = Objects.requireNonNull(disciplina);
	}

	public Optional<CertificadoTipo> getTipo() {
		return tipo;
	}

	public Optional<DisciplinaIdentificador> getDisciplina() {
		return disciplina;
	}
}
