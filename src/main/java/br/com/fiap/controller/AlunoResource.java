package br.com.fiap.controller;

import br.com.fiap.dto.*;
import br.com.fiap.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoResource {

    private final AlunoService alunoService;

    public AlunoResource(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoRequest novoAluno) {
        AlunoResponse alunoResponse = alunoService.cadastrar(novoAluno);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(alunoResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AlunoResponse>> consultar(
            @RequestParam(required = false) String nome,
            @PageableDefault(page = 0, size = 10, sort = "nome") Pageable paginacao) {
        Page<AlunoResponse> alunosCadastrados = alunoService.consultar(nome, paginacao);
        return ResponseEntity.ok(alunosCadastrados);
    }

    @GetMapping(path = "/{alunoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoResponse> consultarPorCodigo(@PathVariable Long alunoId) {
        AlunoResponse alunoResponse = alunoService.consultarPorCodigo(alunoId);
        return ResponseEntity.ok(alunoResponse);
    }

    @GetMapping(path = "/{alunoId}/turmas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TurmaResponse>> consultarTurmas(@PathVariable Long alunoId) {
        List<TurmaResponse> turmas = alunoService.consultarTurmas(alunoId);
        return ResponseEntity.ok(turmas);
    }

    @PutMapping(path = "/{alunoId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable Long alunoId, @Valid @RequestBody AlunoRequest aluno) {
        AlunoResponse alunoResponse = alunoService.atualizar(alunoId, aluno);
        return ResponseEntity.ok(alunoResponse);
    }

    @DeleteMapping(path = "/{alunoId}")
    public ResponseEntity<Void> remover(@PathVariable Long alunoId) {
        alunoService.remover(alunoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{alunoId}/matriculas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MatriculaResponse>> consultarMatriculas(@PathVariable Long alunoId) {
        List<MatriculaResponse> matriculas = alunoService.consultarMatriculas(alunoId);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping(path = "/{alunoId}/perfil", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PerfilResponse> consultarPerfil(@PathVariable Long alunoId) {
        PerfilResponse perfilResponse = alunoService.consultarPerfil(alunoId);
        return ResponseEntity.ok(perfilResponse);
    }

}
