package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.ProductoDTO;
import com.Pos.RestauranteApp.model.Producto;
import com.Pos.RestauranteApp.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ProductoDTO> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ProductoDTO obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id).orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoDTO guardar(@Valid @RequestBody ProductoDTO productoDTO) {
        return productoService.guardar(productoDTO);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        productoDTO.setIdProducto(id);
        return productoService.guardar(productoDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

}