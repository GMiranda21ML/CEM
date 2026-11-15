# language: pt
Funcionalidade: pergunta

  Contexto:
    Dado o ambiente de pergunta preparado

  Cenário: Autor mantém sua pergunta pendente
    Quando edito minha pergunta enquanto está pendente
    Então a nova pergunta é preservada

  Cenário: Limite mínimo da dúvida
    Quando envio uma dúvida com apenas dois caracteres
    Então a dúvida curta é recusada

  Cenário: Resposta definitiva preservada
    Quando respondo uma pergunta e tento tratá-la novamente
    Então a primeira resposta permanece e as novas alterações são recusadas

  Cenário: Descarte de dúvida pendente
    Quando descarto uma dúvida e tento respondê-la depois
    Então a dúvida permanece descartada
