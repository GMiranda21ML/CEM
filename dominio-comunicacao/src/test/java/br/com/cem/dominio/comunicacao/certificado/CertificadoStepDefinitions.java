package br.com.cem.dominio.comunicacao.certificado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.comunicacao.certificado.spi.ICertificadoElegibilidadeServico;
import br.com.cem.dominio.comunicacao.certificado.spi.ICertificadoRepositorio;
import br.com.cem.dominio.comunicacao.certificado.spi.IGeradorCodigoVerificacao;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class CertificadoStepDefinitions {
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 11, 12, 0);
	private static final Periodo PERIODO = new Periodo(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 30));
	private static final DisciplinaIdentificador DISCIPLINA = new DisciplinaIdentificador("ALG");

	private final Map<String, Usuario> usuarios = new LinkedHashMap<>();
	private final Map<String, Certificado> certificados = new LinkedHashMap<>();
	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final ICertificadoRepositorio repositorio = mock(ICertificadoRepositorio.class);
	private final ICertificadoElegibilidadeServico elegibilidadeServico = mock(ICertificadoElegibilidadeServico.class);
	private final IGeradorCodigoVerificacao geradorCodigo = mock(IGeradorCodigoVerificacao.class);
	private final IUsuarioRepositorio usuarioRepositorio = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogioServico = mock(IRelogioServico.class);
	private final CertificadoServico servico = new CertificadoServico(segurancaServico, repositorio,
			elegibilidadeServico, geradorCodigo, usuarioRepositorio, relogioServico);

	private int proximoUsuario = 1;
	private int proximoCertificado = 1;
	private String usuarioAutenticado;
	private Usuario beneficiario;
	private CertificadoTipo tipo;
	private EvidenciaCertificacao evidencia;
	private Certificado resultado;
	private RuntimeException excecaoCapturada;

	public CertificadoStepDefinitions() {
		when(relogioServico.agora()).thenReturn(AGORA);
		when(segurancaServico.getUsuario()).thenAnswer(chamada -> usuarioAutenticado);
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name()))
				.thenAnswer(chamada -> "coordenador@cem.edu.br".equals(usuarioAutenticado));
		when(usuarioRepositorio.buscarPorEmail(any())).thenAnswer(chamada -> {
			UsuarioEmail email = chamada.getArgument(0);
			return usuarios.values().stream().filter(usuario -> usuario.getEmailInstitucional().equals(email)).findFirst()
					.orElseThrow();
		});
		when(elegibilidadeServico.calcular(any(), any(), any(), any())).thenAnswer(chamada -> evidencia);
		when(geradorCodigo.gerar()).thenAnswer(chamada -> new CodigoVerificacao(
				"CEM-" + String.format("%020d", proximoCertificado)));
		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> certificados.containsKey(chamada.getArgument(0).toString()));
		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> {
			CertificadoIdentificador identificador = chamada.getArgument(0);
			var certificado = certificados.get(identificador.toString());
			if (certificado == null) {
				throw new AgregadoInexistenteExcecao(
						CertificadoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador));
			}
			return certificado;
		});
		when(repositorio.existePorCodigo(any())).thenAnswer(chamada -> {
			CodigoVerificacao codigo = chamada.getArgument(0);
			return certificados.values().stream().anyMatch(certificado -> certificado.getCodigo().equals(codigo));
		});
		when(repositorio.buscarPorCodigo(any())).thenAnswer(chamada -> {
			CodigoVerificacao codigo = chamada.getArgument(0);
			return certificados.values().stream().filter(certificado -> certificado.getCodigo().equals(codigo)).findFirst()
					.orElseThrow(() -> new AgregadoInexistenteExcecao(CertificadoServico.CODIGO_NAO_ENCONTRADO));
		});
		when(repositorio.existePorIdentidade(any(), any(), any(), any())).thenAnswer(chamada -> {
			UsuarioIdentificador usuario = chamada.getArgument(0);
			CertificadoTipo tipoConsultado = chamada.getArgument(1);
			DisciplinaIdentificador disciplina = chamada.getArgument(2);
			Periodo periodo = chamada.getArgument(3);
			return certificados.values().stream().anyMatch(certificado -> certificado.getBeneficiario().equals(usuario)
					&& certificado.getTipo() == tipoConsultado && certificado.getDisciplina().equals(disciplina)
					&& certificado.getPeriodo().equals(periodo));
		});
		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Certificado certificado = chamada.getArgument(0);
			var identificador = new CertificadoIdentificador(String.valueOf(proximoCertificado++));
			var incluido = copiar(certificado, Optional.of(identificador));
			certificados.put(identificador.toString(), incluido);
			return incluido;
		});
	}

	@Dado("que o coordenador está autenticado")
	public void coordenadorAutenticado() {
		usuarioAutenticado = "coordenador@cem.edu.br";
		adicionarUsuario("Coordenador", usuarioAutenticado, Autorizacao.COORDENADOR);
	}

	@Dado("que um aluno está autenticado")
	public void alunoAutenticado() {
		usuarioAutenticado = "ana@aluno.cem.edu.br";
		beneficiario = adicionarUsuario("Ana", usuarioAutenticado, Autorizacao.ALUNO);
	}

	@Dado("que o aluno {string} participou de {int} sessões realizadas da disciplina {string} no período informado")
	public void alunoParticipou(String nome, int sessoes, String disciplina) {
		beneficiario = garantirUsuario(nome, Autorizacao.ALUNO);
		tipo = CertificadoTipo.ALUNO;
		evidencia = new EvidenciaCertificacao(nome, disciplina, sessoes, sessoes * 60L);
	}

	@Dado("que o monitor {string} ministrou {int} minutos em sessões realizadas da disciplina {string} no período informado")
	public void monitorMinistrou(String nome, int minutos, String disciplina) {
		beneficiario = garantirUsuario(nome, Autorizacao.ALUNO);
		tipo = CertificadoTipo.MONITOR;
		evidencia = new EvidenciaCertificacao(nome, disciplina, 1, minutos);
	}

	@Dado("que o monitor {string} não ministrou tempo efetivo em sessão realizada no período informado")
	public void monitorNaoMinistrou(String nome) {
		beneficiario = garantirUsuario(nome, Autorizacao.ALUNO);
		tipo = CertificadoTipo.MONITOR;
		evidencia = new EvidenciaCertificacao(nome, "Algoritmos", 0, 0);
	}

	@Dado("que já existe certificado para {string}, do tipo aluno, da disciplina {string} e do período informado")
	public void certificadoDuplicado(String nome, String disciplina) {
		beneficiario = garantirUsuario(nome, Autorizacao.ALUNO);
		tipo = CertificadoTipo.ALUNO;
		evidencia = new EvidenciaCertificacao(nome, disciplina, 5, 300);
		adicionarCertificado(beneficiario, tipo, new CodigoVerificacao("CEM-DUPLICADO"));
	}

	@Dado("que o aluno {string} possui {int} presenças, {int} falta e {int} sessão cancelada na disciplina {string}")
	public void alunoComParticipacoesInvalidas(String nome, int presencas, int faltas, int canceladas,
			String disciplina) {
		beneficiario = garantirUsuario(nome, Autorizacao.ALUNO);
		tipo = CertificadoTipo.ALUNO;
		evidencia = new EvidenciaCertificacao(nome, disciplina, presencas, presencas * 60L);
	}

	@Dado("que existe certificado emitido para o aluno {string}")
	public void existeCertificadoDoAluno(String nome) {
		beneficiario = garantirUsuario(nome, Autorizacao.ALUNO);
		usuarioAutenticado = beneficiario.getEmailInstitucional().toString();
		resultado = adicionarCertificado(beneficiario, CertificadoTipo.ALUNO,
				new CodigoVerificacao("CEM-CERTIFICADO-ANA"));
		var outro = garantirUsuario("Outra Pessoa", Autorizacao.ALUNO);
		adicionarCertificado(outro, CertificadoTipo.ALUNO, new CodigoVerificacao("CEM-OUTRO-CERTIFICADO"));
	}

	@Dado("que existe certificado com o código {string}")
	public void existeCertificadoComCodigo(String codigo) {
		beneficiario = garantirUsuario("Ana", Autorizacao.ALUNO);
		resultado = adicionarCertificado(beneficiario, CertificadoTipo.ALUNO, new CodigoVerificacao(codigo));
	}

	@Dado("que não existe certificado com o código {string}")
	public void naoExisteCertificadoComCodigo(String codigo) {
		certificados.values().removeIf(certificado -> certificado.getCodigo().equals(new CodigoVerificacao(codigo)));
	}

	private Usuario garantirUsuario(String nome, Autorizacao papel) {
		return usuarios.values().stream().filter(usuario -> usuario.getNome().equals(nome)).findFirst()
				.orElseGet(() -> adicionarUsuario(nome, nome.toLowerCase().replace(' ', '.') + "@aluno.cem.edu.br", papel));
	}

	private Usuario adicionarUsuario(String nome, String email, Autorizacao papel) {
		var usuario = new Usuario(Optional.of(new UsuarioIdentificador(String.valueOf(proximoUsuario++))), nome,
				new UsuarioEmail(email), new UsuarioSenha("Cem@2026!"), papel, "Computação", null,
				UsuarioSituacao.ATIVO);
		usuarios.put(usuario.getIdentificador().orElseThrow().toString(), usuario);
		return usuario;
	}

	private Certificado adicionarCertificado(Usuario usuario, CertificadoTipo tipo, CodigoVerificacao codigo) {
		var identificador = new CertificadoIdentificador(String.valueOf(proximoCertificado++));
		var certificado = new Certificado(Optional.of(identificador), codigo,
				usuario.getIdentificador().orElseThrow(), usuario.getNome(), tipo, DISCIPLINA, "Algoritmos", PERIODO, 5,
				300, "coordenador@cem.edu.br", AGORA);
		certificados.put(identificador.toString(), certificado);
		return certificado;
	}

	private static Certificado copiar(Certificado certificado, Optional<CertificadoIdentificador> identificador) {
		return new Certificado(identificador, certificado.getCodigo(), certificado.getBeneficiario(),
				certificado.getBeneficiarioNome(), certificado.getTipo(), certificado.getDisciplina(),
				certificado.getDisciplinaNome(), certificado.getPeriodo(), certificado.getQuantidadeSessoes(),
				certificado.getCargaHorariaMinutos(), certificado.getCoordenador(), certificado.getEmitidoEm());
	}

	@Quando("o coordenador emite o certificado de aluno para {string}")
	@Quando("o coordenador emite o certificado de monitor para {string}")
	@Quando("o coordenador tenta emitir o certificado de aluno para {string}")
	@Quando("o coordenador tenta emitir o certificado de monitor para {string}")
	public void coordenadorEmite(String nome) {
		executarEmissao();
	}

	@Quando("o coordenador tenta emitir novamente o mesmo certificado")
	public void coordenadorEmiteNovamente() {
		executarEmissao();
	}

	@Quando("o aluno tenta emitir seu próprio certificado")
	public void alunoTentaEmitir() {
		tipo = CertificadoTipo.ALUNO;
		evidencia = new EvidenciaCertificacao(beneficiario.getNome(), "Algoritmos", 5, 300);
		executarEmissao();
	}

	private void executarEmissao() {
		executar(() -> resultado = servico.emitir(beneficiario.getIdentificador().orElseThrow(), tipo, DISCIPLINA,
				PERIODO));
	}

	@Quando("{string} consulta seus certificados")
	public void consultaSeusCertificados(String nome) {
		executar(() -> resultado = servico.buscar(resultado.getIdentificador().orElseThrow()));
	}

	@Quando("qualquer pessoa valida o código {string}")
	public void validarCodigo(String codigo) {
		executar(() -> resultado = servico.validar(new CodigoVerificacao(codigo)));
	}

	private void executar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Entao("o certificado é emitido com {int} sessões e a carga horária efetiva")
	public void certificadoEmitidoComSessoes(int sessoes) {
		assertNull(excecaoCapturada);
		assertNotNull(resultado);
		assertEquals(sessoes, resultado.getQuantidadeSessoes());
		assertTrue(resultado.getCargaHorariaMinutos() > 0);
	}

	@Entao("o certificado possui um código de verificação único")
	public void certificadoPossuiCodigo() {
		assertNotNull(resultado.getCodigo());
		assertEquals(1L, certificados.values().stream()
				.filter(certificado -> certificado.getCodigo().equals(resultado.getCodigo())).count());
	}

	@Entao("o certificado é emitido com carga horária de {int} minutos")
	public void certificadoComCargaHoraria(int minutos) {
		assertNull(excecaoCapturada);
		assertEquals(minutos, resultado.getCargaHorariaMinutos());
	}

	@Entao("a emissão do certificado é recusada")
	public void emissaoRecusada() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("apenas os certificados de {string} são apresentados")
	public void apenasCertificadosDoBeneficiario(String nome) {
		assertNull(excecaoCapturada);
		assertEquals(nome, resultado.getBeneficiarioNome());
	}

	@Entao("são apresentados os dados públicos do certificado")
	public void dadosPublicosApresentados() {
		assertNull(excecaoCapturada);
		assertNotNull(resultado);
		assertNotNull(resultado.getBeneficiarioNome());
		assertNotNull(resultado.getDisciplinaNome());
	}

	@Entao("é apresentada a informação {string}")
	public void informacaoApresentada(String mensagem) {
		assertNotNull(excecaoCapturada);
		assertEquals(mensagem, excecaoCapturada.getMessage());
	}
}
