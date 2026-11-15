# language: pt
Funcionalidade: faq

  Contexto:
    Dado o ambiente de faq preparado

  Cenário: Cadastro de pergunta repetida
    Quando cadastro novamente a mesma pergunta com espaços e maiúsculas
    Então o FAQ recusa a duplicidade

  Cenário: Resposta com mais de mil caracteres
    Quando cadastro uma resposta de 1001 caracteres
    Então o FAQ recusa a resposta longa

  Cenário: Aluno tenta gerenciar FAQ
    Quando um aluno tenta cadastrar uma entrada do FAQ
    Então o FAQ recusa o cadastro sem autorização

  Cenário: Consulta de perguntas frequentes
    Quando consulto as autorizações de leitura do FAQ
    Então a consulta ao FAQ é permitida
