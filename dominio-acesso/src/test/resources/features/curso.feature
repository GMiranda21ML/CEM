# language: pt

Funcionalidade: Cadastro e gerenciamento de cursos

  Cenário: Coordenador cadastra curso de sua instituição
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    E que não existe curso "Sistemas de Informação" com sigla "SI" na instituição "Instituto CEM"
    Quando a coordenadora cadastra o curso "Sistemas de Informação" com a sigla "SI"
    Então o curso "Sistemas de Informação" com sigla "SI" é registrado na instituição "Instituto CEM"

  Cenário: Não cadastrar curso sem nome
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    Quando a coordenadora tenta cadastrar um curso com nome vazio e sigla "SI"
    Então o cadastro do curso é recusado

  Cenário: Não cadastrar curso sem sigla
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    Quando a coordenadora tenta cadastrar o curso "Sistemas de Informação" com sigla vazia
    Então o cadastro do curso é recusado

  Cenário: Não cadastrar curso com nome duplicado na mesma instituição
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    E que existe o curso "Sistemas de Informação" com sigla "SI" na instituição "Instituto CEM"
    Quando a coordenadora tenta cadastrar outro curso "sistemas de informação" com a sigla "BSI"
    Então o cadastro do curso é recusado

  Cenário: Não cadastrar curso com sigla duplicada na mesma instituição
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    E que existe o curso "Sistemas de Informação" com sigla "SI" na instituição "Instituto CEM"
    Quando a coordenadora tenta cadastrar outro curso "Segurança da Informação" com a sigla "si"
    Então o cadastro do curso é recusado

  Cenário: Permitir cursos com mesmo nome ou sigla em instituições diferentes
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    E que existe o curso "Sistemas de Informação" com sigla "SI" na instituição "Outra Instituição"
    Quando a coordenadora cadastra o curso "Sistemas de Informação" com a sigla "SI"
    Então o curso "Sistemas de Informação" com sigla "SI" é registrado na instituição "Instituto CEM"

  Cenário: Coordenador remove curso sem vínculos
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    E que existe o curso "Sistemas de Informação" com sigla "SI" sem disciplinas e sem usuários vinculados
    Quando a coordenadora remove o curso "Sistemas de Informação"
    Então o curso "Sistemas de Informação" deixa de existir

  Cenário: Não remover curso que possui disciplinas vinculadas
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    E que existe o curso "Ciência da Computação" com disciplinas vinculadas
    Quando a coordenadora tenta remover o curso "Ciência da Computação"
    Então a remoção do curso é recusada

  Cenário: Não remover curso que possui usuários vinculados
    Dado que a coordenadora do curso pertence à instituição "Instituto CEM"
    E que existe o curso "Engenharia Civil" com usuários vinculados
    Quando a coordenadora tenta remover o curso "Engenharia Civil"
    Então a remoção do curso é recusada
