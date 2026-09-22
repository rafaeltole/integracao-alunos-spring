package br.com.fiap.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity //--- configurando a classe como uma entidade JPA
public class Aluno {

    @Id //--- chave primária | PK
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //--- como as chaves primárias (PK) serão geradas
    @SequenceGenerator(name = "aluno_seq", allocationSize = 1) //--- responsável pela geração das chaves primárias (PK)
    private Long id;

    private String nome;

    //--- relacionamento bidirecional
    @OneToOne(mappedBy = "aluno", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}) //--- Configurando relacionamento com cascade
    private Perfil perfil;

    //--- relacionamento bidirecional
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
