# language: pt
Funcionalidade: Acompanhamento e relatório de desempenho do aluno
  Contexto:
    Dado um monitor ativo e um aluno do mesmo curso da disciplina

  @F013 @RN001
  Cenário: Monitor ativo abre acompanhamento para aluno elegível
    Quando o monitor abre o acompanhamento definindo metas de nota e frequência
    Então o acompanhamento é registrado como ativo

  @F013 @RN001
  Cenário: Usuário sem vínculo de monitor ativo não pode abrir acompanhamento
    Quando um aluno sem vínculo de monitor ativo na disciplina tenta abrir acompanhamento
    Então a operação do acompanhamento é recusada

  @F013 @RN001
  Cenário: Monitor não pode abrir acompanhamento para si mesmo
    Quando o monitor tenta abrir acompanhamento para si mesmo
    Então a operação do acompanhamento é recusada

  @F013 @RN001
  Cenário: Não é permitido mais de um acompanhamento ativo para o mesmo aluno na disciplina
    Quando o monitor tenta abrir um segundo acompanhamento ativo para o mesmo aluno na disciplina
    Então a operação do acompanhamento é recusada

  @F013 @RN001
  Cenário: Metas fora dos limites permitidos são recusadas
    Quando o monitor tenta abrir acompanhamento com meta de frequência menor que cinquenta por cento
    Então a operação do acompanhamento é recusada

  @F013 @RN001
  Cenário: Monitor ou professor edita as metas de acompanhamento ativo
    Quando o professor da disciplina altera as metas do acompanhamento ativo
    Então as novas metas do acompanhamento são atualizadas

  @F013 @RN001
  Cenário: Encerramento exige parecer final com pelo menos vinte caracteres
    Quando o monitor tenta encerrar o acompanhamento com parecer curto e depois com parecer válido
    Então o parecer curto é recusado e o parecer válido encerra o acompanhamento

  @F013 @RN001
  Cenário: Exclusão de acompanhamento sem feedbacks é permitida e bloqueada quando há feedback
    Quando o monitor tenta excluir acompanhamento com feedback e depois exclui acompanhamento sem feedback
    Então a exclusão com feedback é recusada e a sem feedback é concluída

  @F013 @RN003 @RN004 @RN005 @RN006
  Cenário: Relatório calcula frequência com reposição, notas, tendência e situação
    Quando o relatório é emitido com sessões realizadas, falta reposta e feedbacks crescentes
    Então o relatório calcula corretamente a frequência, nota inicial, nota atual, tendência em evolução e situação meta atingida

  @F013 @RN007
  Cenário: Acesso ao relatório respeita o perfil do usuário
    Quando um aluno não vinculado tenta consultar o relatório de outro aluno
    Então o acesso ao relatório é recusado
