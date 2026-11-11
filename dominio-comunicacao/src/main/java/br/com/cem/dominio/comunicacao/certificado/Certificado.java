package br.com.cem.dominio.comunicacao.certificado;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class Certificado implements IAgregado<CertificadoIdentificador> {
	public static final int NOME_TAMANHO = 150;
	public static final int COORDENADOR_TAMANHO = 180;

	private final Optional<CertificadoIdentificador> identificador;
	private final CodigoVerificacao codigo;
	private final UsuarioIdentificador beneficiario;
	private final String beneficiarioNome;
	private final CertificadoTipo tipo;
	private final DisciplinaIdentificador disciplina;
	private final String disciplinaNome;
	private final Periodo periodo;
	private final int quantidadeSessoes;
	private final long cargaHorariaMinutos;
	private final String coordenador;
	private final LocalDateTime emitidoEm;

	public Certificado(Optional<CertificadoIdentificador> identificador, CodigoVerificacao codigo,
			UsuarioIdentificador beneficiario, String beneficiarioNome, CertificadoTipo tipo,
			DisciplinaIdentificador disciplina, String disciplinaNome, Periodo periodo, int quantidadeSessoes,
			long cargaHorariaMinutos, String coordenador, LocalDateTime emitidoEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.codigo = Objects.requireNonNull(codigo);
		this.beneficiario = Objects.requireNonNull(beneficiario);
		this.beneficiarioNome = Validate.notBlank(beneficiarioNome, "Nome do beneficiário em branco");
		Validate.isTrue(beneficiarioNome.length() <= NOME_TAMANHO,
				"Nome do beneficiário maior que %d caracteres", NOME_TAMANHO);
		this.tipo = Objects.requireNonNull(tipo);
		this.disciplina = Objects.requireNonNull(disciplina);
		this.disciplinaNome = Validate.notBlank(disciplinaNome, "Nome da disciplina em branco");
		Validate.isTrue(disciplinaNome.length() <= NOME_TAMANHO, "Nome da disciplina maior que %d caracteres",
				NOME_TAMANHO);
		this.periodo = Objects.requireNonNull(periodo);
		Validate.isTrue(quantidadeSessoes >= 0, "Quantidade de sessões negativa");
		this.quantidadeSessoes = quantidadeSessoes;
		Validate.isTrue(cargaHorariaMinutos >= 0, "Carga horária negativa");
		this.cargaHorariaMinutos = cargaHorariaMinutos;
		this.coordenador = Validate.notBlank(coordenador, "Coordenador emissor em branco");
		Validate.isTrue(coordenador.length() <= COORDENADOR_TAMANHO,
				"Identificação do coordenador maior que %d caracteres", COORDENADOR_TAMANHO);
		this.emitidoEm = Objects.requireNonNull(emitidoEm);
	}

	@Override
	public Optional<CertificadoIdentificador> getIdentificador() {
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

	public String getCoordenador() {
		return coordenador;
	}

	public LocalDateTime getEmitidoEm() {
		return emitidoEm;
	}
}
