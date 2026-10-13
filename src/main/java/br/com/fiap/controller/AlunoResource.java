package br.com.fiap.controller;

import br.com.fiap.dto.*;
import br.com.fiap.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Alunos", description = "API de Alunos com as operações CRUD.")
public class AlunoResource {

    private final AlunoService alunoService;

    public AlunoResource(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @Operation(
            summary = "Cadastrar aluno",
            description = "Responsável pelo cadastro de alunos.",
            security = @SecurityRequirement(name = "bearerAuth") //---configuração necessária somente se aplicação estiver usando Spring Security
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Aluno cadastrado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AlunoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Falha durante a validação dos dados do aluno.",
                    content = @Content
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dados do aluno a ser cadastrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlunoRequest.class)))
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoRequest novoAluno) {
        AlunoResponse alunoResponse = alunoService.cadastrar(novoAluno);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(alunoResponse);
    }

    @Operation(
            summary = "Listar alunos",
            description = "Listar alunos cadastrados.",
            security = @SecurityRequirement(name = "bearerAuth") //---configuração necessária somente se aplicação estiver usando Spring Security
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista com os alunos cadastrados."
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AlunoResponse>> consultar(
            @Parameter(description = "Nome do aluno") @RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "nome") Pageable paginacao) {
        Page<AlunoResponse> alunosCadastrados = alunoService.consultar(nome, paginacao);
        return ResponseEntity.ok(alunosCadastrados);
    }

    @Operation(
            summary = "Consultar aluno pelo código",
            description = "Consulta aluno utilizando o identificador.",
            security = @SecurityRequirement(name = "bearerAuth") //---configuração necessária somente se aplicação estiver usando Spring Security
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aluno encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AlunoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aluno não encontrado",
                    content = @Content
            )
    })
    @GetMapping(path = "/{alunoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoResponse> consultarPorCodigo(@Parameter(required = true, description = "Código do aluno") @PathVariable Long alunoId) {
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
