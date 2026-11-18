# language: pt
Funcionalidade: Feedback do aluno por sessão
  Contexto:
    Dado um monitor que conduziu uma sessão realizada com um aluno presente

  @F013 @RN002
  Cenário: Monitor registra feedback para aluno presente em sessão realizada
    Quando o monitor registra o feedback do aluno na sessão com nota e comentário
    Então o feedback é registrado com sucesso

  @F013 @RN002
  Cenário: Outro monitor não pode registrar feedback na sessão
    Quando outro monitor tenta registrar feedback na sessão
    Então a operação do feedback é recusada

  @F013 @RN002
  Cenário: Não é permitido registrar feedback em sessão futura, cancelada ou com falta do monitor
    Quando o monitor tenta registrar feedback em sessão ainda não realizada ou com falta do monitor
    Então a operação do feedback é recusada

  @F013 @RN002
  Cenário: Não é permitido registrar feedback para aluno com falta na sessão
    Quando o monitor tenta registrar feedback para um aluno que faltou à sessão
    Então a operação do feedback é recusada

  @F013 @RN002
  Cenário: Não é permitido mais de um feedback para o mesmo aluno na mesma sessão
    Quando o monitor tenta registrar um segundo feedback para o mesmo aluno na sessão
    Então a operação do feedback é recusada

  @F013 @RN002
  Cenário: Autor edita e exclui o feedback enquanto o acompanhamento está ativo
    Quando o autor edita a nota do feedback e depois o exclui com acompanhamento ativo
    Então o feedback é atualizado e posteriormente removido

  @F013 @RN002
  Cenário: Edição e exclusão de feedback são bloqueadas quando o acompanhamento está encerrado
    Quando o autor tenta editar ou excluir um feedback após o encerramento do acompanhamento
    Então a operação do feedback é recusada
