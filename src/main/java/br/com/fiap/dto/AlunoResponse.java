package br.com.fiap.dto;

import br.com.fiap.entity.Aluno;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AlunoResponse(
        @Schema(description = "Identificador do aluno")
        Long id,

        @Schema(description = "Nome do aluno")
        String nome,

        @Schema(description = "Data em que o aluno foi cadastrado")
        LocalDate cadastradoEm,

        @Schema(description = "Data em que o aluno foi atualizado")
        LocalDate atualizadoEm) {

    public static AlunoResponse from(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCadastradoEm(),
                aluno.getAtualizadoEm()
        );
    }

}
