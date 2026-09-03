package br.com.fiap.service;

import br.com.fiap.dto.AlunoRequest;
import br.com.fiap.dto.AlunoResponse;
import br.com.fiap.entity.Aluno;
import br.com.fiap.exception.AlunoNaoEncontradoException;
import br.com.fiap.exception.RmJaCadastradoException;
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
        Optional<Aluno> retornoConsulta = alunoRepository.findByRm(alunoRequest.rm());
        if (retornoConsulta.isPresent()) {
            throw new RmJaCadastradoException("O [rm=" + alunoRequest.rm() + "] informado pertence a outro aluno");
        }

        Aluno novoAluno = alunoRequest.toEntity();
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

    public AlunoResponse atualizar(Long codigo, Aluno aluno) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(codigo);
        if (retornoConsulta.isEmpty()) {
            throw new AlunoNaoEncontradoException("Aluno [codigo=" + codigo + "] não encontrado.");
        }

        Aluno alunoCadastrado = retornoConsulta.get();
        alunoCadastrado.setNome(aluno.getNome());

        Aluno alunoAtualizado = alunoRepository.save(alunoCadastrado);

        return AlunoResponse.from(alunoAtualizado);
    }

    public void remover(Long codigo) {
        Optional<Aluno> retornoConsulta = alunoRepository.findById(codigo);
        if (retornoConsulta.isPresent()) {
            alunoRepository.deleteById(codigo);
        }
    }

}
