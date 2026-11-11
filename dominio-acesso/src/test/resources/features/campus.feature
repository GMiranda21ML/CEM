# language: pt

Funcionalidade: Gerenciamento de campus da instituição

  Cenário: Coordenador cadastra campus de sua instituição
    Dado que o coordenador pertence à instituição "Instituto CEM"
    E que não existe campus chamado "Centro" na instituição "Instituto CEM"
    Quando o coordenador cadastra o campus "Centro" com endereço "Rua Principal, 100"
    Então o campus "Centro" é registrado ativo na instituição "Instituto CEM"

  Cenário: Não cadastrar campus sem nome
    Dado que o coordenador pertence à instituição "Instituto CEM"
    Quando o coordenador tenta cadastrar um campus com nome vazio
    Então o cadastro do campus é recusado

  Cenário: Não cadastrar nome duplicado na mesma instituição
    Dado que o coordenador pertence à instituição "Instituto CEM"
    E que existe campus chamado "Centro" na instituição "Instituto CEM"
    Quando o coordenador tenta cadastrar outro campus chamado "centro"
    Então o cadastro do campus é recusado

  Cenário: Permitir nomes iguais em instituições diferentes
    Dado que o coordenador pertence à instituição "Instituto CEM"
    E que existe campus chamado "Centro" na instituição "Outra Instituição"
    Quando o coordenador cadastra o campus "Centro" com endereço "Rua Principal, 100"
    Então o campus "Centro" é registrado ativo na instituição "Instituto CEM"

  Cenário: Não cadastrar campus em outra instituição
    Dado que o coordenador pertence à instituição "Instituto CEM"
    Quando o coordenador tenta cadastrar o campus "Centro" na instituição "Outra Instituição"
    Então o cadastro do campus é recusado

  Cenário: Coordenador altera campus de sua instituição
    Dado que o coordenador pertence à instituição "Instituto CEM"
    E que existe o campus ativo "Centro" na instituição "Instituto CEM"
    Quando o coordenador altera o nome do campus para "Centro Histórico"
    Então o campus passa a se chamar "Centro Histórico"

  Cenário: Não alterar campus de outra instituição
    Dado que o coordenador pertence à instituição "Instituto CEM"
    E que existe o campus ativo "Centro" na instituição "Outra Instituição"
    Quando o coordenador tenta alterar o nome do campus para "Centro Histórico"
    Então a alteração do campus é recusada

  Cenário: Coordenador inativa campus ativo
    Dado que o coordenador pertence à instituição "Instituto CEM"
    E que existe o campus ativo "Centro" na instituição "Instituto CEM"
    Quando o coordenador inativa o campus "Centro"
    Então o campus "Centro" está inativo

  Cenário: Campus inativo não recebe nova disponibilidade
    Dado que o campus "Centro" está inativo
    Quando tenta-se criar uma disponibilidade no campus "Centro"
    Então a disponibilidade é recusada

  Cenário: Campus inativo não recebe nova sessão
    Dado que o campus "Centro" está inativo
    Quando tenta-se criar uma sessão no campus "Centro"
    Então a sessão é recusada

  Cenário: Coordenador reativa campus inativo
    Dado que o coordenador pertence à instituição "Instituto CEM"
    E que existe o campus inativo "Centro" na instituição "Instituto CEM"
    Quando o coordenador reativa o campus "Centro"
    Então o campus "Centro" está ativo
