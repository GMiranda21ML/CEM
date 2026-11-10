# language: pt

Funcionalidade: Convite de coordenador e de professor

  Cenário: Administrador convida um coordenador
    Quando o administrador convida "beatriz.lopes@cem.edu.br" para coordenador do curso "Engenharia Civil"
    Então existe um convite para "beatriz.lopes@cem.edu.br" aguardando aceite
    E o convite tem um código de verificação de 6 dígitos válido por 7 dias

  Cenário: Administrador não pode convidar um professor
    Quando o administrador tenta convidar "rodrigo.alves@cem.edu.br" para professor do curso "Ciência da Computação"
    Então a tentativa é recusada

  Cenário: Coordenador convida um professor do seu curso
    Quando o coordenador convida "rodrigo.alves@cem.edu.br" para professor do curso "Ciência da Computação"
    Então existe um convite para "rodrigo.alves@cem.edu.br" aguardando aceite

  Cenário: Coordenador não pode convidar outro coordenador
    Quando o coordenador tenta convidar "novo.coordenador@cem.edu.br" para coordenador do curso "Ciência da Computação"
    Então a tentativa é recusada

  Cenário: Não convidar um e-mail que já tem conta
    Dado que "marta.siqueira@cem.edu.br" já tem uma conta
    Quando o administrador tenta convidar "marta.siqueira@cem.edu.br" para coordenador do curso "Ciência da Computação"
    Então a tentativa é recusada

  Cenário: Reenviar um convite aguardando aceite
    Dado que existe um convite para "beatriz.lopes@cem.edu.br" aguardando aceite com código "123456"
    Quando o convite de "beatriz.lopes@cem.edu.br" é reenviado
    Então o convite de "beatriz.lopes@cem.edu.br" tem um código de verificação diferente de "123456", válido por mais 7 dias

  Cenário: Cancelar um convite aguardando aceite
    Dado que existe um convite para "beatriz.lopes@cem.edu.br" aguardando aceite
    Quando o convite de "beatriz.lopes@cem.edu.br" é cancelado
    Então o convite de "beatriz.lopes@cem.edu.br" está cancelado

  Cenário: Não cancelar um convite já aceito
    Dado que existe um convite para "marta.siqueira@cem.edu.br" aceito
    Quando tenta-se cancelar o convite de "marta.siqueira@cem.edu.br"
    Então a tentativa é recusada

  Cenário: Aceitar um convite com e-mail e código corretos
    Dado que existe um convite para "beatriz.lopes@cem.edu.br" aguardando aceite com código "423037"
    Quando "beatriz.lopes@cem.edu.br" aceita o convite com o código "423037", o nome "Beatriz Lopes" e uma senha forte
    Então uma conta é criada para "beatriz.lopes@cem.edu.br"
    E o convite de "beatriz.lopes@cem.edu.br" está aceito

  Cenário: Não aceitar um convite com código incorreto
    Dado que existe um convite para "beatriz.lopes@cem.edu.br" aguardando aceite com código "423037"
    Quando "beatriz.lopes@cem.edu.br" tenta aceitar o convite com o código "000000"
    Então a tentativa é recusada

  Cenário: Não aceitar um convite expirado
    Dado que existe um convite para "beatriz.lopes@cem.edu.br" aguardando aceite com código "423037" enviado há 8 dias
    Quando "beatriz.lopes@cem.edu.br" tenta aceitar o convite com o código "423037"
    Então a tentativa é recusada

  Cenário: Não aceitar um convite já cancelado
    Dado que existe um convite para "beatriz.lopes@cem.edu.br" cancelado
    Quando "beatriz.lopes@cem.edu.br" tenta aceitar o convite
    Então a tentativa é recusada
