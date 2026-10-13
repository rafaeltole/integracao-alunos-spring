package br.com.fiap.dto;

import br.com.fiap.entity.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PerfilRequest(
        @Schema(description = "Título do perfil do aluno")
        @NotBlank(message = "O título deve ser informado") String titulo,

        @Schema(description = "Descrição para o perfil do aluno")
        @NotBlank(message = "A descrição deve ser informada") String descricao) {

    public Perfil toEntity() {
        Perfil perfil = new Perfil();
        perfil.setTitulo(titulo);
        perfil.setDescricao(descricao);
        return perfil;
    }

}
