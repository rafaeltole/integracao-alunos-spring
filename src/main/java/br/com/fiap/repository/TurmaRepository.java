package br.com.fiap.repository;

import br.com.fiap.entity.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByMatriculasAlunoId(Long alunoId);

    @Query(value = "SELECT t FROM Turma t JOIN t.matriculas m JOIN m.aluno a WHERE a.id = :alunoId")
    List<Turma> consultarTurmasPorAluno(@Param("alunoId") Long alunoId);

}
