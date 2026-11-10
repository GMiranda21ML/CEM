package br.com.cem.ui.backend.seguranca;

import java.util.Objects;
import java.util.Optional;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.dominio.acesso.convite.ConviteServico;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailServico;
import br.com.cem.dominio.acesso.usuario.UsuarioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.ui.Backend;
import dev.sauloaraujo.d5.framework.ui.seguranca.AutenticacaoControladorAbstrato;

@RestController
@RequestMapping(Backend.PREFIXO + "autenticacao")
public class AutenticacaoControlador extends AutenticacaoControladorAbstrato {
	private final DominioEmailServico dominioEmailServico;
	private final UsuarioServico usuarioServico;
	private final ConviteServico conviteServico;

	public AutenticacaoControlador(ISegurancaServico segurancaServico, DominioEmailServico dominioEmailServico,
			UsuarioServico usuarioServico, ConviteServico conviteServico) {
		super(segurancaServico);

		this.dominioEmailServico = Objects.requireNonNull(dominioEmailServico);
		this.usuarioServico = Objects.requireNonNull(usuarioServico);
		this.conviteServico = Objects.requireNonNull(conviteServico);
	}

	@Override
	protected AutenticacaoRecurso recurso() {
		var dominioEmail = dominioEmailServico.criarAutorizacoes(Optional.empty());
		var usuarios = usuarioServico.criarAutorizacoes(Optional.empty());
		var convite = conviteServico.criarAutorizacoes(Optional.empty());
		return new AutenticacaoRecurso(segurancaServico.getUsuario(), dominioEmail, usuarios, convite);
	}
}
