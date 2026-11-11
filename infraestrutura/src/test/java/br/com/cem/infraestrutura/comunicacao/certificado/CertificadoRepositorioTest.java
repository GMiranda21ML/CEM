package br.com.cem.infraestrutura.comunicacao.certificado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoServico;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;

@DataJpaTest
class CertificadoRepositorioTest {
	@Autowired
	private ICertificadoRepositorioJpa repositorioJpa;

	@Autowired
	private IUsuarioRepositorioJpa usuarioRepositorioJpa;

	private CertificadoRepositorio repositorio;
	private UsuarioRepositorio usuarioRepositorio;

	@BeforeEach
	void configurar() {
		repositorio = new CertificadoRepositorio(repositorioJpa, usuarioRepositorioJpa);
		usuarioRepositorio = new UsuarioRepositorio(usuarioRepositorioJpa);
	}

	@Test
	void incluiBuscaPorCodigoEVerificaIdentidade() {
		var usuario = incluirUsuario();
		var disciplina = new DisciplinaIdentificador("ALG");
		var periodo = new Periodo(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 30));
		var codigo = new CodigoVerificacao("CEM-12345678901234567890");
		var certificado = criar(usuario, disciplina, periodo, codigo);

		var incluido = repositorio.incluir(certificado);

		assertTrue(repositorio.existePorCodigo(codigo));
		assertTrue(repositorio.existePorIdentidade(usuario.getIdentificador().orElseThrow(), CertificadoTipo.ALUNO,
				disciplina, periodo));
		assertEquals(incluido.getIdentificador(), repositorio.buscarPorCodigo(codigo).getIdentificador());
	}

	@Test
	void codigoInexistenteProduzMensagemPublica() {
		var excecao = assertThrows(AgregadoInexistenteExcecao.class,
				() -> repositorio.buscarPorCodigo(new CodigoVerificacao("CEM-INEXISTENTE")));
		assertEquals(CertificadoServico.CODIGO_NAO_ENCONTRADO, excecao.getMessage());
		assertFalse(repositorio.existePorCodigo(new CodigoVerificacao("CEM-INEXISTENTE")));
	}

	private Usuario incluirUsuario() {
		return usuarioRepositorio.incluir(new Usuario(Optional.empty(), "Ana",
				new UsuarioEmail("ana@aluno.cem.edu.br"), new UsuarioSenha("Cem@2026!"), Autorizacao.ALUNO,
				"Computação", null, UsuarioSituacao.ATIVO));
	}

	private static Certificado criar(Usuario usuario, DisciplinaIdentificador disciplina, Periodo periodo,
			CodigoVerificacao codigo) {
		return new Certificado(Optional.empty(), codigo, usuario.getIdentificador().orElseThrow(), usuario.getNome(),
				CertificadoTipo.ALUNO, disciplina, "Algoritmos", periodo, 5, 300, "coordenador@cem.edu.br",
				LocalDateTime.of(2026, 11, 11, 12, 0));
	}
}
