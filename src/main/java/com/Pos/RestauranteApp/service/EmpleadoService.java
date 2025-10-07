package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.dto.EmpleadoDTO;
import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.model.Rol;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, RolRepository rolRepository) {
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
    }

    // Entidad → DTO
    public EmpleadoDTO convertirADTO(Empleado empleado) {
        return new EmpleadoDTO(
                empleado.getIdEmpleado(),
                empleado.getNombre(),
                empleado.getDni(),
                empleado.getUsuario(),
                empleado.getRol() != null ? empleado.getRol().getNombre() : null,
                empleado.getRol() != null ? empleado.getRol().getIdRol() : null
        );
    }

    // DTO → Entidad
    public Empleado convertirAEntidad(EmpleadoDTO dto) {
        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(dto.getIdEmpleado());
        empleado.setNombre(dto.getNombre());
        empleado.setDni(dto.getDni());
        empleado.setUsuario(dto.getUsuario());

        // buscar rol
        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        empleado.setRol(rol);

        return empleado;
    }

    public List<EmpleadoDTO> listarTodos() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Optional<EmpleadoDTO> buscarPorId(Long id) {
        return empleadoRepository.findById(id).map(this::convertirADTO);
    }

    public EmpleadoDTO guardar(EmpleadoDTO dto) {
        if (dto.getDni() == null || dto.getDni().isBlank()) {
            throw new RuntimeException("El DNI del empleado es obligatorio");
        }
        Empleado empleado = convertirAEntidad(dto);
        return convertirADTO(empleadoRepository.save(empleado));
    }

    public void eliminar(Long id) {
        empleadoRepository.deleteById(id);
    }
}
