package br.com.fiap.dto;

import br.com.fiap.entity.Aluno;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlunoResponse(
        Long id,
        String nome,
        LocalDate cadastradoEm,
        LocalDateTime atualizadoEm) {

    public static AlunoResponse from(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCadastradoEm(),
                aluno.getAtualizadoEm()
        );
    }

}
