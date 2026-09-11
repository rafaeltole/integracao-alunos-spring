package br.com.fiap.dto;

import br.com.fiap.entity.Aluno;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlunoRequest(
        @NotBlank(message = "O nome deve ser informado")  String nome,
        @Valid @NotNull(message = "O perfil deve ser informado") PerfilRequest perfil) {

    public Aluno toEntity() {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        return aluno;
    }

}
