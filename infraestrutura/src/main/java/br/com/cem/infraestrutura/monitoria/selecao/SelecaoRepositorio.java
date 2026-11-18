package br.com.cem.infraestrutura.monitoria.selecao;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoServico;
import br.com.cem.dominio.monitoria.selecao.spi.ISelecaoRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Objects;
import java.util.Optional;

public class SelecaoRepositorio
		extends AgregadoRepositorioAbstrato<SelecaoIdentificador, Selecao, Long, SelecaoJpa>
		implements ISelecaoRepositorio {
	private final ISelecaoRepositorioJpa repositorioJpa;
	private final IDisciplinaRepositorioJpa disciplinaRepositorioJpa;
	private final IUsuarioRepositorioJpa usuarioRepositorioJpa;

	public SelecaoRepositorio(
			ISelecaoRepositorioJpa repositorioJpa,
			IDisciplinaRepositorioJpa disciplinaRepositorioJpa,
			IUsuarioRepositorioJpa usuarioRepositorioJpa) {
		super(repositorioJpa, SelecaoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.disciplinaRepositorioJpa = Objects.requireNonNull(disciplinaRepositorioJpa);
		this.usuarioRepositorioJpa = Objects.requireNonNull(usuarioRepositorioJpa);
	}

	@Override
	protected SelecaoJpa paraJpa(Selecao a) {
		Objects.requireNonNull(a);
		return new SelecaoJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				disciplinaRepositorioJpa.getReferenceById(
						Long.valueOf(a.getDisciplina().toString())),
				usuarioRepositorioJpa.getReferenceById(Long.valueOf(a.getProfessor().toString())),
				a.getSemestre(),
				a.getPrazoInscricao(),
				a.getVagas(),
				a.getCrMinimo(),
				a.getPesoCr(),
				a.getPesoEntrevista(),
				a.getPatamarMinimo(),
				a.getSituacao(),
				a.getMotivoEncerramento().orElse(null),
				a.getAbertaEm(),
				a.getEncerradaEm().orElse(null));
	}

	@Override
	protected Selecao paraAgregado(SelecaoJpa a) {
		Objects.requireNonNull(a);
		return new Selecao(
				Optional.of(
						new SelecaoIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				new DisciplinaIdentificador(
						Objects.requireNonNull(a.getDisciplina().getIdentificador()).toString()),
				new UsuarioIdentificador(
						Objects.requireNonNull(a.getProfessor().getIdentificador()).toString()),
				a.getSemestre(),
				a.getPrazoInscricao(),
				a.getVagas(),
				a.getCrMinimo(),
				a.getPesoCr(),
				a.getPesoEntrevista(),
				a.getPatamarMinimo(),
				a.getSituacao(),
				Optional.ofNullable(a.getMotivoEncerramento()),
				a.getAbertaEm(),
				Optional.ofNullable(a.getEncerradaEm()));
	}

	@Override
	protected Long paraIdentificadorJpa(SelecaoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					SelecaoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(SelecaoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean existeAbertaPorDisciplina(
			DisciplinaIdentificador disciplina, Optional<SelecaoIdentificador> ignorado) {
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(ignorado);
		return repositorioJpa.existeAbertaPorDisciplina(
				Long.valueOf(disciplina.toString()),
				ignorado.map(this::paraIdentificadorJpa).orElse(null));
	}
}
