package br.com.cem.ui.backend.acesso.convite;

import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.aplicacao.acesso.convite.ConviteAplicacaoServico;
import br.com.cem.aplicacao.acesso.convite.ConviteConsulta;
import br.com.cem.aplicacao.acesso.convite.ConviteProjecao;
import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteAutorizacoes;
import br.com.cem.dominio.acesso.convite.ConviteCodigo;
import br.com.cem.dominio.acesso.convite.ConviteEmail;
import br.com.cem.dominio.acesso.convite.ConviteIdentificador;
import br.com.cem.dominio.acesso.convite.ConviteServico;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "acesso/convite")
public class ConviteControlador extends
		AgregadoControladorAbstrato<ConviteIdentificador, Convite, ConviteAutorizacoes, ConviteProjecao, ConviteConsulta> {
	private final ConviteServico servico;

	public ConviteControlador(ConviteServico servico, ConviteAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);

		this.servico = Objects.requireNonNull(servico);
	}

	@PostMapping("convidar")
	public Convite convidar(@RequestBody ConvidarRequisicao requisicao) {
		Objects.requireNonNull(requisicao);

		return servico.convidar(new ConviteEmail(requisicao.email()), requisicao.papel(), requisicao.curso());
	}

	@PostMapping("{identificador}/reenviar")
	public void reenviar(@PathVariable ConviteIdentificador identificador) {
		Objects.requireNonNull(identificador);

		servico.reenviar(identificador);
	}

	@PostMapping("{identificador}/cancelar")
	public void cancelar(@PathVariable ConviteIdentificador identificador) {
		Objects.requireNonNull(identificador);

		servico.cancelar(identificador);
	}

	/** Rota pública, sem autenticação. */
	@GetMapping("verificacao")
	public Convite verificar(@RequestParam String email, @RequestParam String codigo) {
		return servico.verificar(new ConviteEmail(email), new ConviteCodigo(codigo));
	}

	/** Rota pública, sem autenticação. */
	@PostMapping("aceitar")
	public Usuario aceitar(@RequestBody AceitarRequisicao requisicao) {
		Objects.requireNonNull(requisicao);

		return servico.aceitar(new ConviteEmail(requisicao.email()), new ConviteCodigo(requisicao.codigo()),
				requisicao.nome(), new UsuarioSenha(requisicao.senha()));
	}
}
