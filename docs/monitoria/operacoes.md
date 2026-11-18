# Operações de faltas e reposição — F009

Todas as operações exigem autenticação; POST também exige CSRF. As pesquisas usam as operações padrão D5 com paginação e filtro `texto`. Os identificadores são cadeias de caracteres.

## Faltas

Tela: `/monitoria/falta/pesquisa`. GET `/backend/monitoria/falta/pesquisa` respeita a participação e a autoria: monitor e professor veem as faltas de suas sessões; a coordenação pode acompanhá-las. Alunos também consultam relatos de ausência do monitor nas sessões a que estão vinculados.

Sem busca, o frontend omite o parâmetro `texto`. Na pesquisa de faltas, `texto` ausente, vazio ou composto somente de espaços significa ausência de filtro, incluindo registros sem observação. Após registrar, editar ou excluir uma falta, a tela atualiza a tabela, os totais e as consultas de reposição. A tabela apresenta sessão, faltoso, autoria, observação opcional e a ação `Sugerir reposição` para faltas de aluno sem reposição ativa, conforme o protótipo.

POST `/backend/monitoria/falta/registrar`:
```json
{"sessao":"1","pessoa":"3","papel":"ALUNO","observacao":"Não compareceu."}
```

Para relatar ausência do monitor, o aluno usa a mesma operação com `papel` igual a `MONITOR` e a identidade do monitor da sessão. O relato aparece no cartão de sessão e impede a avaliação enquanto a ausência estiver registrada. O registro só é aceito após o encerramento da sessão, com prazo de trinta dias para o relato do aluno.

POST `/backend/monitoria/falta/1/editar` recebe `{"observacao":"Observação corrigida."}`. POST `/backend/monitoria/falta/1/excluir` exige autoria ou professor responsável e ausência de reposição ativa.

GET `/backend/monitoria/falta/1/sugestao` retorna até três opções:
```json
[{"inicio":"2026-12-01T14:00:00","fim":"2026-12-01T15:00:00","campus":"1"}]
```

POST `/backend/monitoria/falta/1/agendar` recebe uma dessas opções e a confere novamente sob bloqueio das agendas do monitor e aluno. A sessão de reposição é persistida e ambos recebem e-mail. POST `/backend/monitoria/sessao/2/cancelar` cancela a reposição e permite novo agendamento. Nenhum agendamento é feito sem a escolha de uma sugestão pelo monitor ou professor.

## Alertas

Tela: `/monitoria/alerta/pesquisa`, exclusiva da coordenação responsável. GET `/backend/monitoria/alerta/pesquisa` lista os alertas persistidos ao atingir três faltas por pessoa, papel e disciplina, sem duplicar um alerta ainda não acompanhado. A responsabilidade usa o curso cadastrado do faltoso e os coordenadores ativos desse curso, cujo domínio de e-mail institucional coincide com o do faltoso ou é superior a ele (por exemplo, `cem.edu.br` e `aluno.cem.edu.br`). Curso é comparado sem espaços nas extremidades e sem distinguir maiúsculas e minúsculas. Coordenadores de outros cursos ou instituições não recebem esses alertas na pesquisa nem podem acessar diretamente a contagem ou o acompanhamento.

Ao gerar um novo alerta, o sistema envia uma mensagem ao e-mail institucional de cada coordenador correspondente, usando o serviço SMTP existente. Novas faltas não reenviam o e-mail enquanto o alerta estiver pendente. Se não houver coordenador ativo ou curso informado, a falta e o alerta continuam registrados, sem destinatário arbitrário; o painel passa a apresentá-lo quando houver cadastro correspondente, sem envio retroativo. Alertas anteriores também seguem o escopo do cadastro atual, sem migração ou alteração das tabelas existentes.

GET `/backend/monitoria/alerta/1/contagem` retorna a contagem atual, inclusive após exclusões. POST `/backend/monitoria/alerta/1/acompanhar` marca o alerta como acompanhado. O início exibe a quantidade de alertas novos do coordenador autenticado.

## Agenda necessária às reposições

O projeto anterior ainda não implementava sessões nem disponibilidade: a integração de avaliações lançava uma exceção de funcionalidade pendente. Foi acrescentado o suporte persistente a esses dados, sem simular agendas ou disponibilidades. Tela: `/monitoria/sessao/pesquisa`.

GET `/backend/monitoria/sessao/contexto` fornece o usuário atual, nomes dos participantes visíveis e os campi ativos. Somente a coordenação recebe o catálogo completo de pessoas. GET `/backend/monitoria/sessao/pesquisa` lista as sessões do participante, ou todas para a coordenação. GET `/backend/monitoria/sessao/1/alteracao` carrega os participantes de uma sessão autorizada.

POST `/backend/monitoria/sessao/incluir`, exclusivo da coordenação enquanto os fluxos de seleção e cadastro de sessões não estão implementados:
```json
{"identificador":null,"disciplina":"Cálculo I","professor":"4","monitor":"2","campus":"1","alunos":["3"],"inicio":"2026-12-01T10:00:00","fim":"2026-12-01T11:00:00","cancelada":false}
```

Professor e participantes devem existir. O monitor é um aluno designado para a sessão, não um novo papel de autorização. A disciplina usa o nome da agenda de suporte, pois o cadastro de disciplinas também não está implementado. Reposições preservam a mesma disciplina, professor e monitor da sessão original.

POST `/backend/monitoria/disponibilidade/incluir`:
```json
{"identificador":null,"disciplina":"Cálculo I","monitor":"2","campus":"1","diaSemana":2,"inicio":"14:00:00","fim":"16:00:00"}
```

`diaSemana` varia de 1 (segunda) a 7 (domingo). O monitor mantém os próprios horários; a coordenação também pode mantê-los. As operações D5 `alterar` e `excluir` respeitam essa propriedade. Campi inativos são recusados. As sugestões consideram os próximos quatorze dias, duração original e conflitos de ambos os participantes.

## Execução e validação

Backend: `mvn spring-boot:test-run -pl ui-backend`. Frontend: `npm run start` em `ui-frontend/src/main/react` (porta 4200). A configuração SMTP fica no arquivo local ignorado pelo Git. Não foram alteradas dependências Maven, npm ou arquivos requirements.txt.

Foram executados os testes Cucumber e JPA do projeto (138 testes), o build e os testes do frontend (14 testes), a validação dos modelos CML e a validação consolidada de DBML 10.1.1. Os testes de persistência foram executados no MySQL local com `ddl-auto=validate`, substituição de banco desativada e rollback das transações. A verificação HTTP percorreu cadastro e manutenção de FAQ, envio e resposta de dúvida, mural, três faltas, alerta, sugestões, agendamento, bloqueio de duplicidade, cancelamento e atualização da contagem.

O ambiente local usa MySQL 8.0 em `localhost:3306`, banco `cem`, usuário `cem` e perfil `local`. O banco foi criado com `utf8mb4`; o usuário da aplicação tem permissões de consulta, gravação e atualização do esquema exclusivamente em `cem`, sem privilégios administrativos globais. O Hibernate usa `MySQLDialect` e cria e atualiza as tabelas com `ddl-auto=update`. A senha administrativa do MySQL não fica no projeto.

Na execução com o perfil local, foram conferidas as tabelas e chaves estrangeiras, a autenticação do administrador, as consultas de FAQ e mural, as restrições de acesso aos alertas e o encaminhamento das requisições do frontend ao backend. O cadastro administrativo inicial segue a implementação existente; a coordenação acessa as ações de gestão após ser convidada pelo administrador.

A referência à reposição parte de `Falta`, pelo identificador opcional da nova sessão. `Sessao` não guarda referência inversa à falta. Os alunos formam uma coleção sem ordem. As ações adicionais usam as subclasses de `AgregadoAutorizacoes` e `validarAutorizacao`; os objetos de valor com múltiplos atributos implementam `IObjetoValor`. Os cenários BDD são automatizados em step definitions por agregado, com repositórios e demais SPIs substituídos por mocks.

A configuração SMTP fica em `ui-backend/src/main/resources/application-local.properties`, ignorado pelo Git. Não são enviados e-mails de teste a destinatários reais. A inspeção visual em navegador não estava disponível nesta sessão; o layout reutiliza o CSS e a estrutura do protótipo fornecido.


## Seleção de monitores (`monitoria/selecao`)

### `GET /backend/monitoria/selecao/disciplinas`
Lista as disciplinas pelas quais o professor autenticado é responsável para abertura de seleção.

Exemplo de resposta (`200 OK`):
```json
[
  {
    "identificador": "12",
    "codigo": "MAT101",
    "nome": "Cálculo I",
    "cursoNome": "Ciência da Computação"
  }
]
```

### `POST /backend/monitoria/selecao/abrir`
Abre uma seleção de monitoria para uma disciplina do professor autenticado.

Exemplo de requisição:
```json
{
  "disciplina": "12",
  "prazoInscricao": "2026-04-30",
  "vagas": 2,
  "crMinimo": 7.0,
  "pesoCr": 40,
  "pesoEntrevista": 60,
  "patamarMinimo": 7.5
}
```

### `POST /backend/monitoria/selecao/{identificador}/editar`
Altera critérios da seleção antes do recebimento da primeira candidatura e do encerramento.

Exemplo de requisição:
```json
{
  "prazoInscricao": "2026-05-05",
  "vagas": 3,
  "crMinimo": 7.0,
  "pesoCr": 50,
  "pesoEntrevista": 50,
  "patamarMinimo": 7.0
}
```

### `POST /backend/monitoria/selecao/{identificador}/encerrar`
Encerra a seleção e reprova automaticamente as candidaturas pendentes.

## Candidatura à monitoria (`monitoria/candidatura`)

### `GET /backend/monitoria/candidatura/abertas`
Lista as seleções abertas do curso do aluno autenticado com diagnóstico de elegibilidade e sua candidatura ativa, quando existir.

Exemplo de resposta (`200 OK`):
```json
[
  {
    "selecao": {
      "identificador": "5",
      "disciplina": "12",
      "disciplinaNome": "Cálculo I",
      "disciplinaCodigo": "MAT101",
      "cursoNome": "Ciência da Computação",
      "professor": "3",
      "professorNome": "Helena Duarte",
      "semestre": "2026.1",
      "prazoInscricao": "2026-04-30",
      "vagas": 2,
      "crMinimo": 7.0,
      "pesoCr": 40,
      "pesoEntrevista": 60,
      "patamarMinimo": 7.5,
      "situacao": "ABERTA",
      "totalCandidaturas": 1,
      "pendentes": 1,
      "aprovadas": 0,
      "monitoresSelecionados": []
    },
    "elegivel": true,
    "cursoCompativel": true,
    "limiteDisponivel": true,
    "monitoriasNoSemestre": 0,
    "semCandidaturaAtiva": true,
    "situacaoCandidaturaAtiva": null,
    "dentroDoPrazo": true,
    "minhaCandidatura": null
  }
]
```

### `POST /backend/monitoria/candidatura/candidatar`
Registra a candidatura do aluno autenticado em uma seleção.

Exemplo de requisição:
```json
{
  "selecao": "5",
  "cr": 8.4
}
```

### `POST /backend/monitoria/candidatura/{identificador}/cancelar`
Cancela uma candidatura pendente do próprio aluno.

### `POST /backend/monitoria/candidatura/{identificador}/entrevista`
Registra nota e comentário opcional da entrevista pelo professor responsável.

Exemplo de requisição:
```json
{
  "nota": 9.0,
  "comentario": "Boa didática na explicação dos exercícios."
}
```

### `POST /backend/monitoria/candidatura/{identificador}/aprovar`
Aprova a candidatura após o prazo de inscrição respeitando classificação, patamar e vagas; encerra a seleção ao preencher todas as vagas.

### `POST /backend/monitoria/candidatura/{identificador}/reprovar`
Reprova a candidatura após o prazo de inscrição com nota de entrevista já registrada.

## Acompanhamento e relatório de desempenho (`monitoria/acompanhamento`)

### `GET /backend/monitoria/acompanhamento/opcoes`
Lista alunos ativos do mesmo curso sem acompanhamento ativo na disciplina em que o usuário autenticado é monitor.

Exemplo de resposta (`200 OK`):
```json
[
  {
    "aluno": "21",
    "alunoNome": "Lucas Almeida",
    "disciplina": "12",
    "disciplinaNome": "Cálculo I",
    "cursoNome": "Ciência da Computação"
  }
]
```

### `POST /backend/monitoria/acompanhamento/abrir`
Abre acompanhamento de progresso para um aluno na disciplina em que o usuário autenticado atua como monitor.

Exemplo de requisição:
```json
{
  "aluno": "21",
  "disciplina": "12",
  "metaNota": 7.5,
  "metaFrequencia": 75
}
```

### `POST /backend/monitoria/acompanhamento/{identificador}/metas`
Atualiza as metas de nota e frequência de um acompanhamento ativo.

Exemplo de requisição:
```json
{
  "metaNota": 8.0,
  "metaFrequencia": 80
}
```

### `POST /backend/monitoria/acompanhamento/{identificador}/encerrar`
Encerra um acompanhamento ativo exigindo parecer final com pelo menos 20 caracteres.

Exemplo de requisição:
```json
{
  "parecerFinal": "Aluno evoluiu de forma consistente e atingiu as metas definidas."
}
```

### `GET /backend/monitoria/acompanhamento/{identificador}/relatorio`
Calcula o relatório de desempenho com filtro opcional por período (`?de=YYYY-MM-DD&ate=YYYY-MM-DD`).

Exemplo de resposta (`200 OK`):
```json
{
  "acompanhamento": "1",
  "de": null,
  "ate": null,
  "filtroIgnorado": false,
  "totalSessoesValidas": 4,
  "totalPresencas": 4,
  "frequenciaPercentual": 100.0,
  "quantidadeNotas": 4,
  "mediaNotas": 6.75,
  "notaInicial": 5.25,
  "notaAtual": 8.25,
  "variacaoNotas": 3.0,
  "inclinacaoTendencia": 1.2,
  "tendencia": "EM_EVOLUCAO",
  "situacao": "META_ATINGIDA",
  "sessoes": []
}
```

## Feedback por sessão (`monitoria/feedback`)

### `POST /backend/monitoria/feedback/registrar`
Registra feedback (nota de 0 a 10 e comentário opcional) para aluno presente em sessão realizada sem falta do monitor.

Exemplo de requisição:
```json
{
  "sessao": "44",
  "aluno": "21",
  "nota": 8.5,
  "comentario": "Resolveu os exercícios propostos com autonomia."
}
```

### `POST /backend/monitoria/feedback/{identificador}/editar`
Edita nota e comentário de feedback pelo autor enquanto o acompanhamento na disciplina estiver ativo.

Exemplo de requisição:
```json
{
  "nota": 9.0,
  "comentario": "Demonstrou domínio dos conceitos na revisão."
}
```
