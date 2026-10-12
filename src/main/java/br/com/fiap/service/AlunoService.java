package br.com.fiap.service;

import br.com.fiap.dto.*;
import br.com.fiap.entity.Aluno;
import br.com.fiap.entity.Matricula;
import br.com.fiap.entity.Perfil;
import br.com.fiap.entity.Turma;
import br.com.fiap.exception.AlunoNaoEncontradoException;
import br.com.fiap.repository.AlunoRepository;
import br.com.fiap.repository.TurmaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;

    public AlunoService(AlunoRepository alunoRepository, TurmaRepository turmaRepository) {
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
    }

    public AlunoResponse cadastrar(AlunoRequest alunoRequest) {
        Aluno novoAluno = alunoRequest.toEntity();

        PerfilRequest perfilRequest = alunoRequest.perfil();
        Perfil perfil = perfilRequest.toEntity();

        novoAluno.setPerfil(perfil);
        perfil.setAluno(novoAluno);

        Aluno alunoCadastrado = alunoRepository.save(novoAluno);

        return AlunoResponse.from(alunoCadastrado);
    }

    //--- consulta de alunos utilizando paginação e filtro por nome
    public Page<AlunoResponse> consultar(String nome, Pageable paginacao) {
        Page<Aluno> alunosCadastrados;
        if (Objects.isNull(nome)) {
            alunosCadastrados = alunoRepository.findAll(paginacao);
        } else {
            alunosCadastrados = alunoRepository.findByNomeContainsIgnoringCase(nome, paginacao);
        }

        return alunosCadastrados.map(AlunoResponse::from);
    }

    public AlunoResponse consultarPorCodigo(Long alunoId) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(alunoId);
        if (retornoConsulta.isPresent()) {
            return AlunoResponse.from(retornoConsulta.get());
        }
        throw new AlunoNaoEncontradoException(alunoId);
    }

    public AlunoResponse atualizar(Long alunoId, AlunoRequest aluno) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(alunoId);
        if (retornoConsulta.isEmpty()) {
            throw new AlunoNaoEncontradoException(alunoId);
        }

        Aluno alunoCadastrado = retornoConsulta.get();
        alunoCadastrado.setNome(aluno.nome());

        PerfilRequest perfilRequest = aluno.perfil();

        Perfil perfil = alunoCadastrado.getPerfil();
        perfil.setTitulo(perfilRequest.titulo());
        perfil.setDescricao(perfilRequest.descricao());

        Aluno alunoAtualizado = alunoRepository.save(alunoCadastrado);

        return AlunoResponse.from(alunoAtualizado);
    }

    public void remover(Long alunoId) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(alunoId);
        if (retornoConsulta.isEmpty()) {
            throw new AlunoNaoEncontradoException(alunoId);
        }
        alunoRepository.deleteById(alunoId);
    }

    public List<MatriculaResponse> consultarMatriculas(Long alunoId) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(alunoId);
        if (retornoConsulta.isEmpty()) {
            throw new AlunoNaoEncontradoException(alunoId);
        }

        Aluno aluno = retornoConsulta.get();
        List<Matricula> matriculas = aluno.getMatriculas();

        return MatriculaResponse.from(matriculas);
    }

    public List<TurmaResponse> consultarTurmas(Long alunoId) {
        List<Turma> turmas = turmaRepository.findByMatriculasAlunoId(alunoId);

        return TurmaResponse.from(turmas);
    }

    public PerfilResponse consultarPerfil(Long alunoId) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(alunoId);
        if (retornoConsulta.isPresent()) {
            Aluno aluno = retornoConsulta.get();
            Perfil perfil = aluno.getPerfil();

            return PerfilResponse.from(alunoId, perfil);
        }
        throw new AlunoNaoEncontradoException(alunoId);
    }

}
