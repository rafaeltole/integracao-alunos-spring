package br.com.fiap.dto;

import br.com.fiap.entity.Aluno;

public record AlunoResponse(
        Long id,
        String nome) {

    public static AlunoResponse from(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome()
        );
    }

}
