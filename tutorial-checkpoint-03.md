# Checkpoint 03 — Tutorial Java Advanced

**Tema:** Implementando autenticação, controle de acesso e documentação de APIs REST.

## Objetivos

Ao final da atividade, a aplicação deverá:

1. Autenticar usuários com HTTP Basic no endpoint de login.
2. Gerar um token JWT assinado, válido por cinco minutos.
3. Validar tokens JWT nas requisições protegidas.
4. Restringir operações com base nos papéis `ALUNO` e `COORDENADOR`.
5. Disponibilizar a documentação OpenAPI e a interface Swagger UI.

## 1. Dependências Maven

No `pom.xml`, adicione as dependências correspondentes à versão do Spring Boot do projeto:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- OAuth2 Resource Server -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId>
</dependency>

<!-- OpenAPI e Swagger UI: versão conforme o Spring Boot -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.1.0</version>
</dependency>
```

## 2. DTO de resposta da autenticação

Crie `TokenResponse` no pacote `dto`. O contrato abaixo está alinhado com os três valores retornados pelo serviço de geração de token.

```java
public record TokenResponse(
        String accessToken,
        String tokenType,
        Integer expiresIn) {
}
```

- `accessToken`: JWT assinado.
- `tokenType`: tipo do token (`Bearer`).
- `expiresIn`: duração, em segundos.


## 3. Serviço de geração do JWT

Crie `GeradorTokenJWTService` no pacote `service`.

```java
@Service
public class GeradorTokenJWTService {

    private static final String TIPO_TOKEN = "Bearer";
    private static final Integer VALIDADE_TOKEN_EM_SEGUNDOS = 300; //--- 5 minutos

    //--- Responsável por assinar o Token JWT, permitindo verificar sua integridade e autenticidade.
    private final JwtEncoder jwtEncoder;

    public GeradorTokenJWTService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public TokenResponse gerar(Authentication authentication) {
        Instant dataAtual = Instant.now();
        Instant dataExpiracaoToken = dataAtual.plusSeconds(VALIDADE_TOKEN_EM_SEGUNDOS); //--- Token válido por 5 minutos

        //--- Recuperar as roles (papéis) concedidos ao usuário autenticado.
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        //--- Configurando o conjunto de informações ao Token JWT
        JwtClaimsSet informacoesDoTokenJWT = JwtClaimsSet
                .builder()
                .issuer("integracao-alunos-spring") //--- emissor do token
                .subject(authentication.getName()) //--- para quem o token foi emitido
                .issuedAt(dataAtual) //--- quando o token foi emitido
                .expiresAt(dataExpiracaoToken) //--- quando o token expira
                .claim("roles", roles) //--- roles (papéis) configurados para o usuário
                .build();

        //--- Parametrizando o Token JWT para ser enviado para assinatura
        JwtEncoderParameters parametrizacaoTokenJwtParaAssinatura = JwtEncoderParameters.from(informacoesDoTokenJWT);

        //--- Assinando o Token JWT
        Jwt tokenJwtAssinado = jwtEncoder.encode(parametrizacaoTokenJwtParaAssinatura);

        //--- Conteúdo do Token JWT como String
        String tokenJwt = tokenJwtAssinado.getTokenValue();

        return new TokenResponse(tokenJwt, TIPO_TOKEN, VALIDADE_TOKEN_EM_SEGUNDOS);
    }

}
```

**Principais imports:** 

`org.springframework.security.core.Authentication`,

`org.springframework.security.oauth2.jwt.JwtClaimsSet`,
 

## 4. Endpoint de autenticação

Crie `AutenticacaoResource` no pacote `controller`.

```java
@RestController
@RequestMapping("/autenticacao")
public class AutenticacaoResource {

    private final GeradorTokenJWTService geradorTokenService;

    public AutenticacaoResource(GeradorTokenJWTService geradorTokenService) {
        this.geradorTokenService = geradorTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(Authentication authentication) {
        TokenResponse resposta = geradorTokenService.gerar(authentication);
        return ResponseEntity.ok(resposta);
    }
}
```

**Como funciona:** o cliente envia `Authorization: Basic ...` ao endpoint `POST /autenticacao/login`. O Spring Security valida as credenciais e disponibiliza o objeto `Authentication` ao controller. O controller devolve o JWT, que poderá ser usado com `Authorization: Bearer <token>` nos demais endpoints.

## 5. Papéis da aplicação

Crie o enum `Roles` no pacote `enums`.

```java
public enum Roles {
    ALUNO,
    COORDENADOR
}
```

Ao configurar um usuário com `.roles("ALUNO")`, o Spring Security cria a authority `ROLE_ALUNO`. Por isso, `.hasRole("ALUNO")` verifica `ROLE_ALUNO`.

## 6. Configurações de segurança

Crie `ConfiguracoesDeSeguranca` no pacote de configuração da aplicação.

### 6.1. Chave de assinatura

No `application.properties`:

```properties
integracao-alunos-spring.jwt-secret=configure-sua-chave-super-secreta-com-no-minimo-32-bytes
```

Defina uma chave aleatória e suficientemente longa (no mínimo 32 bytes para HS256). Não publique a chave em repositórios. Em exercícios locais, utilize uma chave própria de desenvolvimento.

### 6.2. Autenticação e usuários em memória

```java
@Configuration
@EnableWebSecurity
public class ConfiguracoesDeSeguranca {

    //--- injetando o valor da secret configurada no arquivo .properties. 
    //--- Ideal que a secret fique em outro arquivo separado dos fontes ou variável de ambiente
    @Value("${integracao-alunos-spring.jwt-secret}")
    private String secret;

    //--- Configurações para Autenticação dos Usuários
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        //--- configurando o serviço responsável por validar o usuário durante a autenticação
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); 
        provider.setPasswordEncoder(passwordEncoder); //--- configurando o responsável por criptografar a senha do usuário
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();//--- reponsável por criptografar a senha do usuário
    }

    @Bean
    //--- Configuração com os usuários da aplicação
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails usuarioCoordenado = User
                .withUsername("nomeDoCoordenador")
                .password(passwordEncoder.encode("123")) //--- criptografando a senha do usuário
                .roles(Roles.COORDENADOR.name())
                .build();

        UserDetails usuarioAluno = User
                .withUsername("nomeDoAluno")
                .password(passwordEncoder.encode("456")) //--- criptografando a senha do usuário
                .roles(Roles.ALUNO.name())
                .build();

        return new InMemoryUserDetailsManager(usuarioCoordenado, usuarioAluno); //--- configurando os usuários da aplicação em memória
    }

    // Os demais configurações estão nas seções seguintes.
}
```

### 6.3. Assinatura e validação de JWT

Adicione os beans abaixo à mesma classe `ConfiguracoesDeSeguranca` (não crie outra classe com o mesmo nome):

```java
//--- Configurações sobre o Token JWT
    @Bean
    public SecretKey secretKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSha256");
    }

    @Bean
    //--- Configuração da classe responsável pela assinatura do Token JWT. Utilizada na classe GeradorTokenJWTService
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    //--- Configuração da classe responsável por decodificar e validar integridade e autenticidade do Token JWT
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    //--- Configuração das classes responsáveis pela leitura das informações do Token JWT e por recuperar as roles (papéis) do usuário autenticado
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); //--- parâmetro onde as roles do usuário foram configuradas

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return converter;
    }
```


### 6.4. Autorização dos endpoints

Adicione o bean abaixo à classe de configuração:

```java
   //--- Configurações para Autorização de acessos
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) {
        return http
                .csrf(csrf -> csrf.disable())

                //--- configurando acesso as URLs
                .authorizeHttpRequests(auth -> auth
                        //--- acesso a documentação OpenAPI permitido sem autenticação
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**")
                        .permitAll()

                        // o login exige HTTP Basic.
                        .requestMatchers(HttpMethod.POST,"/autenticacao/login")
                        .authenticated()

                        //--- acesso ao cadastro de alunos somente para usuários autenticados que tenham role (papel) ALUNO
                        .requestMatchers(HttpMethod.POST, "/alunos")
                        .hasRole(Roles.ALUNO.name())

                        //--- acesso a atualização de alunos somente para usuários autenticados que tenham role (papel) ALUNO
                        .requestMatchers(HttpMethod.PUT, "/alunos/**")
                        .hasRole(Roles.ALUNO.name())

                        //--- acesso a consulta de alunos somente para usuários autenticados que tenham roles (papéis) ALUNO ou COORDENADOR
                        .requestMatchers(HttpMethod.GET, "/alunos/**")
                        .hasAnyRole(Roles.ALUNO.name(), Roles.COORDENADOR.name())

                        //--- acesso a remoção de alunos somente para usuários autenticados que tenham role (papel) COORDENADOR
                        .requestMatchers(HttpMethod.DELETE, "/alunos/**")
                        .hasRole(Roles.COORDENADOR.name())

                        //--- acesso as demais URLs somente para usuários autenticados independente a role (papel) do usuário
                        .anyRequest()
                        .authenticated())

                .httpBasic(Customizer.withDefaults()) //--- configurando tipo de autenticação. HTTP Basic

                .oauth2ResourceServer(resource -> resource.
                        jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))) //--- configuração ao token JWT

                .build();
    }
```

### 6.5. Classe completa com as configurações de segurança

Classe ConfiguracoesDeSeguranca com todas as configurações:

```java

@Configuration
@EnableWebSecurity
public class ConfiguracoesDeSeguranca {

    //--- injetando o valor da secret configurada no arquivo .properties. Ideal que a secret fique em outro arquivo separado dos fontes
    @Value("${integracao-alunos-spring.jwt-secret}")
    private String secret;

    //--- Configurações para Autenticação dos Usuários
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); //--- configurando o serviço responsável por validar o usuário durante a autenticação
        provider.setPasswordEncoder(passwordEncoder); //--- configurando o responsável por criptografar a senha do usuário
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();//--- reponsável por criptografar a senha do usuário
    }

    @Bean
    //--- Configuração com os usuários da aplicação
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails usuarioCoordenado = User
                .withUsername("nomeDoCoordenador")
                .password(passwordEncoder.encode("123")) //--- criptografando a senha do usuário
                .roles(Roles.COORDENADOR.name())
                .build();

        UserDetails usuarioAluno = User
                .withUsername("nomeDoAluno")
                .password(passwordEncoder.encode("456")) //--- criptografando a senha do usuário
                .roles(Roles.ALUNO.name())
                .build();

        return new InMemoryUserDetailsManager(usuarioCoordenado, usuarioAluno); //--- configurando os usuários da aplicação em memória
    }


    //--- Configurações sobre o Token JWT
    @Bean
    public SecretKey secretKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSha256");
    }

    @Bean
    //--- Configuração da classe responsável pela assinatura do Token JWT. Utilizada na classe GeradorTokenJWTService
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    //--- Configuração da classe responsável por decodificar e validar integridade e autenticidade do Token JWT
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    //--- Configuração das classes responsáveis pela leitura das informações do Token JWT e por recuperar as roles (papéis) do usuário autenticado
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); //--- parâmetro onde as roles do usuário foram configuradas

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return converter;
    }


    //--- Configurações para Autorização de acessos
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) {
        return http
                .csrf(csrf -> csrf.disable())

                //--- configurando acesso as URLs
                .authorizeHttpRequests(auth -> auth
                        //--- acesso a documentação OpenAPI permitido sem autenticação
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**")
                        .permitAll()

                        // o login exige HTTP Basic.
                        .requestMatchers(HttpMethod.POST,"/autenticacao/login")
                        .authenticated()

                        //--- acesso ao cadastro de alunos somente para usuários autenticados que tenham role (papel) ALUNO
                        .requestMatchers(HttpMethod.POST, "/alunos")
                        .hasRole(Roles.ALUNO.name())

                        //--- acesso a atualização de alunos somente para usuários autenticados que tenham role (papel) ALUNO
                        .requestMatchers(HttpMethod.PUT, "/alunos/**")
                        .hasRole(Roles.ALUNO.name())

                        //--- acesso a consulta de alunos somente para usuários autenticados que tenham roles (papéis) ALUNO ou COORDENADOR
                        .requestMatchers(HttpMethod.GET, "/alunos/**")
                        .hasAnyRole(Roles.ALUNO.name(), Roles.COORDENADOR.name())

                        //--- acesso a remoção de alunos somente para usuários autenticados que tenham role (papel) COORDENADOR
                        .requestMatchers(HttpMethod.DELETE, "/alunos/**")
                        .hasRole(Roles.COORDENADOR.name())

                        //--- acesso as demais URLs somente para usuários autenticados independente a role (papel) do usuário
                        .anyRequest()
                        .authenticated())

                .httpBasic(Customizer.withDefaults()) //--- configurando tipo de autenticação. HTTP Basic

                .oauth2ResourceServer(resource -> resource.
                        jwt(config -> config.jwtAuthenticationConverter(jwtAuthenticationConverter)))

                .build();
    }

}
```


## 7. Documentação com OpenAPI e Swagger UI

Com a dependência SpringDoc configurada, acesse:

- Documento OpenAPI: <http://localhost:8080/v3/api-docs>
- Swagger UI: <http://localhost:8080/swagger-ui/index.html>

### 7.1. Informações sobre a aplicação

```java
@Configuration
@SecurityScheme( //--- Configuração caso a aplicação utilize Spring Security. 
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic") //--- Autenticação por HTTP Basic
@SecurityScheme( //--- Configuração caso a aplicação utilize Spring Security
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",  //--- Utilização de Token JWT
        bearerFormat = "JWT")
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Integração Alunos Spring")
                        .description("Aplicação de exemplo com os módulos do Spring."));
    }

}
```


### 7.2. Documentando a autenticação

Adicione as anotações ao método `login` de `AutenticacaoResource`:

```java
    @Operation(
            summary = "Autenticar usuário.",
            description = "Autentica via HTTP Basic e retorna um Token JWT.",
            security = @SecurityRequirement(name = "basicAuth")) //--- somente se aplicação estiver usando Spring Security. Autenticação HTTP Basic
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário autenticado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TokenResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas.",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(Authentication authentication) {
        TokenResponse tokenResponse = geradorTokenService.gerar(authentication);
        return ResponseEntity.ok(tokenResponse);
    }
```

Na classe, inclua também:

```java
@Tag(name = "Autenticação", description = "API para autenticação dos usuários.")
```

### 7.3. Documentando o cadastro de alunos

Na classe `AlunoResource`, adicione:

```java
@Tag(name = "Alunos", description = "API de alunos com operações CRUD.")
```

No endpoint de cadastro, combine as anotações com o método existente:

```java
@Operation(
            summary = "Cadastrar aluno",
            description = "Responsável pelo cadastro de alunos.",
            security = @SecurityRequirement(name = "bearerAuth") //--- somente se aplicação estiver usando Spring Security
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Aluno cadastrado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AlunoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Falha durante a validação dos dados do aluno.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "401", description = "Não autenticado."),
            @ApiResponse(responseCode = "403", description = "Sem permissão.")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Dados do aluno a ser cadastrado.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AlunoRequest.class)))
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoRequest novoAluno) {
        AlunoResponse alunoResponse = alunoService.cadastrar(novoAluno);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(alunoResponse);
    }
```

**Observação:** A anotação de request body do OpenAPI e a do Spring Web são diferentes.

### 7.4. Documentando uma consulta paginada

```java
    @Operation(
            summary = "Listar alunos",
            description = "Listar alunos cadastrados com paginação.",
            security = @SecurityRequirement(name = "bearerAuth") //--- somente se aplicação estiver usando Spring Security
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista com os alunos cadastrados."
            ),
            @ApiResponse(responseCode = "401", description = "Não autenticado."),
            @ApiResponse(responseCode = "403", description = "Sem permissão.")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AlunoResponse>> consultar(
            @Parameter(description = "Nome do aluno") @RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "nome") Pageable paginacao) {
        Page<AlunoResponse> alunosCadastrados = alunoService.consultar(nome, paginacao);
        return ResponseEntity.ok(alunosCadastrados);
    }
```

O `@ParameterObject` ajuda a expor os parâmetros de `Pageable` no Swagger UI.

### 7.5. Documentando uma consulta por identificador

```java
    @Operation(
            summary = "Consultar aluno pelo código",
            description = "Consulta um aluno pelo identificador.",
            security = @SecurityRequirement(name = "bearerAuth") //--- somente se aplicação estiver usando Spring Security
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aluno encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AlunoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aluno não encontrado",
                    content = @Content
            ),
            @ApiResponse(responseCode = "401", description = "Não autenticado."),
            @ApiResponse(responseCode = "403", description = "Sem permissão.")
    })
    @GetMapping(path = "/{alunoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoResponse> consultarPorCodigo(@Parameter(required = true, description = "Código do aluno") @PathVariable Long alunoId) {
        AlunoResponse alunoResponse = alunoService.consultarPorCodigo(alunoId);
        return ResponseEntity.ok(alunoResponse);
    }
```
