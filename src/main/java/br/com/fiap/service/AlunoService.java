package br.com.fiap.service;

import br.com.fiap.dto.*;
import br.com.fiap.entity.Aluno;
import br.com.fiap.entity.Matricula;
import br.com.fiap.entity.Perfil;
import br.com.fiap.exception.AlunoNaoEncontradoException;
import br.com.fiap.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AlunoService {

    private AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
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

    public List<AlunoResponse> consultar() {
        List<AlunoResponse> alunosResponse = new ArrayList<>();

        List<Aluno> alunosCadastrados = alunoRepository.findAll();

        for (Aluno alunoCadastrado : alunosCadastrados) {
            AlunoResponse alunoResponse = AlunoResponse.from(alunoCadastrado);
            alunosResponse.add(alunoResponse);
        }

        return alunosResponse;
    }

    public AlunoResponse consultarPorCodigo(Long codigo) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(codigo);
        if (retornoConsulta.isPresent()) {
            return AlunoResponse.from(retornoConsulta.get());
        }
        throw new AlunoNaoEncontradoException("Aluno [codigo=" + codigo + "] não encontrado.");
    }

    public List<AlunoResponse> consultarPorNome(String nome) {
        List<AlunoResponse> alunosResponse = new ArrayList<>();

        List<Aluno> alunosCadastrados = alunoRepository.findByNome(nome);

        for (Aluno alunoCadastrado : alunosCadastrados) {
            AlunoResponse alunoResponse = AlunoResponse.from(alunoCadastrado);
            alunosResponse.add(alunoResponse);
        }

        return alunosResponse;
    }

    public AlunoResponse atualizar(Long codigo, AlunoRequest aluno) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(codigo);
        if (retornoConsulta.isEmpty()) {
            throw new AlunoNaoEncontradoException("Aluno [codigo=" + codigo + "] não encontrado.");
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

    public void remover(Long codigo) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(codigo);
        if (retornoConsulta.isPresent()) {
            alunoRepository.deleteById(codigo);
        }
    }

    public List<MatriculaResponse> consultarMatriculas(Long alunoId) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(alunoId);
        if (retornoConsulta.isEmpty()) {
            throw new AlunoNaoEncontradoException("Aluno [id=" + alunoId + "] não encontrado");
        }

        Aluno aluno = retornoConsulta.get();
        List<Matricula> matriculas = aluno.getMatriculas();

        List<MatriculaResponse> matriculasResponse = MatriculaResponse.from(matriculas);

        return matriculasResponse;
    }

    public PerfilResponse consultarPerfil(Long alunoId) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(alunoId);
        if (retornoConsulta.isPresent()) {
            Aluno aluno = retornoConsulta.get();
            Perfil perfil = aluno.getPerfil();

            return PerfilResponse.from(alunoId, perfil);
        }
        throw new AlunoNaoEncontradoException("Aluno [id="+ alunoId+"] não encontrado");
    }

}
