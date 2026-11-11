package br.com.cem.infraestrutura.comunicacao.certificado;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoServico;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;
import br.com.cem.dominio.comunicacao.certificado.spi.ICertificadoRepositorio;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class CertificadoRepositorio
		extends AgregadoRepositorioAbstrato<CertificadoIdentificador, Certificado, Long, CertificadoJpa>
		implements ICertificadoRepositorio {
	private final ICertificadoRepositorioJpa repositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public CertificadoRepositorio(ICertificadoRepositorioJpa repositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, CertificadoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected CertificadoJpa paraJpa(Certificado agregado) {
		Objects.requireNonNull(agregado);
		var identificador = agregado.getIdentificador().map(this::paraIdentificadorJpa).orElse(null);
		var beneficiario = usuarioRepositorioJpa.getReferenceById(converter(agregado.getBeneficiario()));
		return new CertificadoJpa(identificador, agregado.getCodigo().toString(), beneficiario,
				agregado.getBeneficiarioNome(), agregado.getTipo(), agregado.getDisciplina().toString(),
				agregado.getDisciplinaNome(), agregado.getPeriodo().getInicio(), agregado.getPeriodo().getFim(),
				agregado.getQuantidadeSessoes(), agregado.getCargaHorariaMinutos(), agregado.getCoordenador(),
				agregado.getEmitidoEm());
	}

	@Override
	protected Certificado paraAgregado(CertificadoJpa agregadoJpa) {
		Objects.requireNonNull(agregadoJpa);
		var identificador = new CertificadoIdentificador(
				Objects.requireNonNull(agregadoJpa.getIdentificador()).toString());
		var beneficiario = new UsuarioIdentificador(
				Objects.requireNonNull(agregadoJpa.getBeneficiario().getIdentificador()).toString());
		var periodo = new Periodo(agregadoJpa.getPeriodoInicio(), agregadoJpa.getPeriodoFim());
		return new Certificado(Optional.of(identificador), new CodigoVerificacao(agregadoJpa.getCodigo()), beneficiario,
				agregadoJpa.getBeneficiarioNome(), agregadoJpa.getTipo(),
				new DisciplinaIdentificador(agregadoJpa.getDisciplina()), agregadoJpa.getDisciplinaNome(), periodo,
				agregadoJpa.getQuantidadeSessoes(), agregadoJpa.getCargaHorariaMinutos(), agregadoJpa.getCoordenador(),
				agregadoJpa.getEmitidoEm());
	}

	@Override
	protected Long paraIdentificadorJpa(CertificadoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return converter(identificador.toString(), identificador);
	}

	@Override
	public boolean existePorIdentificador(CertificadoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return converterOpcional(identificador.toString()).isPresent() && super.existePorIdentificador(identificador);
	}

	@Override
	public boolean existePorCodigo(CodigoVerificacao codigo) {
		Objects.requireNonNull(codigo);
		return repositorioJpa.existsByCodigo(codigo.toString());
	}

	@Override
	public Certificado buscarPorCodigo(CodigoVerificacao codigo) {
		Objects.requireNonNull(codigo);
		return repositorioJpa.findByCodigo(codigo.toString()).map(this::paraAgregado)
				.orElseThrow(() -> new AgregadoInexistenteExcecao(CertificadoServico.CODIGO_NAO_ENCONTRADO));
	}

	@Override
	public boolean existePorIdentidade(UsuarioIdentificador beneficiario, CertificadoTipo tipo,
			DisciplinaIdentificador disciplina, Periodo periodo) {
		Objects.requireNonNull(beneficiario);
		Objects.requireNonNull(tipo);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(periodo);
		return repositorioJpa.existePorIdentidade(converter(beneficiario), tipo, disciplina.toString(),
				periodo.getInicio(), periodo.getFim());
	}

	private static Long converter(UsuarioIdentificador identificador) {
		return converter(identificador.toString(), identificador);
	}

	private static Long converter(String valor, Object identificador) {
		return converterOpcional(valor).orElseThrow(() -> new AgregadoInexistenteExcecao(
				CertificadoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador)));
	}

	private static Optional<Long> converterOpcional(String valor) {
		try {
			return Optional.of(Long.valueOf(valor));
		} catch (NumberFormatException excecao) {
			return Optional.empty();
		}
	}
}
