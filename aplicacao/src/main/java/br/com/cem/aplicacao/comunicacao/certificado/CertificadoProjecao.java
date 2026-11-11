package br.com.cem.aplicacao.comunicacao.certificado;

import java.time.LocalDateTime;
import java.util.Objects;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class CertificadoProjecao implements IProjecao<Certificado> {
	private final CertificadoIdentificador identificador;
	private final CodigoVerificacao codigo;
	private final UsuarioIdentificador beneficiario;
	private final String beneficiarioNome;
	private final CertificadoTipo tipo;
	private final DisciplinaIdentificador disciplina;
	private final String disciplinaNome;
	private final Periodo periodo;
	private final int quantidadeSessoes;
	private final long cargaHorariaMinutos;
	private final LocalDateTime emitidoEm;

	public CertificadoProjecao(CertificadoIdentificador identificador, CodigoVerificacao codigo,
			UsuarioIdentificador beneficiario, String beneficiarioNome, CertificadoTipo tipo,
			DisciplinaIdentificador disciplina, String disciplinaNome, Periodo periodo, int quantidadeSessoes,
			long cargaHorariaMinutos, LocalDateTime emitidoEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.codigo = Objects.requireNonNull(codigo);
		this.beneficiario = Objects.requireNonNull(beneficiario);
		this.beneficiarioNome = Objects.requireNonNull(beneficiarioNome);
		this.tipo = Objects.requireNonNull(tipo);
		this.disciplina = Objects.requireNonNull(disciplina);
		this.disciplinaNome = Objects.requireNonNull(disciplinaNome);
		this.periodo = Objects.requireNonNull(periodo);
		this.quantidadeSessoes = quantidadeSessoes;
		this.cargaHorariaMinutos = cargaHorariaMinutos;
		this.emitidoEm = Objects.requireNonNull(emitidoEm);
	}

	public CertificadoProjecao(Long identificador, String codigo, Long beneficiario, String beneficiarioNome,
			CertificadoTipo tipo, String disciplina, String disciplinaNome, java.time.LocalDate periodoInicio,
			java.time.LocalDate periodoFim, int quantidadeSessoes, long cargaHorariaMinutos, LocalDateTime emitidoEm) {
		this(new CertificadoIdentificador(identificador.toString()), new CodigoVerificacao(codigo),
				new UsuarioIdentificador(beneficiario.toString()), beneficiarioNome, tipo,
				new DisciplinaIdentificador(disciplina), disciplinaNome, new Periodo(periodoInicio, periodoFim),
				quantidadeSessoes, cargaHorariaMinutos, emitidoEm);
	}

	public CertificadoIdentificador getIdentificador() {
		return identificador;
	}

	public CodigoVerificacao getCodigo() {
		return codigo;
	}

	public UsuarioIdentificador getBeneficiario() {
		return beneficiario;
	}

	public String getBeneficiarioNome() {
		return beneficiarioNome;
	}

	public CertificadoTipo getTipo() {
		return tipo;
	}

	public DisciplinaIdentificador getDisciplina() {
		return disciplina;
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

	public LocalDateTime getEmitidoEm() {
		return emitidoEm;
	}
}
