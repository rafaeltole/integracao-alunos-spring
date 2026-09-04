package br.com.fiap.dto;

import br.com.fiap.entity.Matricula;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record MatriculaResponse(
        String rm,
        String status,
        LocalDate cadastradoEm) {

    public static List<MatriculaResponse> from(List<Matricula> matriculas) {
        List<MatriculaResponse> matriculasResponse = new ArrayList<>();

        for (Matricula matricula : matriculas) {
            matriculasResponse.add(new MatriculaResponse(
                    matricula.getRm(),
                    matricula.getStatus(),
                    matricula.getCadastradoEm()
            ));
        }

        return matriculasResponse;
    }

}
