package br.com.fiap.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Integer expiresIn) {
}
