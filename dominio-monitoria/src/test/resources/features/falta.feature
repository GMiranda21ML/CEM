# language: pt
Funcionalidade: falta

  Contexto:
    Dado o ambiente de falta preparado

  Cenário: Falta única por pessoa e sessão
    Quando registro duas faltas da mesma pessoa na mesma sessão
    Então a segunda falta é recusada

  Cenário: Falta somente após a sessão
    Quando registro falta antes do encerramento da sessão
    Então a falta da sessão futura é recusada

  Cenário: Exclusão depende do cancelamento da reposição
    Quando excluo uma falta com reposição ativa
    Então a falta com reposição permanece registrada

  Cenário: Prazo de relato de ausência do monitor
    Quando um aluno relata ausência do monitor depois de trinta dias
    Então o relato fora do prazo é recusado

  Cenário: Terceira falta gera um alerta
    Quando registro a terceira falta e outra falta na mesma disciplina
    Então a coordenação recebe um único alerta novo

  Cenário: Coordenador cadastrado recebe o e-mail de recorrência
    Quando registro a terceira falta com coordenador responsável cadastrado
    Então o coordenador responsável recebe um e-mail com a pessoa e a disciplina

  Cenário: Falta sem coordenador cadastrado permanece registrada
    Quando registro a terceira falta sem coordenador responsável cadastrado
    Então a falta e o alerta são registrados sem envio de e-mail

  Cenário: Sugestão automática sem conflito
    Quando consulto sugestões na disponibilidade e depois com agenda ocupada
    Então somente os horários livres são sugeridos

  Cenário: Agendamento exige sugestão atual
    Quando agendo um horário fora das sugestões disponíveis
    Então nenhuma reposição é gravada

  Cenário: Duração positiva da sessão
    Quando cadastro uma sessão com início igual ao fim
    Então a sessão sem duração é recusada

  Cenário: Reposição vinculada à falta
    Quando agendo uma sugestão compatível para a falta
    Então a falta referencia a sessão de reposição pelo identificador
