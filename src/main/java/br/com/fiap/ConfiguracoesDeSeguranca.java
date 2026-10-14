package br.com.fiap;

import br.com.fiap.enums.Roles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

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
                        jwt(config -> config.jwtAuthenticationConverter(jwtAuthenticationConverter))) //--- configuração ao token JWT

                .build();
    }

}
