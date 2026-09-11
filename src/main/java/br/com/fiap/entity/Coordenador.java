package br.com.fiap.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Coordenador {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "coordenador_seq", allocationSize = 1)
    private Long id;

    private String nome;

    @OneToMany(mappedBy = "coordenador")
    private List<Curso> cursos;

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

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }
}
