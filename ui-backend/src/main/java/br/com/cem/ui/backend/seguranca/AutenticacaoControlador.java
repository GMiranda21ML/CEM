package br.com.cem.ui.backend.seguranca;

import java.util.Objects;
import java.util.Optional;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.dominio.acesso.convite.ConviteServico;
import br.com.cem.dominio.acesso.campus.CampusServico;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailServico;
import br.com.cem.dominio.acesso.usuario.UsuarioServico;
import br.com.cem.dominio.comunicacao.certificado.CertificadoServico;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.ui.Backend;
import dev.sauloaraujo.d5.framework.ui.seguranca.AutenticacaoControladorAbstrato;

@RestController
@RequestMapping(Backend.PREFIXO + "autenticacao")
public class AutenticacaoControlador extends AutenticacaoControladorAbstrato {
	private final DominioEmailServico dominioEmailServico;
	private final UsuarioServico usuarioServico;
	private final ConviteServico conviteServico;
	private final CampusServico campusServico;
	private final AvaliacaoServico avaliacaoServico;
	private final CertificadoServico certificadoServico;

	public AutenticacaoControlador(ISegurancaServico segurancaServico, DominioEmailServico dominioEmailServico,
			UsuarioServico usuarioServico, ConviteServico conviteServico, CampusServico campusServico,
			AvaliacaoServico avaliacaoServico, CertificadoServico certificadoServico) {
		super(segurancaServico);

		this.dominioEmailServico = Objects.requireNonNull(dominioEmailServico);
		this.usuarioServico = Objects.requireNonNull(usuarioServico);
		this.conviteServico = Objects.requireNonNull(conviteServico);
		this.campusServico = Objects.requireNonNull(campusServico);
		this.avaliacaoServico = Objects.requireNonNull(avaliacaoServico);
		this.certificadoServico = Objects.requireNonNull(certificadoServico);
	}

	@Override
	protected AutenticacaoRecurso recurso() {
		var dominioEmail = dominioEmailServico.criarAutorizacoes(Optional.empty());
		var usuarios = usuarioServico.criarAutorizacoes(Optional.empty());
		var convite = conviteServico.criarAutorizacoes(Optional.empty());
		var campus = campusServico.criarAutorizacoes(Optional.empty());
		var avaliacao = avaliacaoServico.criarAutorizacoes(Optional.empty());
		var certificado = certificadoServico.criarAutorizacoes(Optional.empty());
		return new AutenticacaoRecurso(segurancaServico.getUsuario(), segurancaServico.getAutorizacoes(), dominioEmail,
				usuarios, convite, campus, avaliacao, certificado);
	}
}
