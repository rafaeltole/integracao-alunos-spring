package br.com.fiap.entity;

import jakarta.persistence.*;

@Entity
public class Coordenador {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "coordenador_seq", allocationSize = 1)
    private Long id;

    private String nome;

    @OneToOne(mappedBy = "coordenador")
    private Curso curso;

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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
