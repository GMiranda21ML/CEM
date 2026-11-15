# language: pt
@F009 @RN004
Funcionalidade: Acompanhamento de alertas de faltas
  Contexto:
    Dado um alerta de três faltas ainda não acompanhado

  Cenário: Coordenação acompanha o alerta
    Quando a coordenação acompanha o alerta
    Então o alerta fica acompanhado

  Cenário: Aluno não acompanha alertas da coordenação
    Quando um aluno tenta acompanhar o alerta
    Então o acompanhamento é recusado

  Cenário: Coordenador de outro curso não acessa o alerta
    Quando um coordenador sem responsabilidade tenta consultar e acompanhar o alerta
    Então a consulta e o acompanhamento são recusados
