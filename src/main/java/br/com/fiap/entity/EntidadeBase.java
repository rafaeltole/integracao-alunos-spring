package br.com.fiap.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDate;

@MappedSuperclass
public class EntidadeBase {

    private LocalDate cadastradoEm;

    private LocalDate atualizadoEm;

    public LocalDate getCadastradoEm() {
        return cadastradoEm;
    }

    @PrePersist
    public void configurarDataCadastrado() {
        this.cadastradoEm = LocalDate.now();
    }

    public LocalDate getAtualizadoEm() {
        return atualizadoEm;
    }

    @PreUpdate
    public void configurarDataDeAtualizacao() {
        this.atualizadoEm = LocalDate.now();
    }

}
