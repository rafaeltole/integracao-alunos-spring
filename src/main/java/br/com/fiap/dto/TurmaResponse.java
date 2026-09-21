package br.com.fiap.dto;

import br.com.fiap.entity.Turma;

import java.util.List;

public record TurmaResponse(
        Long id,
//        Long cursoId,
        String nome,
        String status) {

    public static TurmaResponse from(Turma turma) {
        return new TurmaResponse(
                turma.getId(),
                turma.getNome(),
                turma.getStatus()
        );
    }

    public static List<TurmaResponse> from(List<Turma> turmas) {
        return turmas.stream().map(turma -> new TurmaResponse(
                turma.getId(),
                turma.getNome(),
                turma.getStatus()
        )).toList();
    }
}
