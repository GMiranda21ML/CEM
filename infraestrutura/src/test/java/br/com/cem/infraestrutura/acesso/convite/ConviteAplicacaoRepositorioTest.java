package br.com.cem.infraestrutura.acesso.convite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.aplicacao.acesso.convite.ConviteConsulta;
import br.com.cem.aplicacao.acesso.convite.ConviteOrdenacao;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteCodigo;
import br.com.cem.dominio.acesso.convite.ConviteEmail;
import br.com.cem.dominio.acesso.convite.ConviteSituacao;

@DataJpaTest
class ConviteAplicacaoRepositorioTest {
	@Autowired
	private IConviteRepositorioJpa repositorioJpa;

	private ConviteRepositorio repositorio;
	private ConviteAplicacaoRepositorio repositorioAplicacao;

	@BeforeEach
	void configurar() {
		repositorio = new ConviteRepositorio(repositorioJpa);
		repositorioAplicacao = new ConviteAplicacaoRepositorio(repositorioJpa);
	}

	private static Convite criar(String email, ConviteSituacao situacao) {
		var agora = LocalDateTime.now();
		return new Convite(Optional.empty(), new ConviteEmail(email), Autorizacao.PROFESSOR, "Ciência da Computação",
				new ConviteCodigo("423037"), agora, agora.plusDays(Convite.VALIDADE_DIAS), situacao);
	}

	private static ConviteConsulta consultar(Optional<ConviteSituacao> situacao) {
		return new ConviteConsulta(Optional.of(ConviteOrdenacao.EMAIL), Optional.empty(), Optional.empty(),
				Optional.empty(), situacao);
	}

	@Test
	void pesquisaRetornaOsConvites() {
		repositorio.incluir(criar("beatriz.lopes@cem.edu.br", ConviteSituacao.AGUARDANDO_ACEITE));
		repositorio.incluir(criar("marta.siqueira@cem.edu.br", ConviteSituacao.ACEITO));

		var resultado = repositorioAplicacao.pesquisar(consultar(Optional.empty()));

		assertEquals(2, resultado.getTotal());
	}

	@Test
	void pesquisaFiltrandoPorSituacao() {
		repositorio.incluir(criar("beatriz.lopes@cem.edu.br", ConviteSituacao.AGUARDANDO_ACEITE));
		repositorio.incluir(criar("marta.siqueira@cem.edu.br", ConviteSituacao.ACEITO));

		var resultado = repositorioAplicacao.pesquisar(consultar(Optional.of(ConviteSituacao.AGUARDANDO_ACEITE)));

		assertEquals(1, resultado.getTotal());
		assertEquals(new ConviteEmail("beatriz.lopes@cem.edu.br"), resultado.getProjecoes().get(0).getEmail());
	}
}
