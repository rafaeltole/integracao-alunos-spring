package br.com.fiap;

import br.com.fiap.entity.*;
import br.com.fiap.repository.AlunoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.base.TextualTSFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CargaInicialRunner implements CommandLineRunner {

    private final TextualTSFactory textualTSFactory;
    @PersistenceContext
    private EntityManager em;

    private AlunoRepository alunoRepository;

    public CargaInicialRunner(AlunoRepository alunoRepository, TextualTSFactory textualTSFactory) {
        this.alunoRepository = alunoRepository;
        this.textualTSFactory = textualTSFactory;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        final AtomicInteger geradorIndice = new AtomicInteger();

        NOMES.forEach(nome -> {
            //--- Cadastro de alunos
            if (!alunoRepository.existsByNome(nome)) {
                final Aluno aluno = new Aluno();
                aluno.setNome(nome);

                Perfil perfil = new Perfil();
                perfil.setTitulo("Título perfil | " + nome);
                perfil.setDescricao("Descrição perfil | " + nome);

                aluno.setPerfil(perfil);
                perfil.setAluno(aluno);

                alunoRepository.save(aluno);

                int indice = geradorIndice.incrementAndGet();
                Coordenador coordenador = new Coordenador();
                coordenador.setNome("Coordenador - " + indice);
                em.persist(coordenador);

                Curso curso = new Curso();
                curso.setNome("Curso - " + indice);
                curso.setStatus("EM_ANDAMENTO");
                curso.setCoordenador(coordenador);
                em.persist(curso);

                Turma turma = new Turma();
                turma.setCurso(curso);
                turma.setNome("Turma - " + indice);
                turma.setStatus("EM_ANDAMENTO");
                em.persist(turma);

                Matricula matricula = new Matricula();
                matricula.setRm(String.valueOf(indice * 100));
                matricula.setStatus("ATIVA");
                matricula.setTurma(turma);
                matricula.setAluno(aluno);
                em.persist(matricula);
                em.flush();
            }
        });
    }


    //---
    private static final List<String> NOMES = List.of(
            "Cassio",
            "Cauã",
            "Davi",
            "Diego",
            "Enzo",
            "Felipe",
            "Guilherme",
            "Gustavo",
            "Henrique",
            "Hugo",
            "Isabely",
            "João",
            "Julia Spanopoulos",
            "Julia Valerio",
            "Luana",
            "Lucas",
            "Mateus Ribeiro",
            "Matheus Peres",
            "Paulo",
            "Pedro",
            "Renan",
            "Rhayssa",
            "Ruan",
            "Ryan",
            "Thiago",
            "Vitor Godoi",
            "Vitor Domingues"
    );

}
