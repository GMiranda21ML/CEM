package br.com.cem.infraestrutura.acesso.convite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteCodigo;
import br.com.cem.dominio.acesso.convite.ConviteEmail;
import br.com.cem.dominio.acesso.convite.ConviteIdentificador;
import br.com.cem.dominio.acesso.convite.ConviteSituacao;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;

@DataJpaTest
class ConviteRepositorioTest {
	@Autowired
	private IConviteRepositorioJpa repositorioJpa;

	private ConviteRepositorio repositorio;

	@BeforeEach
	void configurar() {
		repositorio = new ConviteRepositorio(repositorioJpa);
	}

	private static Convite criar(String email, ConviteSituacao situacao) {
		var agora = LocalDateTime.now();
		return new Convite(Optional.empty(), new ConviteEmail(email), Autorizacao.PROFESSOR, "Ciência da Computação",
				new ConviteCodigo("423037"), agora, agora.plusDays(Convite.VALIDADE_DIAS), situacao);
	}

	@Test
	void incluiEBuscaPorIdentificador() {
		var incluido = repositorio.incluir(criar("rodrigo.alves@cem.edu.br", ConviteSituacao.AGUARDANDO_ACEITE));

		var convite = repositorio.buscarPorIdentificador(incluido.getIdentificador().orElseThrow());

		assertEquals(new ConviteEmail("rodrigo.alves@cem.edu.br"), convite.getEmailInstitucional());
		assertEquals(Autorizacao.PROFESSOR, convite.getPapel());
		assertEquals(ConviteSituacao.AGUARDANDO_ACEITE, convite.getSituacao());
	}

	@Test
	void buscaPorIdentificadorInexistenteLancaExcecao() {
		assertThrows(AgregadoInexistenteExcecao.class,
				() -> repositorio.buscarPorIdentificador(new ConviteIdentificador("999")));
	}

	@Test
	void existePorIdentificadorComIdentificadorNaoNumerico() {
		assertFalse(repositorio.existePorIdentificador(new ConviteIdentificador("abc")));
	}

	@Test
	void altera() {
		var incluido = repositorio.incluir(criar("rodrigo.alves@cem.edu.br", ConviteSituacao.AGUARDANDO_ACEITE));

		var cancelado = new Convite(incluido.getIdentificador(), incluido.getEmailInstitucional(), incluido.getPapel(),
				incluido.getCurso(), incluido.getCodigo(), incluido.getEnviadoEm(), incluido.getValidoAte(),
				ConviteSituacao.CANCELADO);
		repositorio.alterar(cancelado);

		var convite = repositorio.buscarPorIdentificador(incluido.getIdentificador().orElseThrow());
		assertEquals(ConviteSituacao.CANCELADO, convite.getSituacao());
	}

	@Test
	void existePendentePorEmail() {
		repositorio.incluir(criar("rodrigo.alves@cem.edu.br", ConviteSituacao.AGUARDANDO_ACEITE));
		repositorio.incluir(criar("marta.siqueira@cem.edu.br", ConviteSituacao.ACEITO));

		assertTrue(repositorio.existePendentePorEmail(new ConviteEmail("rodrigo.alves@cem.edu.br")));
		assertFalse(repositorio.existePendentePorEmail(new ConviteEmail("marta.siqueira@cem.edu.br")));
	}

	@Test
	void buscaPendentePorEmail() {
		repositorio.incluir(criar("rodrigo.alves@cem.edu.br", ConviteSituacao.AGUARDANDO_ACEITE));

		var convite = repositorio.buscarPendentePorEmail(new ConviteEmail("rodrigo.alves@cem.edu.br"));

		assertTrue(convite.isPresent());
		assertEquals(ConviteSituacao.AGUARDANDO_ACEITE, convite.orElseThrow().getSituacao());
	}

	@Test
	void naoBuscaConviteJaAceitoComoPendente() {
		repositorio.incluir(criar("marta.siqueira@cem.edu.br", ConviteSituacao.ACEITO));

		var convite = repositorio.buscarPendentePorEmail(new ConviteEmail("marta.siqueira@cem.edu.br"));

		assertTrue(convite.isEmpty());
	}
}
