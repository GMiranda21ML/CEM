# language: pt
Funcionalidade: Candidatura e aprovação de monitor
  Contexto:
    Dado uma seleção de monitoria aberta pelo professor responsável

  @F006 @RN001
  Cenário: Aluno elegível se candidata à seleção do seu curso
    Quando um aluno elegível se candidata informando CR dentro do mínimo
    Então a candidatura é registrada com a situação pendente

  @F006 @RN001
  Cenário: Aluno de outro curso não pode se candidatar
    Quando um aluno de outro curso tenta se candidatar à seleção
    Então a operação da candidatura é recusada

  @F006 @RN001
  Cenário: Aluno com CR abaixo do mínimo não pode se candidatar
    Quando um aluno informa CR inferior ao mínimo da seleção
    Então a operação da candidatura é recusada

  @F006 @RN001
  Cenário: Aluno com duas monitorias ativas no semestre não pode se candidatar
    Quando um aluno com duas monitorias ativas no semestre tenta se candidatar
    Então a operação da candidatura é recusada

  @F006 @RN002
  Cenário: Aluno não pode possuir mais de uma candidatura ativa na mesma disciplina
    Quando um aluno já com candidatura pendente ou aprovada na disciplina tenta se candidatar novamente
    Então a operação da candidatura é recusada

  @F006 @RN006
  Cenário: Candidatura após o prazo de inscrição é recusada
    Quando um aluno tenta se candidatar após o término do prazo de inscrição
    Então a operação da candidatura é recusada

  @F006 @RN002
  Cenário: Aluno cancela a própria candidatura pendente
    Quando o aluno cancela sua candidatura pendente
    Então a candidatura fica com a situação cancelada

  @F006 @RN002
  Cenário: Não é possível cancelar candidatura já decidida
    Quando o aluno tenta cancelar uma candidatura que não está pendente
    Então a operação da candidatura é recusada

  @F006 @RN003 @RN005
  Cenário: Professor responsável registra entrevista da candidatura
    Quando o professor responsável registra a nota da entrevista do candidato
    Então a entrevista e o resultado final ponderado são gravados na candidatura

  @F006 @RN005
  Cenário: Outro professor não pode avaliar a candidatura
    Quando outro professor tenta registrar entrevista ou avaliar a candidatura
    Então a operação da candidatura é recusada

  @F006 @RN004
  Cenário: Aprovação é bloqueada enquanto as inscrições estiverem abertas
    Quando o professor tenta aprovar candidato antes do fim do prazo de inscrição
    Então a operação da candidatura é recusada

  @F006 @RN003 @RN004
  Cenário: Aprovação é bloqueada se houver candidato pendente sem entrevista
    Quando o professor tenta aprovar um candidato enquanto outro pendente está sem entrevista
    Então a operação da candidatura é recusada

  @F006 @RN003
  Cenário: Aprovação é bloqueada se resultado final estiver abaixo do patamar
    Quando o professor tenta aprovar um candidato com resultado abaixo do patamar mínimo
    Então a operação da candidatura é recusada

  @F006 @RN004
  Cenário: Aprovação deve respeitar a ordem de classificação e desempate
    Quando o professor tenta aprovar um candidato havendo outro mais bem classificado pendente
    Então a operação da candidatura é recusada

  @F006 @RN004
  Cenário: Aprovação preenche vagas, encerra seleção e reprova pendentes
    Quando o professor aprova os candidatos melhores classificados até preencher as vagas
    Então os aprovados assumem a vaga, a seleção é encerrada e os demais pendentes são reprovados
