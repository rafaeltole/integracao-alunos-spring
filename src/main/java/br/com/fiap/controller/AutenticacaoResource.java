package br.com.fiap.controller;

import br.com.fiap.dto.TokenResponse;
import br.com.fiap.service.GeradorTokenJWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autenticacao")
@Tag(name = "Autenticação", description = "API para autenticação dos usuários.") //--- agrupando os endpoints por API
public class AutenticacaoResource {

    private GeradorTokenJWTService geradorTokenService;

    public AutenticacaoResource(GeradorTokenJWTService geradorTokenService) {
        this.geradorTokenService = geradorTokenService;
    }

    @Operation(
            summary = "Autenticar usuário.",
            description = "Responsável pela autenticação do usuário",
            security = @SecurityRequirement(name = "basicAuth")) //---configuração necessária somente se aplicação estiver usando Spring Security. Autenticação HTTP Basic
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
                    description = "Usuário ou senha inválido.",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(Authentication authentication) {
        TokenResponse tokenResponse = geradorTokenService.gerar(authentication);
        return ResponseEntity.ok(tokenResponse);
    }

}
