package br.com.fiap.dto;

import br.com.fiap.entity.Perfil;
import jakarta.validation.constraints.NotBlank;

public record PerfilRequest(
        @NotBlank(message = "O titulo deve ser informado") String titulo,
        @NotBlank(message = "O descricao deve ser informado") String descricao) {

    public Perfil toEntity() {
        Perfil perfil = new Perfil();
        perfil.setTitulo(titulo);
        perfil.setDescricao(descricao);
        return perfil;
    }

}
