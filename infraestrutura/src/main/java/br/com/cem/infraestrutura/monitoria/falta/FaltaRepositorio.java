package br.com.cem.infraestrutura.monitoria.falta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.falta.FaltaServico;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.ISessaoRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class FaltaRepositorio
		extends AgregadoRepositorioAbstrato<FaltaIdentificador, Falta, Long, FaltaJpa>
		implements IFaltaRepositorio {
	private final IFaltaRepositorioJpa repositorioJpa;
	private final ISessaoRepositorioJpa sessaoRepositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public FaltaRepositorio(
			IFaltaRepositorioJpa repositorioJpa,
			ISessaoRepositorioJpa sessaoRepositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, FaltaServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.sessaoRepositorioJpa = Objects.requireNonNull(sessaoRepositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected FaltaJpa paraJpa(Falta a) {
		Objects.requireNonNull(a);
		return new FaltaJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				sessaoRepositorioJpa.getReferenceById(Long.valueOf(a.getSessao().toString())),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getPessoa().toString())),
				a.getPapel(),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getAutor().toString())),
				a.getObservacao().orElse(null),
				a.getRegistradaEm(),
				a.getReposicao()
						.map(
								id ->
										sessaoRepositorioJpa.getReferenceById(
												Long.valueOf(id.toString())))
						.orElse(null));
	}

	@Override
	protected Falta paraAgregado(FaltaJpa a) {
		Objects.requireNonNull(a);
		return new Falta(
				Optional.of(
						new FaltaIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				new SessaoIdentificador(
						Objects.requireNonNull(a.getSessao().getIdentificador()).toString()),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getPessoa().getIdentificador()).toString()),
				a.getPapel(),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getAutor().getIdentificador()).toString()),
				Optional.ofNullable(a.getObservacao()),
				a.getRegistradaEm(),
				Optional.ofNullable(a.getReposicao())
						.map(
								s ->
										new SessaoIdentificador(
												Objects.requireNonNull(s.getIdentificador())
														.toString())));
	}

	@Override
	protected Long paraIdentificadorJpa(FaltaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					FaltaServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(FaltaIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean existePorSessaoEPessoa(SessaoIdentificador sessao, UsuarioIdentificador pessoa) {
		return repositorioJpa.existePorSessaoEPessoa(
				Long.valueOf(sessao.toString()), Long.valueOf(pessoa.toString()));
	}

	@Override
	public long contar(UsuarioIdentificador pessoa, String papel, String disciplina) {
		return repositorioJpa.contar(
				Long.valueOf(pessoa.toString()),
				Objects.requireNonNull(papel),
				Objects.requireNonNull(disciplina));
	}

	@Override
	public Collection<Falta> listarPorDisciplina(String disciplina) {
		Objects.requireNonNull(disciplina);
		return repositorioJpa.listarPorDisciplina(disciplina).stream()
				.map(this::paraAgregado)
				.toList();
	}
}
