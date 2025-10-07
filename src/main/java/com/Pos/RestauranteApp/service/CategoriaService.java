package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.dto.CategoriaDTO;
import com.Pos.RestauranteApp.model.Categoria;
import com.Pos.RestauranteApp.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtenerCategoria(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Long id, Categoria categoria) {
        return categoriaRepository.findById(id).map(c -> {
            c.setNombre(categoria.getNombre());
            return categoriaRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Categoría no encontrada con id " + id));
    }

    public void eliminarCategoria(Long id) {
        categoriaRepository.deleteById(id);
    }

    public CategoriaDTO convertirADTO(Categoria categoria) {
        Long idPadre = (categoria.getCategoriaPadre() != null)
                ? categoria.getCategoriaPadre().getIdCategoria()
                : null;

        return new CategoriaDTO(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                idPadre
        );
    }
}

