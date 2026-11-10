package br.com.cem.infraestrutura.acesso.convite;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteCodigo;
import br.com.cem.dominio.acesso.convite.ConviteEmail;
import br.com.cem.dominio.acesso.convite.ConviteIdentificador;
import br.com.cem.dominio.acesso.convite.ConviteServico;
import br.com.cem.dominio.acesso.convite.spi.IConviteRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class ConviteRepositorio extends AgregadoRepositorioAbstrato<ConviteIdentificador, Convite, Long, ConviteJpa>
		implements IConviteRepositorio {
	private final IConviteRepositorioJpa repositorioJpa;

	public ConviteRepositorio(IConviteRepositorioJpa repositorioJpa) {
		super(repositorioJpa, ConviteServico.IDENTIFICADOR_INEXISTENTE_MODELO);

		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected ConviteJpa paraJpa(Convite agregado) {
		Objects.requireNonNull(agregado);

		var identificador = agregado.getIdentificador().map(this::paraIdentificadorJpa).orElse(null);
		return new ConviteJpa(identificador, agregado.getEmailInstitucional().toString(), agregado.getPapel(),
				agregado.getCurso(), agregado.getCodigo().toString(), agregado.getEnviadoEm(), agregado.getValidoAte(),
				agregado.getSituacao());
	}

	@Override
	protected Convite paraAgregado(ConviteJpa agregadoJpa) {
		Objects.requireNonNull(agregadoJpa);

		var identificador = new ConviteIdentificador(Objects.requireNonNull(agregadoJpa.getIdentificador()).toString());
		return new Convite(Optional.of(identificador), new ConviteEmail(agregadoJpa.getEmail()), agregadoJpa.getPapel(),
				agregadoJpa.getCurso(), new ConviteCodigo(agregadoJpa.getCodigo()), agregadoJpa.getEnviadoEm(),
				agregadoJpa.getValidoAte(), agregadoJpa.getSituacao());
	}

	@Override
	protected Long paraIdentificadorJpa(ConviteIdentificador identificador) {
		Objects.requireNonNull(identificador);

		return converter(identificador).orElseThrow(() -> {
			var mensagem = ConviteServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador);
			return new AgregadoInexistenteExcecao(mensagem);
		});
	}

	private static Optional<Long> converter(ConviteIdentificador identificador) {
		try {
			return Optional.of(Long.valueOf(identificador.toString()));
		} catch (NumberFormatException excecao) {
			return Optional.empty();
		}
	}

	@Override
	public boolean existePorIdentificador(ConviteIdentificador identificador) {
		Objects.requireNonNull(identificador);

		return converter(identificador).isPresent() && super.existePorIdentificador(identificador);
	}

	@Override
	public boolean existePendentePorEmail(ConviteEmail emailInstitucional) {
		Objects.requireNonNull(emailInstitucional);

		return repositorioJpa.existePendentePorEmail(emailInstitucional.toString());
	}

	@Override
	public Optional<Convite> buscarPendentePorEmail(ConviteEmail emailInstitucional) {
		Objects.requireNonNull(emailInstitucional);

		return repositorioJpa.buscarPendentePorEmail(emailInstitucional.toString()).map(this::paraAgregado);
	}
}
