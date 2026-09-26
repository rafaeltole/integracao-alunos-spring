package br.com.fiap.dto;

import br.com.fiap.entity.Aluno;

import java.time.LocalDate;

public record AlunoResponse(
        Long id,
        String nome,
        LocalDate cadastradoEm,
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
