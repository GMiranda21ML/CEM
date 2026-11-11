package br.com.cem.infraestrutura.monitoria.avaliacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoConsulta;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoPerfil;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

@DataJpaTest
class AvaliacaoAplicacaoRepositorioTest {
	@Autowired
	private IAvaliacaoRepositorioJpa repositorioJpa;

	@Autowired
	private IUsuarioRepositorioJpa usuarioRepositorioJpa;

	private AvaliacaoRepositorio repositorio;
	private AvaliacaoAplicacaoRepositorio aplicacaoRepositorio;
	private UsuarioRepositorio usuarioRepositorio;

	@BeforeEach
	void configurar() {
		usuarioRepositorio = new UsuarioRepositorio(usuarioRepositorioJpa);
		var segurancaServico = mock(ISegurancaServico.class);
		when(segurancaServico.getUsuario()).thenReturn("ana@aluno.cem.edu.br");
		repositorio = new AvaliacaoRepositorio(repositorioJpa, usuarioRepositorioJpa);
		aplicacaoRepositorio = new AvaliacaoAplicacaoRepositorio(repositorioJpa, segurancaServico, usuarioRepositorio);
	}

	@Test
	void pesquisaPropriasEAvaliacoesDaSessao() {
		var ana = incluirUsuario("Ana", "ana@aluno.cem.edu.br");
		var bia = incluirUsuario("Bia", "bia@aluno.cem.edu.br");
		var sessao = new SessaoIdentificador("S1");
		incluirAvaliacao(sessao, ana, 3);
		incluirAvaliacao(sessao, bia, 5);
		var consulta = new AvaliacaoConsulta(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.of(sessao));

		assertEquals(1, aplicacaoRepositorio.pesquisar(consulta).getTotal());
		assertEquals(2, aplicacaoRepositorio.pesquisarDaSessao(consulta, sessao).getTotal());
		assertEquals(4, aplicacaoRepositorio.calcularMedia(sessao));
	}

	private Usuario incluirUsuario(String nome, String email) {
		return usuarioRepositorio.incluir(new Usuario(Optional.empty(), nome, new UsuarioEmail(email),
				new UsuarioSenha("Cem@2026!"), Autorizacao.ALUNO, "Computação", null, UsuarioSituacao.ATIVO));
	}

	private void incluirAvaliacao(SessaoIdentificador sessao, Usuario autor, int nota) {
		var agora = LocalDateTime.of(2026, 11, 11, 12, 0);
		repositorio.incluir(new Avaliacao(Optional.empty(), sessao, autor.getIdentificador().orElseThrow(),
				AvaliacaoPerfil.ALUNO, nota, null, agora, agora));
	}
}
