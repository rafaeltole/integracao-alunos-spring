package br.com.fiap.repository;


import br.com.fiap.entity.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    //--- consulta utilizando atributos da entidade e paginação
    Page<Aluno> findByNomeContainsIgnoringCase(String nome, Pageable paginacao);

    //--- verificação utilizando atributos da entidade
    boolean existsByNome(String aluno);
}
