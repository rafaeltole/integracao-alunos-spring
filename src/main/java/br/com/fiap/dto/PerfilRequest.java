package br.com.fiap.dto;

import br.com.fiap.entity.Perfil;
import jakarta.validation.constraints.NotBlank;

public record PerfilRequest(
        @NotBlank(message = "O título deve ser informado") String titulo,
        @NotBlank(message = "A descrição deve ser informada") String descricao) {

    public Perfil toEntity() {
        Perfil perfil = new Perfil();
        perfil.setTitulo(titulo);
        perfil.setDescricao(descricao);
        return perfil;
    }

}
