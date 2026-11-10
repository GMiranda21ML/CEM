# language: pt

Funcionalidade: Domínio de e-mail liberado para autocadastro de aluno

  Cenário: Coordenador libera um domínio de e-mail
    Dado que o domínio "aluno.cem.edu.br" não está liberado
    Quando o coordenador libera o domínio "aluno.cem.edu.br"
    Então o domínio "aluno.cem.edu.br" está liberado

  Cenário: Coordenador remove um domínio de e-mail liberado
    Dado que o domínio "pos.cem.edu.br" está liberado
    Quando o coordenador remove o domínio "pos.cem.edu.br"
    Então o domínio "pos.cem.edu.br" não está mais liberado

  Cenário: Apenas coordenador libera domínio de e-mail
    Dado que o domínio "aluno.cem.edu.br" não está liberado
    Quando um professor tenta liberar o domínio "aluno.cem.edu.br"
    Então a tentativa é recusada
