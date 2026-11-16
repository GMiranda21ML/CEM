# language: pt
@F007 @RN001 @RN002
Funcionalidade: Disponibilidade semanal para reposição
  Contexto:
    Dado um aluno e a disponibilidade de outro monitor

  Cenário: Disponibilidade mantida pelo próprio monitor
    Quando o aluno tenta alterar o horário do outro monitor
    Então a alteração da disponibilidade é recusada

  Cenário: Intervalo de disponibilidade válido
    Quando informo disponibilidade com fim anterior ao início
    Então o intervalo de disponibilidade é recusado

  Cenário: Monitor mantém a própria disponibilidade
    Quando o monitor tenta incluir a própria disponibilidade
    Então a disponibilidade é incluída

  Cenário: Campus inativo não pode ser usado
    Quando tento incluir disponibilidade em campus inativo
    Então a inclusão da disponibilidade é recusada

  Cenário: Horário fora do funcionamento não pode ser cadastrado
    Quando tento incluir disponibilidade antes das 7 horas
    Então a inclusão da disponibilidade é recusada

  Cenário: Horários do mesmo dia não podem se sobrepor
    Quando tento incluir horário que se sobrepõe a outro
    Então a inclusão da disponibilidade é recusada

  Cenário: Carga semanal não pode ultrapassar vinte horas
    Quando tento ultrapassar vinte horas semanais
    Então a inclusão da disponibilidade é recusada
