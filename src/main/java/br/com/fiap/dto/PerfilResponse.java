package br.com.fiap.dto;

import br.com.fiap.entity.Perfil;

public record PerfilResponse(
        Long id,
        Long alunoId,
        String titulo,
        String descricao) {

    public static PerfilResponse from(Long alunoId, Perfil perfil) {
        return new PerfilResponse(
                perfil.getId(),
                alunoId,
                perfil.getTitulo(),
                perfil.getDescricao()
        );
    }

}
