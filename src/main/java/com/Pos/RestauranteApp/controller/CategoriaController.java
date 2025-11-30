package com.Pos.RestauranteApp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.dto.CategoriaDTO;
import com.Pos.RestauranteApp.model.Categoria;
import com.Pos.RestauranteApp.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    //@PreAuthorize("isAuthenticated()")
    public List<CategoriaDTO> listarCategorias() {
        return categoriaService.listarCategorias()
                .stream()
                .map(categoriaService::convertirADTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoriaDTO> obtenerCategoria(@PathVariable Long id) {
        return categoriaService.obtenerCategoria(id)
                .map(categoria -> ResponseEntity.ok(categoriaService.convertirADTO(categoria)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria nuevaCategoria = categoriaService.crearCategoria(categoriaDTO);
        return ResponseEntity.ok(categoriaService.convertirADTO(nuevaCategoria));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoriaDTO);
        return ResponseEntity.ok(categoriaService.convertirADTO(categoriaActualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}