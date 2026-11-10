package br.com.cem.ui.backend.acesso.usuario;

import java.util.Objects;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.aplicacao.acesso.usuario.UsuarioAplicacaoServico;
import br.com.cem.aplicacao.acesso.usuario.UsuarioConsulta;
import br.com.cem.aplicacao.acesso.usuario.UsuarioProjecao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioAutorizacoes;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "acesso/usuario")
public class UsuarioControlador
		extends AgregadoControladorAbstrato<UsuarioIdentificador, Usuario, UsuarioAutorizacoes, UsuarioProjecao, UsuarioConsulta> {
	private final UsuarioServico servico;

	public UsuarioControlador(UsuarioServico servico, UsuarioAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);

		this.servico = Objects.requireNonNull(servico);
	}

	/** Autocadastro de aluno. Rota pública, sem autenticação. */
	@PostMapping("criar-conta")
	public void criarConta(@RequestBody Usuario agregado) {
		Objects.requireNonNull(agregado);

		servico.criarConta(agregado);
	}

	@PostMapping("{identificador}/inativar")
	public void inativar(@PathVariable UsuarioIdentificador identificador) {
		Objects.requireNonNull(identificador);

		servico.inativar(identificador);
	}

	@PostMapping("{identificador}/reativar")
	public void reativar(@PathVariable UsuarioIdentificador identificador) {
		Objects.requireNonNull(identificador);

		servico.reativar(identificador);
	}
}
