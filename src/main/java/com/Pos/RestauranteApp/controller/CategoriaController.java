package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.CategoriaDTO;
import com.Pos.RestauranteApp.model.Categoria;
import com.Pos.RestauranteApp.service.CategoriaService;
import jakarta.validation.Valid; // ⬅️ AÑADIDO
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    //  Listar todas las categorías (Cualquiera autenticado)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CategoriaDTO> listarCategorias() {
        return categoriaService.listarCategorias()
                .stream()
                .map(categoriaService::convertirADTO)
                .collect(Collectors.toList());
    }

    //  Obtener una categoría por ID (Cualquiera autenticado)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoriaDTO> obtenerCategoria(@PathVariable Long id) {
        return categoriaService.obtenerCategoria(id)
                .map(categoria -> ResponseEntity.ok(categoriaService.convertirADTO(categoria)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- MODIFICADO: Ahora acepta CategoriaDTO ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria nuevaCategoria = categoriaService.crearCategoria(categoriaDTO);
        return ResponseEntity.ok(categoriaService.convertirADTO(nuevaCategoria));
    }

    // --- MODIFICADO: Ahora acepta CategoriaDTO ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoriaDTO);
        return ResponseEntity.ok(categoriaService.convertirADTO(categoriaActualizada));
    }

    //  Eliminar una categoría (Solo Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}