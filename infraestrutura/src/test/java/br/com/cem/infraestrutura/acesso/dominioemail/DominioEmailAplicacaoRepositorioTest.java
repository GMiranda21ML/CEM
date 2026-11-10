package br.com.cem.infraestrutura.acesso.dominioemail;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailConsulta;
import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailOrdenacao;
import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;

@DataJpaTest
class DominioEmailAplicacaoRepositorioTest {
	@Autowired
	private IDominioEmailRepositorioJpa repositorioJpa;

	private DominioEmailRepositorio repositorio;
	private DominioEmailAplicacaoRepositorio repositorioAplicacao;

	@BeforeEach
	void configurar() {
		repositorio = new DominioEmailRepositorio(repositorioJpa);
		repositorioAplicacao = new DominioEmailAplicacaoRepositorio(repositorioJpa);
	}

	private static DominioEmailConsulta consultar() {
		return new DominioEmailConsulta(Optional.of(DominioEmailOrdenacao.DOMINIO), Optional.empty(), Optional.empty(),
				Optional.empty());
	}

	@Test
	void pesquisaRetornaOsDominiosLiberados() {
		repositorio.incluir(new DominioEmail(new DominioEmailDominio("aluno.cem.edu.br")));
		repositorio.incluir(new DominioEmail(new DominioEmailDominio("pos.cem.edu.br")));

		var resultado = repositorioAplicacao.pesquisar(consultar());

		assertEquals(2, resultado.getTotal());
		assertEquals(new DominioEmailDominio("aluno.cem.edu.br"), resultado.getProjecoes().get(0).getIdentificador());
		assertEquals(new DominioEmailDominio("pos.cem.edu.br"), resultado.getProjecoes().get(1).getIdentificador());
	}

	@Test
	void pesquisaSemDominiosRetornaResultadoVazio() {
		var resultado = repositorioAplicacao.pesquisar(consultar());

		assertEquals(0, resultado.getTotal());
	}
}
