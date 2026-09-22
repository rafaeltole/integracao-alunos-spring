# Tutorial — Checkpoint 02

## Objetivo

Resumo dos tópicos estudados durante o segundo período/bimestre e dos passos aplicados em nossa aplicação de estudos, com conteúdo referente a **JPA** e **Spring Data JPA**.

> O objetivo deste tutorial é revisar o processo utilizado na aplicação de alunos e servir como referência para a resolução do Checkpoint 02.

## 1. Análise do domínio

Antes de iniciar o mapeamento e nos preocuparmos com anotações, analisamos o nosso modelo.

![Modelo de domínio — Aluno, Perfil e Matrícula](./assets/modelo-dominio-alunos.png)

A partir do modelo, identificamos as tabelas e seus relacionamentos.

## 2. Transformando o modelo em classes Java

Após a análise do modelo, escolhemos quais tabelas serão representadas por classes Java.

> Neste primeiro momento, estamos apenas representando a estrutura do modelo em classes. Os relacionamentos serão mapeados posteriormente.

```java
public class Aluno {
    private Long id;
    private String nome;
    private LocalDate cadastradoEm;
    private LocalDateTime atualizadoEm;
}
```

```java
public class Perfil {
    private Long id;
    private Long alunoId;
    private String titulo;
    private String descricao;
}
```

```java
public class Matricula {
    private Long id;
    private Long alunoId;
    private String rm;
    private String status;
    private LocalDate cadastradoEm;
    private LocalDate atualizadoEm;
}
```

## 3. Mapeando as entidades

Vamos incluir as anotações JPA e transformar nossas classes em entidades.

```java
@Entity //--- configurando a classe como uma entidade JPA
public class Aluno {

    @Id //--- chave primária | PK
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //--- como as chaves primárias (PK) serão geradas
    @SequenceGenerator(name = "aluno_seq", allocationSize = 1) //--- responsável pela geração das chaves primárias (PK)
    private Long id;

    private String nome;
    private LocalDate cadastradoEm;
    private LocalDateTime atualizadoEm;
}
```

```java
@Entity //--- configurando a classe como uma entidade JPA
public class Perfil {

    @Id //--- chave primária | PK
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //--- como as chaves primárias (PK) serão geradas
    @SequenceGenerator(name = "perfil_seq", allocationSize = 1) /--- responsável pela geração das chaves primárias (PK)
    private Long id;

    private Long alunoId;
    private String titulo;
    private String descricao;
}
```

```java
@Entity //--- configurando a classe como uma entidade JPA
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //--- como as chaves primárias (PK) serão geradas
    @SequenceGenerator(name = "matricula_seq", allocationSize = 1) //--- responsável pela geração das chaves primárias (PK)
    private Long id;

    private Long alunoId;
    private String rm;
    private String status;
    private LocalDate cadastradoEm;
    private LocalDate atualizadoEm;
}
```

## 4. Mapeando os relacionamentos

Até o momento temos nossas entidades mapeadas, porém de forma isolada. Agora vamos criar as associações entre as classes e configurar os relacionamentos entre as entidades.

### Aluno

```java
@OneToOne(mappedBy = "aluno")
private Perfil perfil;

@OneToMany(mappedBy = "aluno")
private List<Matricula> matriculas;
```

### Perfil

```java
@OneToOne
@JoinColumn(name = "aluno_id", nullable = false, unique = true)
private Aluno aluno;
```

### Matrícula

```java
@ManyToOne
@JoinColumn(name = "aluno_id", nullable = false)
private Aluno aluno;
```


## 5. Configurando Cascade

Vamos configurar o relacionamento entre `Aluno` e `Perfil` para propagar as operações `PERSIST`, `MERGE` e `REMOVE`.

```java
@OneToOne(
    mappedBy = "aluno",
    cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE,
        CascadeType.REMOVE
    }
)
private Perfil perfil;
```

Com essa configuração, operações realizadas em `Aluno` podem ser propagadas para o `Perfil` associado.

### Atualizando DTOs e Service

No cadastro, é importante configurar os dois lados do relacionamento bidirecional:

```java
public AlunoResponse cadastrar(AlunoRequest alunoRequest) {
    Aluno novoAluno = alunoRequest.toEntity();

    PerfilRequest perfilRequest = alunoRequest.perfil();
    Perfil perfil = perfilRequest.toEntity();

    novoAluno.setPerfil(perfil);
    perfil.setAluno(novoAluno);

    Aluno alunoCadastrado = alunoRepository.save(novoAluno);

    return AlunoResponse.from(alunoCadastrado);
}
```

No update, alteramos o objeto já associado ao aluno:

```java
public AlunoResponse atualizar(Long codigo, AlunoRequest aluno) {
    Optional<Aluno> retornoConsulta = alunoRepository.findById(codigo);
    if (retornoConsulta.isEmpty()) {
        throw new AlunoNaoEncontradoException("Aluno [codigo=" + codigo + "] não encontrado.");
    }

    Aluno alunoCadastrado = retornoConsulta.get();
    alunoCadastrado.setNome(aluno.nome());

    PerfilRequest perfilRequest = aluno.perfil();

    Perfil perfil = alunoCadastrado.getPerfil();
    perfil.setTitulo(perfilRequest.titulo());
    perfil.setDescricao(perfilRequest.descricao());

    Aluno alunoAtualizado = alunoRepository.save(alunoCadastrado);

    return AlunoResponse.from(alunoAtualizado);
}
```

Na remoção, enviamos o código do aluno:

```java
public void remover(Long codigo) {
    Optional<Aluno> retornoConsulta = alunoRepository.findById(codigo);
    if (retornoConsulta.isPresent()) {
        alunoRepository.deleteById(codigo);
    }
}
```


## 6. Configurando Fetch Type

Vamos alterar para `LAZY` um relacionamento cujo comportamento padrão é `EAGER`.

### OneToOne

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "aluno_id", nullable = false, unique = true)
private Aluno aluno;
```

### ManyToOne

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "aluno_id", nullable = false)
private Aluno aluno;
```

> `@OneToOne` e `@ManyToOne` possuem `EAGER` como estratégia padrão. Aqui estamos configurando explicitamente `FetchType.LAZY`.

## 7. Derived Query Methods

O `JpaRepository` já fornece as operações básicas de persistência. Para consultas específicas, podemos criar métodos seguindo as convenções de nomes do Spring Data JPA.

### Consulta utilizando atributo da própria entidade

```java
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    //--- consulta utilizando atributos da entidade
    List<Aluno> findByNomeContainsIgnoringCase(String nome);

    //--- verificação utilizando atributos da entidade
    boolean existsByNome(String nome);
}
```

### Consulta utilizando associação entre as entidades

> Consultar turmas utilizando um atributo do aluno relacionado.

```java
@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    
    //--- consulta utilizando associação entre as entidades
    List<Turma> findByMatriculasAlunoId(Long alunoId);

}
```


## 8. Consultas utilizando `@Query`

### Consulta utilizando `@Query` filtrando por um atributo da própria entidade
```java
//TODO implementar consulta
```

### Consulta utilizando `@Query` com JOIN entre as entidades relacionadas e filtrando por um atributo

```java
@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
   
    //--- consulta utilizando JPQL com join entre as entidades
    @Query(value = "SELECT t FROM Turma t JOIN t.matriculas m JOIN m.aluno a WHERE a.id = :alunoId")
    List<Turma> consultarTurmasPorAluno(@Param("alunoId") Long alunoId);

}
```

> Em JPQL trabalhamos com **entidades e seus atributos**, e não diretamente com nomes de tabelas e colunas.

## 9. Paginação e ordenação

Consultando todos os alunos aplicando paginação e ordenação.
```java
//TODO implementar paginação e ordenação
```


## Observação

Utilize este roteiro como referência e aplique os mesmos conceitos em outro domínio.

O importante não é reproduzir as classes `Aluno`, `Perfil` e `Matricula`, mas identificar no novo domínio onde cada conceito de JPA e Spring Data JPA deverá ser aplicado.
