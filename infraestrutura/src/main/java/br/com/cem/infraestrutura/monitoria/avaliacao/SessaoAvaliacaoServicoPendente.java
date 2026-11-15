package br.com.cem.infraestrutura.monitoria.avaliacao;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.SessaoAvaliavel;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.spi.ISessaoAvaliacaoServico;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class SessaoAvaliacaoServicoPendente implements ISessaoAvaliacaoServico {
	private final br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio sessoes;
	private final br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio faltas;
	private final br.com.cem.dominio.spi.IRelogioServico relogio;

	public SessaoAvaliacaoServicoPendente(
			br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio sessoes,
			br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio faltas,
			br.com.cem.dominio.spi.IRelogioServico relogio) {
		this.sessoes = Objects.requireNonNull(sessoes);
		this.faltas = Objects.requireNonNull(faltas);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	public SessaoAvaliavel buscarParaAvaliacao(
			SessaoIdentificador sessao, UsuarioIdentificador participante) {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(participante);
		var identificador =
				new br.com.cem.dominio.monitoria.sessao.SessaoIdentificador(sessao.toString());
		var encontrada = sessoes.buscarPorIdentificador(identificador);
		var ausente = faltas.existePorSessaoEPessoa(identificador, encontrada.getMonitor());
		var monitor = encontrada.getMonitor().equals(participante);
		return new SessaoAvaliavel(
				sessao,
				encontrada.realizada(relogio.agora()) && !ausente,
				encontrada.getFim(),
				monitor || encontrada.getAlunos().contains(participante),
				monitor
						? br.com.cem.dominio.monitoria.avaliacao.AvaliacaoPerfil.MONITOR
						: br.com.cem.dominio.monitoria.avaliacao.AvaliacaoPerfil.ALUNO);
	}
}
