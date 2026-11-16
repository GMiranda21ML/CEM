# language: pt

Funcionalidade: Cadastro e gerenciamento de disciplinas e áreas

  Cenário: Coordenador cadastra disciplina com sucesso
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    E que existe o curso de disciplina "Ciência da Computação" nessa instituição
    E que existe o professor de disciplina "Profa. Helena Duarte" ativo e vinculado ao curso "Ciência da Computação"
    E que não existe disciplina com código "EST1001" nessa instituição
    Quando a coordenadora cadastra a disciplina "Estatística" com código "EST1001", área "Matemática", no curso "Ciência da Computação" e professor "Profa. Helena Duarte"
    Então a disciplina "EST1001" é registrada com sucesso

  Cenário: Não cadastrar disciplina sem nome
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    Quando a coordenadora tenta cadastrar uma disciplina com nome vazio, código "EST1001", área "Matemática", curso "Ciência da Computação" e professor "Profa. Helena Duarte"
    Então o cadastro da disciplina é recusado

  Cenário: Não cadastrar disciplina com código fora do padrão AAA0000
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    Quando a coordenadora tenta cadastrar a disciplina "Estatística" com código "EST-1001", área "Matemática", curso "Ciência da Computação" e professor "Profa. Helena Duarte"
    Então o cadastro da disciplina é recusado

  Cenário: Não cadastrar disciplina sem área
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    Quando a coordenadora tenta cadastrar a disciplina "Estatística" com código "EST1001", área vazia, curso "Ciência da Computação" e professor "Profa. Helena Duarte"
    Então o cadastro da disciplina é recusado

  Cenário: Não cadastrar disciplina com código repetido na mesma instituição
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    E que já existe uma disciplina com código "EST1001" nessa instituição
    Quando a coordenadora tenta cadastrar outra disciplina "Probabilidade" com o mesmo código "EST1001"
    Então o cadastro da disciplina é recusado

  Cenário: Não cadastrar disciplina com nome repetido no mesmo curso
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    E que já existe a disciplina "Cálculo I" no curso "Ciência da Computação"
    Quando a coordenadora tenta cadastrar outra disciplina "cálculo i" com código "MAT1002" no mesmo curso "Ciência da Computação"
    Então o cadastro da disciplina é recusado

  Cenário: Permitir mesmo nome de disciplina em cursos diferentes
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    E que já existe a disciplina "Cálculo I" no curso "Ciência da Computação"
    E que existe o curso de disciplina "Engenharia Civil" nessa instituição
    E que existe o professor de disciplina "Prof. Paulo Andrade" ativo e vinculado ao curso "Engenharia Civil"
    Quando a coordenadora cadastra a disciplina "Cálculo I" com código "MAT2001", área "Matemática", no curso "Engenharia Civil" e professor "Prof. Paulo Andrade"
    Então a disciplina "MAT2001" é registrada com sucesso

  Cenário: Não cadastrar disciplina para professor inativo
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    E que o professor "Prof. Inativo" está inativo no curso "Ciência da Computação"
    Quando a coordenadora tenta cadastrar a disciplina "Estatística" para o professor "Prof. Inativo"
    Então o cadastro da disciplina é recusado

  Cenário: Não cadastrar disciplina para professor de outro curso
    Dado que a coordenadora de disciplinas pertence à instituição "Instituto CEM"
    E que o professor "Prof. Paulo Andrade" pertence apenas ao curso "Engenharia Civil"
    Quando a coordenadora tenta cadastrar a disciplina "Estatística" no curso "Ciência da Computação" com o professor "Prof. Paulo Andrade"
    Então o cadastro da disciplina é recusado

  Cenário: Coordenador altera dados da disciplina
    Dado que existe a disciplina "Estatística" com código "EST1001" e área "Matemática"
    Quando a coordenadora altera a área da disciplina para "Estatística e Probabilidade"
    Então a disciplina passa a ter a área "Estatística e Probabilidade"

  Cenário: Coordenador remove disciplina sem vínculos
    Dado que existe a disciplina "Estatística" com código "EST1001" sem seleções, sem monitores e sem sessões
    Quando a coordenadora remove a disciplina "EST1001"
    Então a disciplina "EST1001" deixa de existir

  Cenário: Não remover disciplina com vínculos de monitoria
    Dado que existe a disciplina "Cálculo I" com código "MAT1001" com vínculos de monitoria
    Quando a coordenadora tenta remover a disciplina "MAT1001"
    Então a remoção da disciplina é recusada
