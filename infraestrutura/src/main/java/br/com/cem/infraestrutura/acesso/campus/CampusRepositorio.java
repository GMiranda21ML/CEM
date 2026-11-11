package br.com.cem.infraestrutura.acesso.campus;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.CampusServico;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class CampusRepositorio extends AgregadoRepositorioAbstrato<CampusIdentificador, Campus, Long, CampusJpa>
		implements ICampusRepositorio {
	private final ICampusRepositorioJpa repositorioJpa;

	public CampusRepositorio(ICampusRepositorioJpa repositorioJpa) {
		super(repositorioJpa, CampusServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected CampusJpa paraJpa(Campus agregado) {
		Objects.requireNonNull(agregado);
		var identificador = agregado.getIdentificador().map(this::paraIdentificadorJpa).orElse(null);
		return new CampusJpa(identificador, agregado.getInstituicao().toString(), agregado.getNome(),
				agregado.getEndereco(), agregado.getSituacao());
	}

	@Override
	protected Campus paraAgregado(CampusJpa agregadoJpa) {
		Objects.requireNonNull(agregadoJpa);
		var identificador = new CampusIdentificador(Objects.requireNonNull(agregadoJpa.getIdentificador()).toString());
		return new Campus(Optional.of(identificador), new InstituicaoIdentificador(agregadoJpa.getInstituicao()),
				agregadoJpa.getNome(), agregadoJpa.getEndereco(), agregadoJpa.getSituacao());
	}

	@Override
	protected Long paraIdentificadorJpa(CampusIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return converter(identificador).orElseThrow(() -> new AgregadoInexistenteExcecao(
				CampusServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador)));
	}

	@Override
	public boolean existePorIdentificador(CampusIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return converter(identificador).isPresent() && super.existePorIdentificador(identificador);
	}

	@Override
	public boolean existePorNome(String nome, InstituicaoIdentificador instituicao,
			Optional<CampusIdentificador> identificadorIgnorado) {
		Objects.requireNonNull(nome);
		Objects.requireNonNull(instituicao);
		Objects.requireNonNull(identificadorIgnorado);
		var ignorado = identificadorIgnorado.flatMap(CampusRepositorio::converter).orElse(null);
		return repositorioJpa.existePorNome(nome, instituicao.toString(), ignorado);
	}

	private static Optional<Long> converter(CampusIdentificador identificador) {
		try {
			return Optional.of(Long.valueOf(identificador.toString()));
		} catch (NumberFormatException excecao) {
			return Optional.empty();
		}
	}
}
