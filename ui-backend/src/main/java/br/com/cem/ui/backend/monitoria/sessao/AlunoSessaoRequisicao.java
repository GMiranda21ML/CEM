package br.com.cem.ui.backend.monitoria.sessao;

import org.apache.commons.lang3.Validate;

public record AlunoSessaoRequisicao(String aluno) {
	public AlunoSessaoRequisicao {
		Validate.notBlank(aluno, "Aluno obrigatório");
	}
}
