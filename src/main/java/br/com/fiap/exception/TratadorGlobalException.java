package br.com.fiap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorGlobalException {

    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ProblemDetail tratar(AlunoNaoEncontradoException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Aluno não encontrado");
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(RmJaCadastradoException.class)
    public ProblemDetail tratar(RmJaCadastradoException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("O RM informado pertence a outro aluno");
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratar(MethodArgumentNotValidException e) {
        Map<String, String> campos = new HashMap<>();

        e.getFieldErrors().forEach(erro -> {
            campos.put(erro.getField(), erro.getDefaultMessage());
        });

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Dados inválidos");
        problemDetail.setProperty("campos", campos);

        return problemDetail;
    }

}
