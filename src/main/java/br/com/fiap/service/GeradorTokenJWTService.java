package br.com.fiap.service;

import br.com.fiap.dto.TokenResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GeradorTokenJWTService {

    private static final String TIPO_TOKEN = "Bearer";
    private static final Integer VALIDADE_TOKEN_EM_SEGUNDOS = 300; //--- 5 minutos

    //--- Responsável por assinar o Token JWT, permitindo verificar sua integridade e autenticidade
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
