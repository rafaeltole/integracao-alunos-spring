package br.com.fiap.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
        @Schema(description = "Access Token")
        String accessToken,

        @Schema(description = "Tipo do Token")
        String tokenType,

        @Schema(description = "Tempo validade do Token")
        Integer expiresIn) {
}
