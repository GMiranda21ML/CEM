# language: pt
Funcionalidade: aviso

  Contexto:
    Dado o ambiente de aviso preparado

  Cenário: Título limitado a cem caracteres
    Quando publico um aviso com título de 101 caracteres
    Então o aviso com título longo é recusado

  Cenário: Aluno sem monitoria não publica
    Quando um aluno tenta publicar um aviso
    Então a publicação sem autorização é recusada

  Esquema do Cenário: Publicação pelos perfis autorizados
    Quando publico um aviso como "<perfil>"
    Então o aviso é publicado

    Exemplos:
      | perfil      |
      | COORDENADOR |
      | PROFESSOR   |
      | MONITOR     |

  Cenário: Monitor não recebe permissão de editar outros avisos
    Quando consulto as permissões do mural como monitor
    Então posso publicar e não posso editar ou inativar avisos

  Cenário: Validade anterior à publicação
    Quando publico um aviso com validade de ontem
    Então a validade passada é recusada

  Cenário: Aviso visível no último dia
    Quando consulto um aviso no último dia e no dia seguinte
    Então o aviso só é exibido até o último dia de validade
