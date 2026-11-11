package br.com.cem.infraestrutura.comunicacao.certificado;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.aplicacao.comunicacao.certificado.CertificadoConsulta;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;

@DataJpaTest
class CertificadoAplicacaoRepositorioTest {
	@Autowired
	private ICertificadoRepositorioJpa repositorioJpa;

	@Autowired
	private IUsuarioRepositorioJpa usuarioRepositorioJpa;

	private CertificadoRepositorio repositorio;
	private CertificadoAplicacaoRepositorio aplicacaoRepositorio;
	private UsuarioRepositorio usuarioRepositorio;

	@BeforeEach
	void configurar() {
		usuarioRepositorio = new UsuarioRepositorio(usuarioRepositorioJpa);
		repositorio = new CertificadoRepositorio(repositorioJpa, usuarioRepositorioJpa);
		aplicacaoRepositorio = new CertificadoAplicacaoRepositorio(repositorioJpa);
	}

	@Test
	void pesquisaSomenteCertificadosDoBeneficiario() {
		var ana = incluirUsuario("Ana", "ana@aluno.cem.edu.br");
		var bia = incluirUsuario("Bia", "bia@aluno.cem.edu.br");
		incluirCertificado(ana, "CEM-CERTIFICADO-ANA");
		incluirCertificado(bia, "CEM-CERTIFICADO-BIA");
		var consulta = new CertificadoConsulta(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.of(CertificadoTipo.ALUNO), Optional.empty());

		var resultado = aplicacaoRepositorio.pesquisarDoBeneficiario(consulta,
				ana.getIdentificador().orElseThrow());

		assertEquals(1, resultado.getTotal());
		assertEquals("Ana", resultado.getProjecoes().getFirst().getBeneficiarioNome());
	}

	private Usuario incluirUsuario(String nome, String email) {
		return usuarioRepositorio.incluir(new Usuario(Optional.empty(), nome, new UsuarioEmail(email),
				new UsuarioSenha("Cem@2026!"), Autorizacao.ALUNO, "Computação", null, UsuarioSituacao.ATIVO));
	}

	private void incluirCertificado(Usuario usuario, String codigo) {
		var periodo = new Periodo(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 30));
		repositorio.incluir(new Certificado(Optional.empty(), new CodigoVerificacao(codigo),
				usuario.getIdentificador().orElseThrow(), usuario.getNome(), CertificadoTipo.ALUNO,
				new DisciplinaIdentificador("ALG"), "Algoritmos", periodo, 5, 300, "coordenador@cem.edu.br",
				LocalDateTime.of(2026, 11, 11, 12, 0)));
	}
}
