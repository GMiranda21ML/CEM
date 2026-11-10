# language: pt

Funcionalidade: Cadastro e manutenção de contas de usuário

  Cenário: Aluno cria a própria conta com e-mail de domínio liberado
    Dado que o domínio "aluno.cem.edu.br" está liberado
    Quando "lucas.ferreira@aluno.cem.edu.br" cria uma conta de aluno com o nome "Lucas Ferreira", o curso "Ciência da Computação" e uma senha forte
    Então uma conta de aluno é criada para "lucas.ferreira@aluno.cem.edu.br"

  Cenário: Aluno não cria conta com e-mail de domínio não liberado
    Dado que o domínio "gmail.com" não está liberado
    Quando "lucas.ferreira@gmail.com" tenta criar uma conta de aluno
    Então a tentativa é recusada

  Cenário: Não criar conta com e-mail já cadastrado
    Dado que "lucas.ferreira@aluno.cem.edu.br" já tem uma conta
    E que o domínio "aluno.cem.edu.br" está liberado
    Quando "lucas.ferreira@aluno.cem.edu.br" tenta criar outra conta de aluno
    Então a tentativa é recusada

  Cenário: Não criar conta com senha fraca
    Dado que o domínio "aluno.cem.edu.br" está liberado
    Quando "lucas.ferreira@aluno.cem.edu.br" tenta criar uma conta de aluno com a senha "12345678"
    Então a tentativa é recusada

  Cenário: Não criar conta diretamente com outro perfil
    Dado que o domínio "aluno.cem.edu.br" está liberado
    Quando "lucas.ferreira@aluno.cem.edu.br" tenta criar uma conta de coordenador
    Então a tentativa é recusada

  Cenário: Coordenador edita nome, curso e campus de um usuário
    Dado que existe um usuário aluno "Rafael Teixeira"
    Quando o coordenador altera o nome, o curso e o campus de "Rafael Teixeira"
    Então os dados de "Rafael Teixeira" são atualizados

  Cenário: Não alterar o e-mail de um usuário
    Dado que existe um usuário aluno "Rafael Teixeira"
    Quando o coordenador tenta alterar o e-mail de "Rafael Teixeira"
    Então a tentativa é recusada

  Cenário: Não alterar o perfil de um usuário
    Dado que existe um usuário aluno "Rafael Teixeira"
    Quando o coordenador tenta alterar o perfil de "Rafael Teixeira" para professor
    Então a tentativa é recusada

  Cenário: Coordenador inativa um usuário
    Dado que existe um usuário ativo "Rafael Teixeira"
    Quando o coordenador inativa "Rafael Teixeira"
    Então "Rafael Teixeira" está inativo

  Cenário: Coordenador reativa um usuário
    Dado que existe um usuário inativo "Rafael Teixeira"
    Quando o coordenador reativa "Rafael Teixeira"
    Então "Rafael Teixeira" está ativo

  Cenário: Coordenador exclui um usuário
    Dado que existe um usuário ativo "Rafael Teixeira"
    Quando o coordenador exclui "Rafael Teixeira"
    Então "Rafael Teixeira" não existe mais
