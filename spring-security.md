# Herança em JPA

## @MappedSuperclass

Em JPA podemos utilizar fazer o uso de herança entre as entidades. Uma das abordagens é utilizando a anotação @MappedSuperclass em uma das nossas classes, com as entidades filhas herdam os métodos da super classe. Na nossa aplicação de exemplo,  algumas entidades possuem atributos que se repetem entre elas, que são as datas em que o registro foi cadastrado ou atualizado, representados pelos atribuitos: cadastrado_em e atualizado_em.

Aluno:

```java
@Entity 
public class Aluno {

    //--- Demais métodos e atributos omitidos
    private LocalDate cadastradoEm;

    private LocalDateTime atualizadoEm;
}
```

```java
@Entity
public class Matricula {

    //--- Demais métodos e atributos omitidos
    
    private LocalDate cadastradoEm;

    private LocalDate atualizadoEm;
}
```

```java
@Entity
public class Turma {
    
    //--- Demais métodos e atributos omitidos
    
    private LocalDate cadastradoEm;

    private LocalDate atualizadoEm;
}
```

Vamos criar uma nova classe EntidadeBase com os atributos cadastradoEm e atualizadoEm, inclui dois métodos que serão responsáveis por configurar as data de cadastro e atualização, anota-los com @PrePersit e @PreUpdate, assim eles serão executados sempre que ocorrer uma das operações de cadastro ou atualização. Por último anotamos a classe com @MappedSuperclass.

EntidadeBase e classes atualizadas.

```java
@MappedSuperclass
public class EntidadeBase {

    private LocalDate cadastradoEm;

    private LocalDate atualizadoEm;

    public LocalDate getCadastradoEm() {
        return cadastradoEm;
    }

    @PrePersist
    public void configurarDataCadastro() {
        cadastradoEm = LocalDate.now();
    }
    
    public LocalDate getAtualizadoEm() {
        return atualizadoEm;
    }

    @PreUpdate
    public void configurarDataAtualizacao() {
        atualizadoEm = LocalDate.now();
    }
}
```

```java
@Entity
public class Aluno extends EntidadeBase {
    //--- Demais métodos e atributos omitidos
}
```

```java
@Entity
public class Matricula extends EntidadeBase{
    //--- Demais métodos e atributos omitidos
}
```

```java
@Entity
public class Turma extends EntidadeBase{
    //--- Demais métodos e atributos omitidos	
}
```

Vamos testar, fazendo uma nova requisição para os endpoints de cadastro e atualização de alunos.

Outro ponto que podemos observar em nossa aplicação de exemplo, que, da maneira como está, qualquer pessoa consegui acessar as nossas APIs, cadastrar novos usuário ou até mesmo, fazer novas matrículas.

## Autenticação x Autorização

Agora, precisamos controlar o acesso a nossa aplicação, para que, somente pessoas autenticadas e autorizadas possam realizar as consultas ou alterações nos dados.

Autenticação: informa quem você é.

Autorização: informa o que você pode acessar ou fazer.

![Diagrama 1](14-aula-assets/image1.png)

Um aluno pode estar autenticado e mesmo assim receber um acesso negado?

## Spring Security

Framework que fornece:

- Autenticação;
- Autorização;
- Proteção contra ataques comuns:
  - HTTP Headers
  - HTTP Requests

Vamos alterar o arquivo pom.xml e, adicionar o Spring Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

E agora tentar acessar novamente os endpois da API de alunos

```http
GET /alunos
```

Ao adicionar o Spring Security o comportamento da nossa aplicação já foi modificado.

## Atuação do Spring Security

As requisições que forem feitas a nossa aplicação passam pela infraestrutura de segurança antes de chegar ao controller.

![Diagrama 2](14-aula-assets/image2.png)

Cadeia/ regras de segurança aplicadas a requisição:


https://docs.spring.io/spring-security/reference/_images/servlet/architecture/filterchain.png

![Diagrama 3](14-aula-assets/image3.png)

## Primeira configuração

Configurando nosso filtro de requisições HTTP.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
```

## Autenticação

Configuração autenticação básica HTTP (HTTP Basic Auth). Classe SecurityConfig.

```java
@Bean
UserDetailsService userDetailsService() {
    UserDetails usuario = User.builder()
            .username("login_aluno")
            .password("123456")
            .roles("ALUNO")		
            .build();

    return new InMemoryUserDetailsManager(usuario);
}
```

Alterando as requisições HTTP para utilizar HTTP Basic. Todas as requisições deverão enviar o usuário e senha no cabeçalho das requisições HTTP.

```http
GET /alunos
```

**Basic Auth**

- username: `login_aluno`
- password: `123456`



## Referências

- https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html
- https://docs.spring.io/spring-security/reference
- https://docs.spring.io/spring-security/reference/servlet/architecture.html
