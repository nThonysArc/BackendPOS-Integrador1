package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.ProductoDTO;
import com.Pos.RestauranteApp.model.Producto;
import com.Pos.RestauranteApp.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDTO> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ProductoDTO obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id).orElse(null);
    }

    @PostMapping
    public ProductoDTO guardar(@RequestBody ProductoDTO productoDTO) {
        return productoService.guardar(productoDTO);
    }

    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        productoDTO.setIdProducto(id); // le asignamos el id
        return productoService.guardar(productoDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}