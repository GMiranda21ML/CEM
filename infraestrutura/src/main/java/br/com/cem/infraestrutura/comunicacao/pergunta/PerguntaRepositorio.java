package br.com.cem.infraestrutura.comunicacao.pergunta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.pergunta.Pergunta;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaIdentificador;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaServico;
import br.com.cem.dominio.comunicacao.pergunta.spi.IPerguntaRepositorio;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Objects;
import java.util.Optional;

public class PerguntaRepositorio
		extends AgregadoRepositorioAbstrato<PerguntaIdentificador, Pergunta, Long, PerguntaJpa>
		implements IPerguntaRepositorio {
	private final IPerguntaRepositorioJpa repositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	@Override
	public void bloquear(PerguntaIdentificador identificador) {
		repositorioJpa.bloquear(paraIdentificadorJpa(Objects.requireNonNull(identificador)));
	}

	public PerguntaRepositorio(
			IPerguntaRepositorioJpa repositorioJpa, IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, PerguntaServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected PerguntaJpa paraJpa(Pergunta a) {
		Objects.requireNonNull(a);
		return new PerguntaJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getAutor().toString())),
				a.getTexto(),
				a.getSituacao(),
				a.getResposta().orElse(null),
				a.getCriadaEm());
	}

	@Override
	protected Pergunta paraAgregado(PerguntaJpa a) {
		Objects.requireNonNull(a);
		return new Pergunta(
				Optional.of(
						new PerguntaIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getAutor().getIdentificador()).toString()),
				a.getTexto(),
				a.getSituacao(),
				Optional.ofNullable(a.getResposta()),
				a.getCriadaEm());
	}

	@Override
	protected Long paraIdentificadorJpa(PerguntaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					PerguntaServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(PerguntaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}
}
