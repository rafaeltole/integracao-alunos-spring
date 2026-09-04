package br.com.fiap.entity;

import jakarta.persistence.*;

@Entity
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "disciplina_seq", allocationSize = 1)
    private Long id;

    private String nome;


}
