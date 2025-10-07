package com.Pos.RestauranteApp.service;
import com.Pos.RestauranteApp.dto.ProductoDTO;
import com.Pos.RestauranteApp.model.Categoria;
import com.Pos.RestauranteApp.model.Producto;
import com.Pos.RestauranteApp.repository.CategoriaRepository;
import com.Pos.RestauranteApp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Entidad → DTO
    public ProductoDTO convertirADTO(Producto producto) {
        return new ProductoDTO(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria().getNombre(),
                producto.getCategoria().getIdCategoria()
        );
    }
    // DTO → Entidad
    public Producto convertirAEntidad(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setActivo(true);

        // buscar la categoría por id
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        producto.setCategoria(categoria);

        return producto;
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

    public ProductoDTO guardar(ProductoDTO dto) {
        Producto producto = convertirAEntidad(dto);
        return convertirADTO(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
