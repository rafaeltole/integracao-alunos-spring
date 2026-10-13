package br.com.fiap.dto;

import br.com.fiap.entity.Aluno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlunoRequest(
        @Schema(description = "Nome do aluno")
        @NotBlank(message = "O nome deve ser informado") String nome,

        @Schema(description = "Perfil do aluno")
        @Valid @NotNull(message = "O perfil deve ser informado") PerfilRequest perfil) {

    public Aluno toEntity() {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        return aluno;
    }

}
