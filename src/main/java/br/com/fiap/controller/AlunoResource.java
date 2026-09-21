package br.com.fiap.controller;

import br.com.fiap.dto.*;
import br.com.fiap.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoResource {

    private AlunoService alunoService;

    public AlunoResource(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoRequest novoAluno) {
        AlunoResponse alunoResponse = alunoService.cadastrar(novoAluno);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(alunoResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AlunoResponse>> consultar() {
        List<AlunoResponse> alunosCadastrados = alunoService.consultar();
        return ResponseEntity.ok(alunosCadastrados);
    }

    @GetMapping(path = "/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoResponse> consultarPorCodigo(@PathVariable Long codigo) {
        AlunoResponse alunoResponse = alunoService.consultarPorCodigo(codigo);
        return ResponseEntity.ok(alunoResponse);
    }

    @GetMapping(params = "nome", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AlunoResponse>> consultarPorNome(@RequestParam String nome) {
        List<AlunoResponse> alunosCadastrados = alunoService.consultarPorNome(nome);
        return ResponseEntity.ok(alunosCadastrados);
    }

    @GetMapping(path = "/{id}/turmas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TurmaResponse>> consultarTurmas(@PathVariable Long id) {
        List<TurmaResponse> turmas = alunoService.consultarTurmas(id);
        return ResponseEntity.ok(turmas);
    }

    @PutMapping(path = "/{codigo}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable Long codigo, @Valid @RequestBody AlunoRequest aluno) {
        AlunoResponse alunoResponse = alunoService.atualizar(codigo, aluno);
        return ResponseEntity.ok(alunoResponse);
    }

    @DeleteMapping(path = "/{codigo}")
    public ResponseEntity<Object> remover(@PathVariable Long codigo) {
        alunoService.remover(codigo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}/matriculas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MatriculaResponse>> consultarMatriculas(@PathVariable Long id) {
        List<MatriculaResponse> matriculas = alunoService.consultarMatriculas(id);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping(path = "/{id}/perfil", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PerfilResponse> consultarPerfil(@PathVariable Long id) {
        PerfilResponse perfilResponse = alunoService.consultarPerfil(id);
        return ResponseEntity.ok(perfilResponse);
    }

}
