package br.com.cem.dominio.monitoria.acompanhamento;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IObjetoValor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record RelatorioDesempenho(
		AcompanhamentoIdentificador acompanhamento,
		Optional<LocalDate> de,
		Optional<LocalDate> ate,
		boolean filtroIgnorado,
		int totalSessoesValidas,
		int totalPresencas,
		Optional<Double> frequenciaPercentual,
		int quantidadeNotas,
		Optional<Double> mediaNotas,
		Optional<Double> notaInicial,
		Optional<Double> notaAtual,
		Optional<Double> variacaoNotas,
		Optional<Double> inclinacaoTendencia,
		TendenciaEvolucao tendencia,
		SituacaoDesempenho situacao,
		List<SessaoRelatorioItem> sessoes)
		implements IObjetoValor {
	private static final double LIMIAR_TENDENCIA = 0.30;
	private static final double TOLERANCIA = 1e-9;

	public RelatorioDesempenho {
		Objects.requireNonNull(acompanhamento);
		Objects.requireNonNull(de);
		Objects.requireNonNull(ate);
		Objects.requireNonNull(frequenciaPercentual);
		Objects.requireNonNull(mediaNotas);
		Objects.requireNonNull(notaInicial);
		Objects.requireNonNull(notaAtual);
		Objects.requireNonNull(variacaoNotas);
		Objects.requireNonNull(inclinacaoTendencia);
		Objects.requireNonNull(tendencia);
		Objects.requireNonNull(situacao);
		sessoes = List.copyOf(Objects.requireNonNull(sessoes));
	}

	public static RelatorioDesempenho calcular(
			Acompanhamento acompanhamento,
			Collection<Sessao> sessoesDaDisciplina,
			Collection<Falta> faltasDaDisciplina,
			Collection<Feedback> feedbacksDoAluno,
			Optional<LocalDate> de,
			Optional<LocalDate> ate,
			LocalDateTime agora) {
		Objects.requireNonNull(acompanhamento);
		Objects.requireNonNull(sessoesDaDisciplina);
		Objects.requireNonNull(faltasDaDisciplina);
		Objects.requireNonNull(feedbacksDoAluno);
		Objects.requireNonNull(de);
		Objects.requireNonNull(ate);
		Objects.requireNonNull(agora);

		boolean filtroIgnorado = false;
		Optional<LocalDate> deEfetivo = de;
		Optional<LocalDate> ateEfetivo = ate;
		if (!acompanhamento.ativo()) {
			deEfetivo = Optional.empty();
			ateEfetivo = acompanhamento.getEncerradoEm();
		} else if (de.isPresent() && ate.isPresent() && de.get().isAfter(ate.get())) {
			filtroIgnorado = true;
			deEfetivo = Optional.empty();
			ateEfetivo = Optional.empty();
		}

		UsuarioIdentificador aluno = acompanhamento.getAluno();
		Map<SessaoIdentificador, Sessao> mapaSessoes = new HashMap<>();
		for (Sessao s : sessoesDaDisciplina) {
			s.getIdentificador().ifPresent(id -> mapaSessoes.put(id, s));
		}

		Set<SessaoIdentificador> faltasMonitor = new HashSet<>();
		Map<SessaoIdentificador, Falta> faltasAluno = new HashMap<>();
		Set<SessaoIdentificador> reposicoesAluno = new HashSet<>();
		for (Falta f : faltasDaDisciplina) {
			if (!f.deAluno()) {
				faltasMonitor.add(f.getSessao());
			} else if (f.getPessoa().equals(aluno)) {
				faltasAluno.put(f.getSessao(), f);
				f.getReposicao().ifPresent(reposicoesAluno::add);
			}
		}

		Map<SessaoIdentificador, Feedback> feedbacksPorSessao = new HashMap<>();
		for (Feedback fb : feedbacksDoAluno) {
			if (fb.getAluno().equals(aluno)) {
				feedbacksPorSessao.put(fb.getSessao(), fb);
			}
		}

		Optional<LocalDate> deFiltro = deEfetivo;
		Optional<LocalDate> ateFiltro = ateEfetivo;
		List<Sessao> sessoesFiltradas =
				sessoesDaDisciplina.stream()
						.filter(s -> s.getAlunos().contains(aluno))
						.filter(
								s -> {
									LocalDate data = s.getInicio().toLocalDate();
									return (deFiltro.isEmpty() || !data.isBefore(deFiltro.get()))
											&& (ateFiltro.isEmpty()
													|| !data.isAfter(ateFiltro.get()));
								})
						.sorted(
								Comparator.comparing(Sessao::getInicio)
										.thenComparing(Sessao::getFim))
						.toList();

		List<SessaoRelatorioItem> itens = new ArrayList<>();
		int validas = 0;
		int presencas = 0;
		List<Double> notas = new ArrayList<>();

		for (Sessao s : sessoesFiltradas) {
			Optional<SessaoIdentificador> sidOpt = s.getIdentificador();
			if (sidOpt.isEmpty()) {
				continue;
			}
			SessaoIdentificador sid = sidOpt.get();
			LocalDate dataSessao = s.getInicio().toLocalDate();

			String situacaoTexto;
			boolean valida;
			boolean presente;

			if (s.getCancelada()) {
				situacaoTexto = "Cancelada";
				valida = false;
				presente = false;
			} else if (!s.realizada(agora)) {
				situacaoTexto = "Agendada";
				valida = false;
				presente = false;
			} else if (faltasMonitor.contains(sid)) {
				situacaoTexto = "Falta do monitor";
				valida = false;
				presente = false;
			} else if (faltasAluno.containsKey(sid)) {
				Falta falta = Objects.requireNonNull(faltasAluno.get(sid));
				Optional<Sessao> reposicaoValida =
						falta.getReposicao()
								.flatMap(repId -> Optional.ofNullable(mapaSessoes.get(repId)))
								.filter(
										rep -> {
											SessaoIdentificador repId =
													rep.getIdentificador().orElseThrow();
											return rep.realizada(agora)
													&& !faltasMonitor.contains(repId)
													&& !faltasAluno.containsKey(repId);
										});
				if (reposicaoValida.isPresent()) {
					LocalDate dataRep = reposicaoValida.get().getInicio().toLocalDate();
					situacaoTexto =
							String.format(
									"Falta reposta em %02d/%02d",
									dataRep.getDayOfMonth(), dataRep.getMonthValue());
					valida = false;
					presente = false;
				} else {
					situacaoTexto = "Falta do aluno";
					valida = true;
					presente = false;
				}
			} else {
				situacaoTexto =
						reposicoesAluno.contains(sid) ? "Presença · reposição" : "Presença";
				valida = true;
				presente = true;
			}

			if (valida) {
				validas++;
				if (presente) {
					presencas++;
				}
			}

			Optional<Feedback> fb = Optional.ofNullable(feedbacksPorSessao.get(sid));
			fb.ifPresent(item -> notas.add(item.getNota()));

			itens.add(
					new SessaoRelatorioItem(
							sid,
							dataSessao,
							situacaoTexto,
							valida,
							presente,
							fb.map(Feedback::getNota),
							fb.flatMap(Feedback::getComentario)));
		}

		Optional<Double> frequenciaPercentual =
				validas > 0 ? Optional.of((presencas * 100.0) / validas) : Optional.empty();

		int quantidadeNotas = notas.size();
		Optional<Double> mediaNotas =
				quantidadeNotas > 0
						? Optional.of(
								notas.stream()
										.mapToDouble(Double::doubleValue)
										.average()
										.orElseThrow())
						: Optional.empty();
		Optional<Double> notaInicial =
				quantidadeNotas >= 4
						? Optional.of((notas.get(0) + notas.get(1)) / 2.0)
						: Optional.empty();
		Optional<Double> notaAtual =
				quantidadeNotas >= 4
						? Optional.of(
								(notas.get(quantidadeNotas - 2)
												+ notas.get(quantidadeNotas - 1))
										/ 2.0)
						: Optional.empty();
		Optional<Double> variacaoNotas =
				notaInicial.isPresent() && notaAtual.isPresent()
						? Optional.of(notaAtual.get() - notaInicial.get())
						: Optional.empty();

		Optional<Double> inclinacao = Optional.empty();
		TendenciaEvolucao tendencia = TendenciaEvolucao.DADOS_INSUFICIENTES;
		if (quantidadeNotas >= 3) {
			double mx = (quantidadeNotas + 1) / 2.0;
			double my = mediaNotas.orElseThrow();
			double num = 0.0;
			double den = 0.0;
			for (int i = 0; i < quantidadeNotas; i++) {
				double dx = (i + 1) - mx;
				num += dx * (notas.get(i) - my);
				den += dx * dx;
			}
			double slope = num / den;
			inclinacao = Optional.of(slope);
			if (slope >= LIMIAR_TENDENCIA - TOLERANCIA) {
				tendencia = TendenciaEvolucao.EM_EVOLUCAO;
			} else if (slope <= -LIMIAR_TENDENCIA + TOLERANCIA) {
				tendencia = TendenciaEvolucao.EM_QUEDA;
			} else {
				tendencia = TendenciaEvolucao.ESTAVEL;
			}
		}

		SituacaoDesempenho situacao =
				classificarSituacao(
						acompanhamento,
						validas,
						frequenciaPercentual,
						notaAtual,
						variacaoNotas,
						tendencia);

		return new RelatorioDesempenho(
				acompanhamento.getIdentificador().orElseThrow(),
				deEfetivo,
				ateEfetivo,
				filtroIgnorado,
				validas,
				presencas,
				frequenciaPercentual,
				quantidadeNotas,
				mediaNotas,
				notaInicial,
				notaAtual,
				variacaoNotas,
				inclinacao,
				tendencia,
				situacao,
				itens);
	}

	private static SituacaoDesempenho classificarSituacao(
			Acompanhamento acompanhamento,
			int validas,
			Optional<Double> frequenciaPercentual,
			Optional<Double> notaAtual,
			Optional<Double> variacaoNotas,
			TendenciaEvolucao tendencia) {
		if (validas < 3) {
			return SituacaoDesempenho.DADOS_INSUFICIENTES;
		}
		if (notaAtual.isPresent()
				&& notaAtual.get() >= acompanhamento.getMetaNota()
				&& frequenciaPercentual.isPresent()
				&& frequenciaPercentual.get() >= acompanhamento.getMetaFrequencia()) {
			return SituacaoDesempenho.META_ATINGIDA;
		}
		if ((frequenciaPercentual.isPresent()
						&& frequenciaPercentual.get() < acompanhamento.getMetaFrequencia())
				|| (variacaoNotas.isPresent() && variacaoNotas.get() < 0.0)
				|| tendencia == TendenciaEvolucao.EM_QUEDA) {
			return SituacaoDesempenho.ATENCAO;
		}
		if ((variacaoNotas.isPresent() && variacaoNotas.get() > 0.0)
				|| tendencia == TendenciaEvolucao.EM_EVOLUCAO) {
			return SituacaoDesempenho.EM_PROGRESSO;
		}
		return SituacaoDesempenho.ESTAVEL;
	}
}
