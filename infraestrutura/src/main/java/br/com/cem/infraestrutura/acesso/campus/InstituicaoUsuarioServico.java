package br.com.cem.infraestrutura.acesso.campus;

import java.util.Objects;

import org.springframework.stereotype.Component;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;

@Component
public final class InstituicaoUsuarioServico implements IInstituicaoUsuarioServico {
	private final IUsuarioRepositorio usuarioRepositorio;

	public InstituicaoUsuarioServico(IUsuarioRepositorio usuarioRepositorio) {
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
	}

	@Override
	public InstituicaoIdentificador buscarPorUsuario(String usuario) {
		Objects.requireNonNull(usuario);
		var cadastrado = usuarioRepositorio.buscarPorEmail(new UsuarioEmail(usuario));
		return new InstituicaoIdentificador(cadastrado.getEmailInstitucional().getDominio());
	}
}
