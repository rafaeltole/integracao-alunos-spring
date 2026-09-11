package br.com.fiap.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "aluno_seq", allocationSize = 1)
    private Long id;

    private String nome;

    @OneToOne(mappedBy = "aluno", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private Perfil perfil;

    @OneToMany(mappedBy = "aluno")
    private List<Matricula> matriculas;

    private LocalDate cadastradoEm;

    private LocalDateTime atualizadoEm;

    @PrePersist
    public void configuraDataCadastro() {
        cadastradoEm = LocalDate.now();
    }

    @PreUpdate
    public void configuraDataAtualizacao() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }

    public LocalDate getCadastradoEm() {
        return cadastradoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

}
