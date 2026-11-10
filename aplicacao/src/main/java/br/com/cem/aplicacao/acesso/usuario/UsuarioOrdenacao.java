package br.com.cem.aplicacao.acesso.usuario;

import dev.sauloaraujo.d5.framework.aplicacao.IOrdenacao;

public enum UsuarioOrdenacao implements IOrdenacao<UsuarioProjecao> {
	NOME, EMAIL, PAPEL, SITUACAO
}
