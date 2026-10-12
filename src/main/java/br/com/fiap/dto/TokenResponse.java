package br.com.fiap.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Integer expiresIn) {
}
