package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.dto.CategoriaDTO;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException; // ⬅️ AÑADIDO
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

    // --- MODIFICADO: Ahora acepta un DTO ---
    public Categoria crearCategoria(CategoriaDTO dto) {
        Categoria categoria = convertirAEntidad(dto);
        return categoriaRepository.save(categoria);
    }

    // --- MODIFICADO: Ahora acepta un DTO y actualiza correctamente ---
    public Categoria actualizarCategoria(Long id, CategoriaDTO dto) {
        // 1. Asegurarse que la categoría existe
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id " + id));

        // 2. Actualizar el nombre
        categoriaExistente.setNombre(dto.getNombre());

        // 3. Actualizar el padre (o quitarlo)
        if (dto.getIdCategoriaPadre() != null) {
            Categoria padre = categoriaRepository.findById(dto.getIdCategoriaPadre())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada con id: " + dto.getIdCategoriaPadre()));
            categoriaExistente.setCategoriaPadre(padre);
        } else {
            categoriaExistente.setCategoriaPadre(null);
        }

        return categoriaRepository.save(categoriaExistente);
    }

    public void eliminarCategoria(Long id) {
        // (Opcional: añadir lógica para verificar si tiene productos antes de borrar)
        categoriaRepository.deleteById(id);
    }

    // 🔹 Entidad -> DTO (Conversión de Salida)
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

    // 🔹 DTO -> Entidad (Conversión de Entrada) (AÑADIDO)
    public Categoria convertirAEntidad(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());

        // Buscar y asignar la categoría padre SI se proporcionó un ID padre
        if (dto.getIdCategoriaPadre() != null) {
            Categoria padre = categoriaRepository.findById(dto.getIdCategoriaPadre())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada con id: " + dto.getIdCategoriaPadre()));
            categoria.setCategoriaPadre(padre);
        } else {
            categoria.setCategoriaPadre(null); // Es una categoría principal
        }

        return categoria;
    }
}