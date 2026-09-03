package br.com.fiap.service;


import br.com.fiap.dto.AlunoRequest;
import br.com.fiap.entity.Aluno;
import br.com.fiap.exception.RmJaCadastradoException;
import br.com.fiap.repository.AlunoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {

    @InjectMocks
    private AlunoService service;

    @Mock
    private AlunoRepository repository;

    @Test
    public void dadoUmAlunoValidoCadastrarNoBancoDeDados() {
        AlunoRequest novoAluno = new AlunoRequest("123", "Aluno Test");

        Aluno alunoCadastrado = novoAluno.toEntity();
        alunoCadastrado.setCodigo(1L);

        Mockito.when(repository.save(Mockito.any(Aluno.class))).thenReturn(alunoCadastrado);

        service.cadastrar(novoAluno);

        Mockito.verify(repository).save(Mockito.any(Aluno.class));
    }

    @Test
    public void dadoUmAlunoComRmJaCadastroLancarExcecao() {
        AlunoRequest novoAluno = new AlunoRequest("123", "Aluno Test");

        Aluno alunoCadastrado = new Aluno();
        alunoCadastrado.setNome("Aluno Teste");
        Mockito.when(repository.findByRm(Mockito.anyString())).thenReturn(Optional.of(alunoCadastrado));

        RmJaCadastradoException excecao = Assertions.assertThrows(RmJaCadastradoException.class, () -> {
            service.cadastrar(novoAluno);
        });

        Assertions.assertEquals("O [rm=123] informado pertence a outro aluno", excecao.getMessage());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Aluno.class));
    }

}
