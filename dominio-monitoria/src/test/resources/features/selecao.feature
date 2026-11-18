# language: pt
Funcionalidade: Seleção de monitoria
  Contexto:
    Dado um professor responsável por uma disciplina

  @F006 @RN006
  Cenário: Professor responsável abre uma seleção de monitoria
    Quando o professor abre uma seleção com critérios válidos para sua disciplina
    Então a seleção é aberta com a situação aberta

  @F006 @RN006
  Cenário: Outro professor não pode abrir seleção para disciplina alheia
    Quando outro professor tenta abrir uma seleção para a disciplina
    Então a operação da seleção é recusada

  @F006 @RN006
  Cenário: Não é permitido abrir segunda seleção aberta na mesma disciplina
    Quando o professor abre uma seleção e tenta abrir outra na mesma disciplina
    Então a operação da seleção é recusada

  @F006 @RN003 @RN006
  Cenário: Pesos que não somam cem por cento são recusados
    Quando o professor tenta abrir uma seleção com pesos somando diferente de cem por cento
    Então a operação da seleção é recusada

  @F006 @RN006
  Cenário: Prazo de inscrição anterior à data atual é recusado
    Quando o professor tenta abrir uma seleção com prazo anterior à data atual
    Então a operação da seleção é recusada

  @F006 @RN006
  Cenário: Professor responsável edita critérios antes da primeira candidatura
    Quando o professor edita os critérios da seleção sem candidaturas registradas
    Então os novos critérios da seleção são salvos

  @F006 @RN006
  Cenário: Edição de critérios é bloqueada após a primeira candidatura
    Quando o professor tenta editar uma seleção que já possui candidaturas
    Então a operação da seleção é recusada

  @F006 @RN004 @RN006
  Cenário: Encerramento manual da seleção reprova candidaturas pendentes
    Quando o professor encerra a seleção com candidatura pendente
    Então a seleção fica encerrada e a candidatura pendente é reprovada automaticamente
