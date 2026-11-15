# language: pt
@F009 @RN007
Funcionalidade: Disponibilidade semanal para reposição
  Contexto:
    Dado um aluno e a disponibilidade de outro monitor

  Cenário: Disponibilidade mantida pelo próprio monitor
    Quando o aluno tenta alterar o horário do outro monitor
    Então a alteração da disponibilidade é recusada

  Cenário: Intervalo de disponibilidade válido
    Quando informo disponibilidade com fim anterior ao início
    Então o intervalo de disponibilidade é recusado
