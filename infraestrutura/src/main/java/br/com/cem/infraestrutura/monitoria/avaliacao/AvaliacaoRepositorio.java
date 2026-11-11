package br.com.cem.infraestrutura.monitoria.avaliacao;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoServico;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.spi.IAvaliacaoRepositorio;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class AvaliacaoRepositorio
		extends AgregadoRepositorioAbstrato<AvaliacaoIdentificador, Avaliacao, Long, AvaliacaoJpa>
		implements IAvaliacaoRepositorio {
	private final IAvaliacaoRepositorioJpa repositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public AvaliacaoRepositorio(IAvaliacaoRepositorioJpa repositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, AvaliacaoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected AvaliacaoJpa paraJpa(Avaliacao agregado) {
		Objects.requireNonNull(agregado);
		var identificador = agregado.getIdentificador().map(this::paraIdentificadorJpa).orElse(null);
		var autor = usuarioRepositorioJpa.getReferenceById(converter(agregado.getAutor()));
		return new AvaliacaoJpa(identificador, agregado.getSessao().toString(), autor, agregado.getPerfil(),
				agregado.getNota(), agregado.getComentario(), agregado.getRegistradaEm(), agregado.getAtualizadaEm());
	}

	@Override
	protected Avaliacao paraAgregado(AvaliacaoJpa agregadoJpa) {
		Objects.requireNonNull(agregadoJpa);
		var identificador = new AvaliacaoIdentificador(
				Objects.requireNonNull(agregadoJpa.getIdentificador()).toString());
		var autor = new UsuarioIdentificador(Objects.requireNonNull(agregadoJpa.getAutor().getIdentificador()).toString());
		return new Avaliacao(Optional.of(identificador), new SessaoIdentificador(agregadoJpa.getSessao()), autor,
				agregadoJpa.getPerfil(), agregadoJpa.getNota(), agregadoJpa.getComentario(), agregadoJpa.getRegistradaEm(),
				agregadoJpa.getAtualizadaEm());
	}

	@Override
	protected Long paraIdentificadorJpa(AvaliacaoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return converter(identificador.toString(), identificador);
	}

	@Override
	public boolean existePorIdentificador(AvaliacaoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return converterOpcional(identificador.toString()).isPresent() && super.existePorIdentificador(identificador);
	}

	@Override
	public boolean existePorSessaoEAutor(SessaoIdentificador sessao, UsuarioIdentificador autor) {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(autor);
		return repositorioJpa.existePorSessaoEAutor(sessao.toString(), converter(autor));
	}

	private static Long converter(UsuarioIdentificador identificador) {
		return converter(identificador.toString(), identificador);
	}

	private static Long converter(String valor, Object identificador) {
		return converterOpcional(valor).orElseThrow(() -> new AgregadoInexistenteExcecao(
				AvaliacaoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador)));
	}

	private static Optional<Long> converterOpcional(String valor) {
		try {
			return Optional.of(Long.valueOf(valor));
		} catch (NumberFormatException excecao) {
			return Optional.empty();
		}
	}
}
