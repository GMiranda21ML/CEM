# language: pt

Funcionalidade: Emissão e validação de certificados

  Cenário: Emitir certificado de aluno com cinco sessões válidas
    Dado que o coordenador está autenticado
    E que o aluno "Ana" participou de 5 sessões realizadas da disciplina "Algoritmos" no período informado
    Quando o coordenador emite o certificado de aluno para "Ana"
    Então o certificado é emitido com 5 sessões e a carga horária efetiva
    E o certificado possui um código de verificação único

  Cenário: Não emitir certificado de aluno com quatro sessões válidas
    Dado que o coordenador está autenticado
    E que o aluno "Ana" participou de 4 sessões realizadas da disciplina "Algoritmos" no período informado
    Quando o coordenador tenta emitir o certificado de aluno para "Ana"
    Então a emissão do certificado é recusada

  Cenário: Emitir certificado de monitor pelas horas ministradas
    Dado que o coordenador está autenticado
    E que o monitor "Bruno" ministrou 150 minutos em sessões realizadas da disciplina "Algoritmos" no período informado
    Quando o coordenador emite o certificado de monitor para "Bruno"
    Então o certificado é emitido com carga horária de 150 minutos

  Cenário: Não emitir certificado sem atuação efetiva do monitor
    Dado que o coordenador está autenticado
    E que o monitor "Bruno" não ministrou tempo efetivo em sessão realizada no período informado
    Quando o coordenador tenta emitir o certificado de monitor para "Bruno"
    Então a emissão do certificado é recusada

  Cenário: Não emitir certificado por usuário não autorizado
    Dado que um aluno está autenticado
    E que o aluno "Ana" participou de 5 sessões realizadas da disciplina "Algoritmos" no período informado
    Quando o aluno tenta emitir seu próprio certificado
    Então a emissão do certificado é recusada

  Cenário: Não emitir certificado duplicado
    Dado que o coordenador está autenticado
    E que já existe certificado para "Ana", do tipo aluno, da disciplina "Algoritmos" e do período informado
    Quando o coordenador tenta emitir novamente o mesmo certificado
    Então a emissão do certificado é recusada

  Cenário: Sessões inválidas não contam para certificado
    Dado que o coordenador está autenticado
    E que o aluno "Ana" possui 4 presenças, 1 falta e 1 sessão cancelada na disciplina "Algoritmos"
    Quando o coordenador tenta emitir o certificado de aluno para "Ana"
    Então a emissão do certificado é recusada

  Cenário: Beneficiário consulta os próprios certificados
    Dado que existe certificado emitido para o aluno "Ana"
    Quando "Ana" consulta seus certificados
    Então apenas os certificados de "Ana" são apresentados

  Cenário: Validar publicamente código existente
    Dado que existe certificado com o código "CEM-VALIDO"
    Quando qualquer pessoa valida o código "CEM-VALIDO"
    Então são apresentados os dados públicos do certificado

  Cenário: Validar publicamente código inexistente
    Dado que não existe certificado com o código "CEM-INEXISTENTE"
    Quando qualquer pessoa valida o código "CEM-INEXISTENTE"
    Então é apresentada a informação "Código não encontrado"
