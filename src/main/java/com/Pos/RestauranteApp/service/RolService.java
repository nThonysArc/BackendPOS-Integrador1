package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.dto.RolDTO;
import com.Pos.RestauranteApp.model.Rol;
import com.Pos.RestauranteApp.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    private RolDTO convertirADTO(Rol rol) {
        return new RolDTO(rol.getIdRol(), rol.getNombre());
    }

    private Rol convertirAEntidad(RolDTO dto) {
        Rol rol = new Rol();
        rol.setIdRol(dto.getIdRol());
        rol.setNombre(dto.getNombre());
        return rol;
    }

    public List<RolDTO> listarTodos() {
        return rolRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Optional<RolDTO> buscarPorId(Long id) {
        return rolRepository.findById(id).map(this::convertirADTO);
    }

    public RolDTO guardar(RolDTO dto) {
        Rol rol = convertirAEntidad(dto);
        return convertirADTO(rolRepository.save(rol));
    }

    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }
}
