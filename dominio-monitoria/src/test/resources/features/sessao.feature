# language: pt
Funcionalidade: Agenda e registro de sessões
  Contexto:
    Dado um aluno e uma sessão sob sua monitoria

  @F009 @RN007
  Cenário: Cadastro de sessão reservado à coordenação
    Quando o aluno tenta cadastrar uma sessão
    Então o cadastro da sessão é recusado

  @F009 @RN007
  Cenário: Cancelamento pelo monitor
    Quando o monitor cancela sua sessão
    Então a sessão permanece cadastrada e cancelada

  @F008 @RN001
  Cenário: Monitor cria a própria sessão
    Quando o monitor registra uma nova sessão
    Então a sessão é cadastrada com participantes sem resposta

  @F008 @RN004
  Cenário: Aluno incluído recebe convite sem resposta
    Quando o monitor inclui um aluno elegível na sessão
    Então o aluno fica sem resposta e recebe uma notificação

  @F008 @RN005
  Cenário: Aluno confirma presença antes da sessão
    Quando o aluno confirma participação antes do início
    Então a resposta fica como participação confirmada

  @F008 @RN005
  Cenário: Aluno não pode responder depois do início
    Quando o aluno tenta responder depois do início
    Então a resposta é recusada

  @F008 @RN006
  Cenário: Remarcação preserva duração e reinicia confirmações
    Quando o monitor remarca a sessão para uma sugestão de aluno
    Então o novo horário mantém a duração e as respostas voltam para sem resposta

  @F008 @RN003
  Cenário: Remarcação com conflito é recusada
    Quando o monitor remarca a sessão para um horário conflitante
    Então a remarcação é recusada
