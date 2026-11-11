package br.com.cem.dominio.comunicacao.certificado;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.comunicacao.certificado.spi.ICertificadoElegibilidadeServico;
import br.com.cem.dominio.comunicacao.certificado.spi.ICertificadoRepositorio;
import br.com.cem.dominio.comunicacao.certificado.spi.IGeradorCodigoVerificacao;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutenticadoExcecao;

public final class CertificadoServico
		extends AgregadoServicoAbstrato<CertificadoIdentificador, Certificado, CertificadoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO = "Não existe certificado com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO = "Já existe um certificado com o identificador %s";
	public static final String CERTIFICADO_DUPLICADO = "Já existe certificado para o beneficiário, tipo, disciplina e período informados";
	public static final String ALUNO_INELEGIVEL = "O aluno precisa ter pelo menos cinco presenças em sessões realizadas";
	public static final String MONITOR_INELEGIVEL = "O monitor precisa ter ministrado tempo efetivo em sessão realizada";
	public static final String CODIGO_NAO_ENCONTRADO = "Código não encontrado";
	private static final int TENTATIVAS_CODIGO = 10;

	private final ICertificadoRepositorio repositorio;
	private final ICertificadoElegibilidadeServico elegibilidadeServico;
	private final IGeradorCodigoVerificacao geradorCodigo;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogioServico;

	public CertificadoServico(ISegurancaServico segurancaServico, ICertificadoRepositorio repositorio,
			ICertificadoElegibilidadeServico elegibilidadeServico, IGeradorCodigoVerificacao geradorCodigo,
			IUsuarioRepositorio usuarioRepositorio, IRelogioServico relogioServico) {
		super(segurancaServico, repositorio, IDENTIFICADOR_EXISTENTE_MODELO, IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.elegibilidadeServico = Objects.requireNonNull(elegibilidadeServico);
		this.geradorCodigo = Objects.requireNonNull(geradorCodigo);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogioServico = Objects.requireNonNull(relogioServico);
	}

	@Override
	public CertificadoAutorizacoes criarAutorizacoes(Optional<CertificadoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		try {
			var coordenador = getSegurancaServico().possuiAutorizacao(Autorizacao.COORDENADOR.name());
			var proprio = identificador.isPresent() && repositorio.existePorIdentificador(identificador.get())
					&& repositorio.buscarPorIdentificador(identificador.get()).getBeneficiario().equals(usuarioAtual());
			return new CertificadoAutorizacoes(false, coordenador || proprio, false, false, coordenador);
		} catch (NaoAutenticadoExcecao excecao) {
			return new CertificadoAutorizacoes(false, false, false, false, false);
		}
	}

	public Certificado emitir(UsuarioIdentificador beneficiario, CertificadoTipo tipo,
			DisciplinaIdentificador disciplina, Periodo periodo) {
		Objects.requireNonNull(beneficiario);
		Objects.requireNonNull(tipo);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(periodo);
		validarAutorizacao(Optional.empty(), CertificadoAutorizacoes::getEmitir);
		validarInexistencia(
				chave -> repositorio.existePorIdentidade(beneficiario, tipo, disciplina, periodo), beneficiario,
				CERTIFICADO_DUPLICADO);
		var evidencia = elegibilidadeServico.calcular(beneficiario, tipo, disciplina, periodo);
		validarElegibilidade(tipo, evidencia);
		var certificado = new Certificado(Optional.empty(), gerarCodigoUnico(), beneficiario,
				evidencia.beneficiarioNome(), tipo, disciplina, evidencia.disciplinaNome(), periodo,
				evidencia.quantidadeSessoes(), evidencia.cargaHorariaMinutos(), getSegurancaServico().getUsuario(),
				relogioServico.agora());
		return repositorio.incluir(certificado);
	}

	public Certificado validar(CodigoVerificacao codigo) {
		Objects.requireNonNull(codigo);
		return repositorio.buscarPorCodigo(codigo);
	}

	private void validarElegibilidade(CertificadoTipo tipo, EvidenciaCertificacao evidencia) {
		Objects.requireNonNull(tipo);
		Objects.requireNonNull(evidencia);
		if (tipo == CertificadoTipo.ALUNO) {
			Validate.validState(evidencia.quantidadeSessoes() >= 5, ALUNO_INELEGIVEL);
		} else {
			Validate.validState(evidencia.cargaHorariaMinutos() > 0, MONITOR_INELEGIVEL);
		}
	}

	private CodigoVerificacao gerarCodigoUnico() {
		for (var tentativa = 0; tentativa < TENTATIVAS_CODIGO; tentativa++) {
			var codigo = geradorCodigo.gerar();
			if (!repositorio.existePorCodigo(codigo)) {
				return codigo;
			}
		}
		throw new IllegalStateException("Não foi possível gerar um código de verificação único");
	}

	private UsuarioIdentificador usuarioAtual() {
		return usuarioRepositorio.buscarPorEmail(new UsuarioEmail(getSegurancaServico().getUsuario())).getIdentificador()
				.orElseThrow();
	}
}
