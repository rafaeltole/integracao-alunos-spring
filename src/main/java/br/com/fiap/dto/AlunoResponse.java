package br.com.fiap.dto;

import br.com.fiap.entity.Aluno;

public record AlunoResponse(
        Long codigo,
        String rm,
        String nome) {

    public static AlunoResponse from(Aluno aluno) {
        return new AlunoResponse(
                aluno.getCodigo(),
                aluno.getRm(),
                aluno.getNome()
        );
    }

}
