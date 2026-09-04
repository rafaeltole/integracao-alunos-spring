package br.com.fiap.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;


@Entity
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "turma_seq", allocationSize = 1)
    private Long id;

    private String nome;

    private String status;

    @OneToMany(mappedBy = "turma")
    private List<Matricula> matriculas;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    private LocalDate cadastradoEm;

    private LocalDate atualizadoEm;


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public LocalDate getCadastradoEm() {
        return cadastradoEm;
    }

    public void setCadastradoEm(LocalDate cadastradoEm) {
        this.cadastradoEm = cadastradoEm;
    }

    public LocalDate getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDate atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
