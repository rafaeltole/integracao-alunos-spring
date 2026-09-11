package br.com.fiap.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "curso_seq", allocationSize = 1)
    private Long id;

    @OneToMany(mappedBy = "curso")
    private List<Turma> turmas;

    @ManyToMany
    @JoinTable(
            name = "disciplina_curso",
            joinColumns = @JoinColumn(name = "curso_id"),
            inverseJoinColumns = @JoinColumn(name = "disciplina_id")
    )
    private List<Disciplina> disciplinas;

    @ManyToOne
    @JoinColumn(name = "coordenador_id")
    private Coordenador coordenador;

    private String nome;

    private String status;

    private LocalDate cadastradoEm;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }

    public void setTurmas(List<Turma> turmas) {
        this.turmas = turmas;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public Coordenador getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(Coordenador coordenador) {
        this.coordenador = coordenador;
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

    public LocalDate getCadastradoEm() {
        return cadastradoEm;
    }

    public void setCadastradoEm(LocalDate cadastradoEm) {
        this.cadastradoEm = cadastradoEm;
    }
}
