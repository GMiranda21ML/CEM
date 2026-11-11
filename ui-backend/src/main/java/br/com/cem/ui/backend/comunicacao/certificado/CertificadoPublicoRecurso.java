package br.com.cem.ui.backend.comunicacao.certificado;

import java.util.Objects;

import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;

public final class CertificadoPublicoRecurso {
	private final CodigoVerificacao codigo;
	private final String beneficiarioNome;
	private final CertificadoTipo tipo;
	private final String disciplinaNome;
	private final Periodo periodo;
	private final int quantidadeSessoes;
	private final long cargaHorariaMinutos;
	private final String emissor;

	public CertificadoPublicoRecurso(Certificado certificado) {
		Objects.requireNonNull(certificado);
		this.codigo = certificado.getCodigo();
		this.beneficiarioNome = certificado.getBeneficiarioNome();
		this.tipo = certificado.getTipo();
		this.disciplinaNome = certificado.getDisciplinaNome();
		this.periodo = certificado.getPeriodo();
		this.quantidadeSessoes = certificado.getQuantidadeSessoes();
		this.cargaHorariaMinutos = certificado.getCargaHorariaMinutos();
		this.emissor = "Emitido pela coordenação do CEM";
	}

	public CodigoVerificacao getCodigo() {
		return codigo;
	}

	public String getBeneficiarioNome() {
		return beneficiarioNome;
	}

	public CertificadoTipo getTipo() {
		return tipo;
	}

	public String getDisciplinaNome() {
		return disciplinaNome;
	}

	public Periodo getPeriodo() {
		return periodo;
	}

	public int getQuantidadeSessoes() {
		return quantidadeSessoes;
	}

	public long getCargaHorariaMinutos() {
		return cargaHorariaMinutos;
	}

	public String getEmissor() {
		return emissor;
	}
}
