package br.com.fiap.controller;

import br.com.fiap.dto.TokenResponse;
import br.com.fiap.service.GeradorTokenJWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autenticacao")
public class AutenticacaoResource {

    private GeradorTokenJWTService geradorTokenService;

    public AutenticacaoResource(GeradorTokenJWTService geradorTokenService) {
        this.geradorTokenService = geradorTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(Authentication authentication) {
        TokenResponse tokenResponse = geradorTokenService.gerar(authentication);
        return ResponseEntity.ok(tokenResponse);
    }

}
