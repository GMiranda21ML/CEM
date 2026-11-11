package br.com.cem.infraestrutura.acesso.campus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.aplicacao.acesso.campus.CampusConsulta;
import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusSituacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

@DataJpaTest
class CampusAplicacaoRepositorioTest {
	@Autowired
	private ICampusRepositorioJpa repositorioJpa;

	private CampusRepositorio repositorio;
	private CampusAplicacaoRepositorio aplicacaoRepositorio;

	@BeforeEach
	void configurar() {
		var segurancaServico = mock(ISegurancaServico.class);
		var instituicaoServico = mock(IInstituicaoUsuarioServico.class);
		when(segurancaServico.getUsuario()).thenReturn("coordenador@cem.edu.br");
		when(instituicaoServico.buscarPorUsuario("coordenador@cem.edu.br"))
				.thenReturn(new InstituicaoIdentificador("cem.edu.br"));
		repositorio = new CampusRepositorio(repositorioJpa);
		aplicacaoRepositorio = new CampusAplicacaoRepositorio(repositorioJpa, segurancaServico, instituicaoServico);
	}

	@Test
	void pesquisaSomenteCampiDaInstituicao() {
		repositorio.incluir(new Campus(Optional.empty(), new InstituicaoIdentificador("cem.edu.br"), "Centro", null,
				CampusSituacao.ATIVO));
		repositorio.incluir(new Campus(Optional.empty(), new InstituicaoIdentificador("outra.edu.br"), "Centro", null,
				CampusSituacao.ATIVO));
		var consulta = new CampusConsulta(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.of("cent"), Optional.of(CampusSituacao.ATIVO));

		var resultado = aplicacaoRepositorio.pesquisar(consulta);

		assertEquals(1, resultado.getTotal());
		assertEquals(new InstituicaoIdentificador("cem.edu.br"),
				resultado.getProjecoes().getFirst().getInstituicao());
	}
}
