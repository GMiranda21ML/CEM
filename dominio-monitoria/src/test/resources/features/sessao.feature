# language: pt
@F009 @RN007
Funcionalidade: Agenda de suporte às reposições
  Contexto:
    Dado um aluno e uma sessão sob sua monitoria

  Cenário: Cadastro de sessão reservado à coordenação
    Quando o aluno tenta cadastrar uma sessão
    Então o cadastro da sessão é recusado

  Cenário: Cancelamento pelo monitor
    Quando o monitor cancela sua sessão
    Então a sessão permanece cadastrada e cancelada
