package br.com.cem.ui.backend.seguranca;

import java.util.Objects;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;

@Component
public class UsuarioDetailsServico implements UserDetailsService {
	private final IUsuarioRepositorio repositorio;

	public UsuarioDetailsServico(IUsuarioRepositorio repositorio) {
		this.repositorio = Objects.requireNonNull(repositorio);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Objects.requireNonNull(email);

		try {
			var usuario = repositorio.buscarPorEmail(new UsuarioEmail(email));
			return User.withUsername(usuario.getEmailInstitucional().toString())
					.password(usuario.getSenha().toString()).authorities(usuario.getPapel().name())
					.disabled(usuario.getSituacao() == UsuarioSituacao.INATIVO).build();
		} catch (AgregadoInexistenteExcecao | IllegalArgumentException excecao) {
			throw new UsernameNotFoundException("Usuário não encontrado para o e-mail " + email, excecao);
		}
	}
}
