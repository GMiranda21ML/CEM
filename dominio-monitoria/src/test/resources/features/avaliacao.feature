# language: pt

Funcionalidade: Avaliação e feedback de sessões realizadas

  Cenário: Aluno avalia sessão da qual participou
    Dado que o aluno participou da sessão "S1" realizada há 2 dias
    E que o aluno ainda não avaliou a sessão "S1"
    Quando o aluno avalia a sessão "S1" com nota 5 e comentário "Excelente atendimento"
    Então a avaliação da sessão "S1" é registrada para o aluno

  Cenário: Monitor avalia turma de sessão que ministrou
    Dado que o monitor ministrou a sessão "S2" realizada há 1 dia
    E que o monitor ainda não avaliou a sessão "S2"
    Quando o monitor avalia a sessão "S2" com nota 4 e comentário "Turma participativa"
    Então a avaliação da sessão "S2" é registrada para o monitor

  Cenário: Não registrar nota menor que um
    Dado que o aluno participou da sessão "S1" realizada há 2 dias
    Quando o aluno tenta avaliar a sessão "S1" com nota 0
    Então a avaliação é recusada

  Cenário: Não registrar nota maior que cinco
    Dado que o aluno participou da sessão "S1" realizada há 2 dias
    Quando o aluno tenta avaliar a sessão "S1" com nota 6
    Então a avaliação é recusada

  Cenário: Não avaliar a mesma sessão duas vezes
    Dado que o aluno participou da sessão "S1" realizada há 2 dias
    E que o aluno já avaliou a sessão "S1"
    Quando o aluno tenta registrar outra avaliação para a sessão "S1"
    Então a avaliação é recusada

  Cenário: Não avaliar sessão não realizada
    Dado que o aluno está inscrito na sessão "S1" que ainda não foi realizada
    Quando o aluno tenta avaliar a sessão "S1"
    Então a avaliação é recusada

  Cenário: Aluno não avalia sessão em que faltou
    Dado que o aluno faltou à sessão "S1" realizada há 2 dias
    Quando o aluno tenta avaliar a sessão "S1"
    Então a avaliação é recusada

  Cenário: Usuário não avalia sessão sem participação
    Dado que o usuário não participou da sessão "S1" realizada há 2 dias
    Quando o usuário tenta avaliar a sessão "S1"
    Então a avaliação é recusada

  Cenário: Editar avaliação dentro do prazo
    Dado que o aluno avaliou a sessão "S1" realizada há 6 dias
    Quando o aluno altera sua avaliação para nota 4 e comentário "Bom atendimento"
    Então a avaliação da sessão "S1" passa a ter nota 4 e comentário "Bom atendimento"

  Cenário: Não editar avaliação fora do prazo
    Dado que o aluno avaliou a sessão "S1" realizada há 8 dias
    Quando o aluno tenta alterar sua avaliação
    Então a alteração da avaliação é recusada

  Cenário: Não criar avaliação fora do prazo
    Dado que o aluno participou da sessão "S1" realizada há 8 dias
    Quando o aluno tenta avaliar a sessão "S1"
    Então a avaliação é recusada

  Cenário: Consultar média das avaliações de uma sessão
    Dado que a sessão "S1" possui avaliações com notas 3 e 5
    Quando um participante consulta as avaliações da sessão "S1"
    Então a média apresentada é 4
