package com.Pos.RestauranteApp.service;
import com.Pos.RestauranteApp.dto.ProductoDTO;
import com.Pos.RestauranteApp.model.Producto;
import com.Pos.RestauranteApp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Método de conversión
    public ProductoDTO convertirADTO(Producto producto) {
        return new ProductoDTO(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria().getNombre()
        );
    }

    // metodo para devolver DTOs
    public List<ProductoDTO> listar() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Optional<ProductoDTO> obtenerPorId(Long id) {
        return productoRepository.findById(id).map(this::convertirADTO);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
