package com.negociofechado.modulos.categoria.controller;
import com.negociofechado.modulos.categoria.dto.CategoriaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Categorias", description = "Endpoints de categorias de serviços")
public interface CategoriaDocument {

    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias ativas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorias")
    })
    ResponseEntity<List<CategoriaResponse>> listar();
}
