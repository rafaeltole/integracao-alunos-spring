# integracao-alunos-spring


## Tutorial
- [Checkpoint 02](tutorial-checkpoint-02.md)


# Derived Query Methods, paginação e ordenação


## 1. Derived Query Methods

Ao estendermos a interface `JpaRepository`, o Spring Data JPA
disponibiliza vários métodos prontos, como:

``` java
findById(id);
save(entidade);
deleteById(id);
findAll();
```

Além desses métodos, podemos criar consultas específicas apenas seguindo
a convenção de nomes definida pelo Spring Data JPA.

Esses métodos são chamados de **Derived Query Methods** (métodos
derivados de consulta).

Por exemplo:

``` java
List<Aluno> findByNome(String nome);
```

O Spring interpreta o nome do método e cria a consulta necessária.

Podemos dividir o método em partes:

``` text
List<Aluno>  find  By  Nome  (String nome)
───────────  ────  ──  ────  ─────────────
  retorno    ação      filtro    argumento
```

-   `List<Aluno>`: tipo de retorno;
-   `find`: ação da consulta;
-   `By`: indica o início dos critérios da consulta;
-   `Nome`: propriedade da entidade utilizada como filtro;
-   `String nome`: valor utilizado na consulta.

> O nome utilizado após `By` deve corresponder a uma propriedade da
> entidade.

------------------------------------------------------------------------

## 2. Palavras-chave de ação

Antes de `By`, podemos utilizar palavras-chave que indicam o tipo de
operação que será realizada.

| Palavra-chave | Descrição |
| --- | --- |
| `find...By`, `read...By`, `get...By`, `query...By`, `search...By`, `stream...By` | Métodos de consulta que normalmente retornam uma entidade, uma coleção ou outro tipo de retorno suportado pelo repositório. Podem ser combinados com outras palavras-chave entre a ação e o `By`. |
| `exists...By` | Verifica se existe algum registro que corresponda aos critérios informados. Normalmente retorna `boolean`. |
| `count...By` | Conta os registros que correspondem aos critérios informados. Retorna um valor numérico. |
| `delete...By`, `remove...By` | Remove os registros que correspondem aos critérios informados. Pode não retornar valor (`void`) ou retornar a quantidade de registros removidos. |
| `...First<n>...`, `...Top<n>...` | Limita a quantidade de resultados retornados. O número pode ser informado no nome do método. |
| `...Distinct...` | Solicita que a consulta retorne apenas resultados distintos. |

### Exemplos

``` java
List<Aluno> findByNome(String nome);

boolean existsByNome(String nome);

long countByNome(String nome);
```

------------------------------------------------------------------------

## 3. Palavras-chave de condição/filtro

Tudo o que vem depois de `By` representa os critérios utilizados na
consulta. O Spring Data JPA disponibiliza diversas palavras-chave para
expressar essas condições.

| Condição | Expressões suportadas |
| --- | --- |
| `AND` | `And` |
| `OR` | `Or` |
| `AFTER` | `After`, `IsAfter` |
| `BEFORE` | `Before`, `IsBefore` |
| `CONTAINING` | `Containing`, `IsContaining`, `Contains` |
| `BETWEEN` | `Between`, `IsBetween` |
| `ENDING_WITH` | `EndingWith`, `IsEndingWith`, `EndsWith` |
| `EXISTS` | `Exists` |
| `FALSE` | `False`, `IsFalse` |
| `GREATER_THAN` | `GreaterThan`, `IsGreaterThan` |
| `GREATER_THAN_EQUALS` | `GreaterThanEqual`, `IsGreaterThanEqual` |
| `IN` | `In`, `IsIn` |
| `IS` | `Is`, `Equals` ou nenhuma palavra-chave |
| `IS_EMPTY` | `IsEmpty`, `Empty` |
| `IS_NOT_EMPTY` | `IsNotEmpty`, `NotEmpty` |
| `IS_NOT_NULL` | `NotNull`, `IsNotNull` |
| `IS_NULL` | `Null`, `IsNull` |
| `LESS_THAN` | `LessThan`, `IsLessThan` |
| `LESS_THAN_EQUAL` | `LessThanEqual`, `IsLessThanEqual` |
| `LIKE` | `Like`, `IsLike` |
| `NEAR` | `Near`, `IsNear` |
| `NOT` | `Not`, `IsNot` |
| `NOT_IN` | `NotIn`, `IsNotIn` |
| `NOT_LIKE` | `NotLike`, `IsNotLike` |
| `REGEX` | `Regex`, `MatchesRegex`, `Matches` |
| `STARTING_WITH` | `StartingWith`, `IsStartingWith`, `StartsWith` |
| `TRUE` | `True`, `IsTrue` |
| `WITHIN` | `Within`, `IsWithin` |

### Exemplos

``` java
List<Aluno> findByNomeContaining(String nome);

List<Aluno> findByNomeStartingWith(String nome);

List<Aluno> findByNomeEndingWith(String nome);

List<Aluno> findByNomeOrEmail(String nome, String email);
```

> Essas tabelas funcionam como material de consulta. Durante a aula, não
> é necessário apresentar ou memorizar todas as palavras-chave. O mais
> importante é compreender como elas são combinadas para formar o nome
> do método.

------------------------------------------------------------------------

## 4. Modificadores

Também podemos adicionar modificadores aos filtros.

### Ignorando maiúsculas e minúsculas

``` java
List<Aluno> findByNomeIgnoreCase(String nome);
```

### Buscando parte do texto

``` java
List<Aluno> findByNomeContaining(String nome);
```

### Combinando os dois comportamentos

``` java
List<Aluno> findByNomeContainingIgnoreCase(String nome);
```

Assim, uma busca por:

``` text
rafa
```

poderá encontrar nomes como:

``` text
Rafael
RAFAELA
Rafaela Silva
```

------------------------------------------------------------------------

## 5. Aplicando no projeto

Atualmente, nosso repositório possui:

``` java
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    List<Aluno> findByNome(String nome);

    boolean existsByNome(String aluno);
}
```

O método:

``` java
findByNome(String nome)
```

procura alunos cujo nome seja igual ao valor informado.

Queremos permitir uma pesquisa que:

1.  encontre nomes que **contenham** o texto informado;
2.  não diferencie letras maiúsculas de minúsculas;
3.  permita paginação.

Podemos alterar o método para:

``` java
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Page<Aluno> findByNomeContainingIgnoreCase(
            String nome,
            Pageable paginacao);

    boolean existsByNome(String aluno);
}
```

Também podemos aplicar o mesmo tipo de comparação ao método `exists`:

``` java
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Page<Aluno> findByNomeContainingIgnoreCase(
            String nome,
            Pageable paginacao);

    boolean existsByNomeContainingIgnoreCase(String aluno);
}
```

------------------------------------------------------------------------

# Paginação e ordenação

## 6. Por que paginar?

Imagine uma tabela com milhares de alunos.

Retornar todos os registros em uma única requisição pode:

-   aumentar o consumo de memória;
-   aumentar o tráfego de dados;
-   tornar a consulta mais lenta;
-   dificultar a exibição dos resultados no frontend.

O Spring Data permite trabalhar com paginação através da interface:

``` java
Pageable
```

E retornar os resultados utilizando:

``` java
Page<T>
```

------------------------------------------------------------------------

## 7. Consulta atual

Inicialmente, nosso serviço consulta todos os alunos:

``` java
public List<AlunoResponse> consultar() {

    List<AlunoResponse> alunosResponse = new ArrayList<>();

    List<Aluno> alunosCadastrados = alunoRepository.findAll();

    for (Aluno alunoCadastrado : alunosCadastrados) {
        AlunoResponse alunoResponse = AlunoResponse.from(alunoCadastrado);
        alunosResponse.add(alunoResponse);
    }

    return alunosResponse;
}
```

------------------------------------------------------------------------

## 8. Adicionando filtro e paginação

Queremos utilizar o mesmo endpoint para:

-   consultar todos os alunos;
-   pesquisar alunos por nome;
-   paginar os resultados;
-   ordenar os resultados.

O serviço pode ser alterado para:

``` java
public Page<AlunoResponse> consultar(
        String nome,
        Pageable paginacao) {

    Page<Aluno> alunosCadastrados;

    if (nome != null && !nome.isBlank()) {

        alunosCadastrados =
                alunoRepository.findByNomeContainingIgnoreCase(
                        nome,
                        paginacao);

    } else {

        alunosCadastrados =
                alunoRepository.findAll(paginacao);
    }

    return alunosCadastrados.map(AlunoResponse::from);
}
```

Observe que tanto:

``` java
findByNomeContainingIgnoreCase(nome, paginacao)
```

quanto:

``` java
findAll(paginacao)
```

retornam:

``` java
Page<Aluno>
```

Isso permite utilizar:

``` java
alunosCadastrados.map(AlunoResponse::from);
```

para converter cada `Aluno` em `AlunoResponse` sem perder as informações
da paginação.

------------------------------------------------------------------------

## 9. Ajustando o endpoint

### Versão atual

``` java
@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<List<AlunoResponse>> consultar() {

    List<AlunoResponse> alunosCadastrados =
            alunoService.consultar();

    return ResponseEntity.ok(alunosCadastrados);
}
```

### Versão com paginação, filtro e ordenação

``` java
@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Page<AlunoResponse>> consultar(
        @RequestParam(required = false) String nome,
        @PageableDefault(
                page = 0,
                size = 10,
                sort = "nome",
                direction = Sort.Direction.ASC)
        Pageable paginacao) {

    Page<AlunoResponse> alunosCadastrados =
            alunoService.consultar(nome, paginacao);

    return ResponseEntity.ok(alunosCadastrados);
}
```

Caso os parâmetros não sejam informados, serão utilizados os valores
definidos em `@PageableDefault`:

``` text
page = 0
size = 10
sort = nome
direction = ASC
```

------------------------------------------------------------------------

## 10. Testando a API

### Primeira página

``` http
GET /alunos
```

### Pesquisando pelo nome

``` http
GET /alunos?nome=rafa
```

### Alterando a página

``` http
GET /alunos?page=1
```

### Alterando a quantidade de registros

``` http
GET /alunos?page=0&size=5
```

### Ordenando pelo nome

``` http
GET /alunos?sort=nome,asc
```

### Ordenação decrescente

``` http
GET /alunos?sort=nome,desc
```

### Combinando filtro, paginação e ordenação

``` http
GET /alunos?nome=rafa&page=0&size=5&sort=nome,asc
```

------------------------------------------------------------------------

## Referências

-   [Spring Data JPA --- Repository query
    keywords](https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html)
-   [Spring Data Commons --- Query
    methods](https://docs.spring.io/spring-data/commons/reference/repositories/query-methods-details.html)
