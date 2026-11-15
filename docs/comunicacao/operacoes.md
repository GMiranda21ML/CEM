# Operações de FAQ, dúvidas e mural

Todas as operações exigem sessão autenticada. Os POST usam o token do cookie `XSRF-TOKEN` no cabeçalho `X-XSRF-TOKEN`. Erros têm o formato `{"mensagem":"..."}`. As pesquisas retornam `{"consulta":{...},"resultado":{"projecoes":[...],"total":1}}` e aceitam `texto`, `indicePagina` e `tamanhoPagina`.

## FAQ — F010

Tela: `/comunicacao/faq/pesquisa`. GET `/backend/comunicacao/faq/pesquisa` pesquisa pergunta e resposta; para alunos e professores retorna apenas entradas ativas. A coordenação também vê as inativas. As operações padrão D5 de inclusão, alteração e exclusão estão disponíveis somente à coordenação.

POST `/backend/comunicacao/faq/incluir`:
```json
{"identificador":null,"pergunta":"Como participar da monitoria?","resposta":"Consulte as sessões disponíveis.","ativo":true}
```

POST `/backend/comunicacao/faq/1/alterar` usa o mesmo corpo com `"identificador":"1"`; o atributo `ativo` também permite ativar e inativar. POST `/backend/comunicacao/faq/1/excluir` exclui a entrada.

GET `/backend/comunicacao/pergunta/pesquisa` retorna as perguntas do autor; a coordenação recebe todas. POST `/backend/comunicacao/pergunta/enviar` recebe `{"texto":"Como confirmar minha presença na sessão?"}`. POST `/backend/comunicacao/pergunta/1/editar` usa o mesmo corpo e exige autoria e situação pendente. POST `/backend/comunicacao/pergunta/1/excluir` tem as mesmas restrições.

POST `/backend/comunicacao/pergunta/1/responder`, exclusivo da coordenação:
```json
{"pergunta":"Como confirmar presença?","resposta":"Acesse Minhas sessões.","ativo":true}
```

A publicação do FAQ e a resposta da dúvida pertencem à mesma transação. A resposta é preservada na dúvida, independentemente da manutenção posterior do FAQ. O autor recebe e-mail. POST `/backend/comunicacao/pergunta/1/descartar` encerra uma pergunta pendente sem publicá-la.

## Mural — F011

Tela: `/comunicacao/aviso/pesquisa`. GET `/backend/comunicacao/aviso/pesquisa` retorna apenas avisos ativos com validade igual ou posterior à data atual, ordenados pela publicação mais recente. A coordenação também recebe avisos inativos e expirados. A página inicial consome essa mesma consulta.

POST `/backend/comunicacao/aviso/publicar`, permitido à coordenação, professores e alunos designados como monitores de sessões:
```json
{"titulo":"Encontro de monitoria","texto":"Encontro no Campus Central.","validade":"2026-12-10"}
```

POST `/backend/comunicacao/aviso/1/alterar` recebe o aviso completo com identificador e data de publicação originais. POST `/backend/comunicacao/aviso/1/alternar` ativa ou inativa o aviso. Ambas são ações da coordenação. Avisos não têm exclusão física. A tela usa `aviso.publicar`, respondido pela autenticação, para mostrar o botão de novo aviso, independentemente de `aviso.alterar`.

GET `/backend/autenticacao` inclui `faq`, `pergunta` e `aviso` com as autorizações que orientam o menu. O backend valida as permissões em cada ação.
