package br.com.fiap.exception;

public class AlunoNaoEncontradoException extends RuntimeException {

    private static final String MSG = "Aluno [id=%d] não encontrado.";

    public AlunoNaoEncontradoException(Long id) {
        super(String.format(MSG, id));
    }

}
