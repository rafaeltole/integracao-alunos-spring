package br.com.fiap.service;


import br.com.fiap.dto.AlunoRequest;
import br.com.fiap.entity.Aluno;
import br.com.fiap.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class AlunoServiceIT {

    @Autowired
    private AlunoService service;

    @MockitoBean
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


}
