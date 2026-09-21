package br.com.fiap.repository;


import br.com.fiap.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    List<Aluno> findByNomeContainsIgnoringCase(String nome);

    boolean existsByNome(String aluno);

}
