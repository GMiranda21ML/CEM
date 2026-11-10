package br.com.cem.infraestrutura.acesso.usuario;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioServico;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class UsuarioRepositorio extends AgregadoRepositorioAbstrato<UsuarioIdentificador, Usuario, Long, UsuarioJpa>
		implements IUsuarioRepositorio {
	private final IUsuarioRepositorioJpa repositorioJpa;

	public UsuarioRepositorio(IUsuarioRepositorioJpa repositorioJpa) {
		super(repositorioJpa, UsuarioServico.IDENTIFICADOR_INEXISTENTE_MODELO);

		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected UsuarioJpa paraJpa(Usuario agregado) {
		Objects.requireNonNull(agregado);

		var identificador = agregado.getIdentificador().map(this::paraIdentificadorJpa).orElse(null);
		return new UsuarioJpa(identificador, agregado.getNome(), agregado.getEmailInstitucional().toString(),
				agregado.getSenha().toString(), agregado.getPapel(), agregado.getCurso(), agregado.getCampus(),
				agregado.getSituacao());
	}

	@Override
	protected Usuario paraAgregado(UsuarioJpa agregadoJpa) {
		Objects.requireNonNull(agregadoJpa);

		var identificador = new UsuarioIdentificador(Objects.requireNonNull(agregadoJpa.getIdentificador()).toString());
		return new Usuario(Optional.of(identificador), agregadoJpa.getNome(), new UsuarioEmail(agregadoJpa.getEmail()),
				new UsuarioSenha(agregadoJpa.getSenha()), agregadoJpa.getPapel(), agregadoJpa.getCurso(),
				agregadoJpa.getCampus(), agregadoJpa.getSituacao());
	}

	@Override
	protected Long paraIdentificadorJpa(UsuarioIdentificador identificador) {
		Objects.requireNonNull(identificador);

		return converter(identificador).orElseThrow(() -> {
			var mensagem = UsuarioServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador);
			return new AgregadoInexistenteExcecao(mensagem);
		});
	}

	private static Optional<Long> converter(UsuarioIdentificador identificador) {
		try {
			return Optional.of(Long.valueOf(identificador.toString()));
		} catch (NumberFormatException excecao) {
			return Optional.empty();
		}
	}

	@Override
	public boolean existePorIdentificador(UsuarioIdentificador identificador) {
		Objects.requireNonNull(identificador);

		return converter(identificador).isPresent() && super.existePorIdentificador(identificador);
	}

	@Override
	public boolean existePorEmail(UsuarioEmail emailInstitucional, Optional<UsuarioIdentificador> identificadorIgnorado) {
		Objects.requireNonNull(emailInstitucional);
		Objects.requireNonNull(identificadorIgnorado);

		var identificadorIgnoradoJpa = identificadorIgnorado.map(this::paraIdentificadorJpa).orElse(null);
		return repositorioJpa.existePorEmail(emailInstitucional.toString(), identificadorIgnoradoJpa);
	}

	@Override
	public Usuario buscarPorEmail(UsuarioEmail emailInstitucional) {
		Objects.requireNonNull(emailInstitucional);

		var encontrado = repositorioJpa.findByEmailIgnoreCase(emailInstitucional.toString());
		if (encontrado.isEmpty()) {
			var mensagem = UsuarioServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(emailInstitucional);
			throw new AgregadoInexistenteExcecao(mensagem);
		}
		return paraAgregado(encontrado.get());
	}
}
