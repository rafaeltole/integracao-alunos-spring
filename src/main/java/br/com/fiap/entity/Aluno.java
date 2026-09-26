package br.com.fiap.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity //--- configurando a classe como uma entidade JPA
public class Aluno extends EntidadeBase {

    @Id //--- chave primária | PK
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //--- como as chaves primárias (PK) serão geradas
    @SequenceGenerator(name = "aluno_seq", allocationSize = 1) //--- responsável pela geração das chaves primárias (PK)
    private Long id;

    private String nome;

    //--- relacionamento bidirecional
    @OneToOne(mappedBy = "aluno", cascade = { //--- Configurando relacionamento com cascade
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE})
    private Perfil perfil;

    //--- relacionamento bidirecional
    @OneToMany(mappedBy = "aluno")
    private List<Matricula> matriculas;

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

}
