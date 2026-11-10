package br.com.cem.ui.backend.seguranca;

import java.util.Objects;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;

/** Garante que exista uma conta de administrador CEM, já que nenhum convite cria a primeira. */
@Component
public class AdministradorInicializador implements CommandLineRunner {
	private static final String EMAIL = "administrador@cem.edu.br";
	private static final String SENHA = "Cem@2026!";

	private final IUsuarioRepositorio repositorio;

	public AdministradorInicializador(IUsuarioRepositorio repositorio) {
		this.repositorio = Objects.requireNonNull(repositorio);
	}

	@Override
	public void run(String... args) {
		var email = new UsuarioEmail(EMAIL);
		if (!repositorio.existePorEmail(email, Optional.empty())) {
			var administrador = new Usuario(Optional.empty(), "Administrador CEM", email, new UsuarioSenha(SENHA),
					Autorizacao.ADMINISTRADOR, null, null, UsuarioSituacao.ATIVO);
			repositorio.incluir(administrador);
		}
	}
}
