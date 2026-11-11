package br.com.cem.infraestrutura.comunicacao.certificado;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CER_CERTIFICADO", uniqueConstraints = {
		@UniqueConstraint(name = "UK_CER_CODIGO", columnNames = "CER_CODIGO"),
		@UniqueConstraint(name = "UK_CER_IDENTIDADE", columnNames = { "USR_ID", "CER_TIPO", "CER_DISCIPLINA_ID",
				"CER_PERIODO_INICIO", "CER_PERIODO_FIM" }) })
public class CertificadoJpa implements IAgregadoJpa<Certificado> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CER_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "CER_CODIGO", updatable = false)
	@NotNull
	@Size(max = CodigoVerificacao.TAMANHO)
	private String codigo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USR_ID", updatable = false)
	@NotNull
	private UsuarioJpa beneficiario;

	@Column(name = "CER_BENEFICIARIO_NOME", updatable = false)
	@NotNull
	@Size(max = Certificado.NOME_TAMANHO)
	private String beneficiarioNome;

	@Column(name = "CER_TIPO", updatable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private CertificadoTipo tipo;

	@Column(name = "CER_DISCIPLINA_ID", updatable = false)
	@NotNull
	@Size(max = DisciplinaIdentificador.TAMANHO)
	private String disciplina;

	@Column(name = "CER_DISCIPLINA_NOME", updatable = false)
	@NotNull
	@Size(max = Certificado.NOME_TAMANHO)
	private String disciplinaNome;

	@Column(name = "CER_PERIODO_INICIO", updatable = false)
	@NotNull
	private LocalDate periodoInicio;

	@Column(name = "CER_PERIODO_FIM", updatable = false)
	@NotNull
	private LocalDate periodoFim;

	@Column(name = "CER_QUANTIDADE_SESSOES", updatable = false)
	@Min(0)
	private int quantidadeSessoes;

	@Column(name = "CER_CARGA_HORARIA_MINUTOS", updatable = false)
	@Min(0)
	private long cargaHorariaMinutos;

	@Column(name = "CER_COORDENADOR", updatable = false)
	@NotNull
	@Size(max = Certificado.COORDENADOR_TAMANHO)
	private String coordenador;

	@Column(name = "CER_EMITIDO_EM", updatable = false)
	@NotNull
	private LocalDateTime emitidoEm;

	protected CertificadoJpa() {
	}

	public CertificadoJpa(@Nullable Long identificador, String codigo, UsuarioJpa beneficiario,
			String beneficiarioNome, CertificadoTipo tipo, String disciplina, String disciplinaNome,
			LocalDate periodoInicio, LocalDate periodoFim, int quantidadeSessoes, long cargaHorariaMinutos,
			String coordenador, LocalDateTime emitidoEm) {
		this.identificador = identificador;
		this.codigo = codigo;
		this.beneficiario = beneficiario;
		this.beneficiarioNome = beneficiarioNome;
		this.tipo = tipo;
		this.disciplina = disciplina;
		this.disciplinaNome = disciplinaNome;
		this.periodoInicio = periodoInicio;
		this.periodoFim = periodoFim;
		this.quantidadeSessoes = quantidadeSessoes;
		this.cargaHorariaMinutos = cargaHorariaMinutos;
		this.coordenador = coordenador;
		this.emitidoEm = emitidoEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getCodigo() {
		return codigo;
	}

	public UsuarioJpa getBeneficiario() {
		return beneficiario;
	}

	public String getBeneficiarioNome() {
		return beneficiarioNome;
	}

	public CertificadoTipo getTipo() {
		return tipo;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public String getDisciplinaNome() {
		return disciplinaNome;
	}

	public LocalDate getPeriodoInicio() {
		return periodoInicio;
	}

	public LocalDate getPeriodoFim() {
		return periodoFim;
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
