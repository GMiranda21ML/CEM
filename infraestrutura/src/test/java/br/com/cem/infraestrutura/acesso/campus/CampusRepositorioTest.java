package br.com.cem.infraestrutura.acesso.campus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.CampusSituacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;

@DataJpaTest
class CampusRepositorioTest {
	@Autowired
	private ICampusRepositorioJpa repositorioJpa;

	private CampusRepositorio repositorio;

	@BeforeEach
	void configurar() {
		repositorio = new CampusRepositorio(repositorioJpa);
	}

	@Test
	void incluiBuscaEAltera() {
		var instituicao = new InstituicaoIdentificador("cem.edu.br");
		var incluido = repositorio.incluir(
				new Campus(Optional.empty(), instituicao, "Centro", "Rua Principal", CampusSituacao.ATIVO));

		var identificador = incluido.getIdentificador().orElseThrow();
		var encontrado = repositorio.buscarPorIdentificador(identificador);
		assertEquals("Centro", encontrado.getNome());
		assertTrue(encontrado.estaAtivo());

		repositorio.alterar(encontrado.inativar());
		assertFalse(repositorio.buscarPorIdentificador(identificador).estaAtivo());
	}

	@Test
	void verificaNomeNaInstituicaoIgnorandoOProprioCampus() {
		var instituicao = new InstituicaoIdentificador("cem.edu.br");
		var incluido = repositorio.incluir(
				new Campus(Optional.empty(), instituicao, "Centro", null, CampusSituacao.ATIVO));

		assertTrue(repositorio.existePorNome("centro", instituicao, Optional.empty()));
		assertFalse(repositorio.existePorNome("Centro", instituicao, incluido.getIdentificador()));
		assertFalse(repositorio.existePorNome("Centro", new InstituicaoIdentificador("outra.edu.br"),
				Optional.empty()));
	}

	@Test
	void identificadorNaoNumericoNaoExiste() {
		assertFalse(repositorio.existePorIdentificador(new CampusIdentificador("abc")));
	}
}
