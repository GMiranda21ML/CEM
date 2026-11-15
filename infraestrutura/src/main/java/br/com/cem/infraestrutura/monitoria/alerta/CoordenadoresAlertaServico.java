package br.com.cem.infraestrutura.monitoria.alerta;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.alerta.spi.ICoordenadoresAlertaServico;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class CoordenadoresAlertaServico implements ICoordenadoresAlertaServico {
	private final IUsuarioRepositorio usuarios;
	private final IUsuarioRepositorioJpa usuariosJpa;

	public CoordenadoresAlertaServico(IUsuarioRepositorio usuarios, IUsuarioRepositorioJpa usuariosJpa) {
		this.usuarios = Objects.requireNonNull(usuarios);
		this.usuariosJpa = Objects.requireNonNull(usuariosJpa);
	}

	@Override
	public Collection<Usuario> buscarPorPessoa(UsuarioIdentificador pessoa) {
		var faltoso = usuarios.buscarPorIdentificador(Objects.requireNonNull(pessoa));
		var curso = faltoso.getCurso();
		if (curso == null || curso.isBlank()) return List.of();
		var dominio = faltoso.getEmailInstitucional().getDominio();
		return usuariosJpa.buscarPorCursoEPapelESituacao(
						curso.strip().toLowerCase(Locale.ROOT), Autorizacao.COORDENADOR, UsuarioSituacao.ATIVO)
				.stream()
				.filter(u -> {
					var institucional = new UsuarioEmail(u.getEmail()).getDominio();
					return dominio.equals(institucional) || dominio.endsWith("." + institucional);
				})
				.map(u -> usuarios.buscarPorIdentificador(new UsuarioIdentificador(
						Objects.requireNonNull(u.getIdentificador()).toString())))
				.toList();
	}
}
