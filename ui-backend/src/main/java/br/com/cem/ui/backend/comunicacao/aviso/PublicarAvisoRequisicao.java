package br.com.cem.ui.backend.comunicacao.aviso;

import java.time.LocalDate;

public record PublicarAvisoRequisicao(String titulo, String texto, LocalDate validade) {}
