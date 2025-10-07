package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.CategoriaDTO;
import com.Pos.RestauranteApp.model.Categoria;
import com.Pos.RestauranteApp.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    //  Listar todas las categorías
    @GetMapping
    public List<CategoriaDTO> listarCategorias() {
        return categoriaService.listarCategorias()
                .stream()
                .map(categoriaService::convertirADTO)
                .collect(Collectors.toList());
    }

    //  Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerCategoria(@PathVariable Long id) {
        return categoriaService.obtenerCategoria(id)
                .map(categoria -> ResponseEntity.ok(categoriaService.convertirADTO(categoria)))
                .orElse(ResponseEntity.notFound().build());
    }

    //  Crear una categoría
    @PostMapping
    public ResponseEntity<CategoriaDTO> crearCategoria(@RequestBody Categoria categoria) {
        Categoria nuevaCategoria = categoriaService.crearCategoria(categoria);
        return ResponseEntity.ok(categoriaService.convertirADTO(nuevaCategoria));
    }

    //  Actualizar una categoría
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoria);
        return ResponseEntity.ok(categoriaService.convertirADTO(categoriaActualizada));
    }

    //  Eliminar una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
