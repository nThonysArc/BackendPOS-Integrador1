package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.dto.EmpleadoDTO;
import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.model.Rol;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.RolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoService(EmpleadoRepository empleadoRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
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

        empleado.setContrasena(dto.getContrasena());
        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + dto.getIdRol()));
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
            // Es mejor lanzar una excepción de Bad Request, pero RuntimeException
            // será capturada por nuestro handler como 500 (o podemos crear una BadRequestException)
            // Por ahora, lo dejamos así, pero la validación @NotBlank lo capturará primero.
            throw new IllegalArgumentException("El DNI del empleado es obligatorio");
        }

        Empleado empleado = convertirAEntidad(dto);

        // Lógica de codificación de contraseña
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            // Si se provee una contraseña, se codifica
            empleado.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }if (dto.getIdEmpleado() != null) {
            Empleado empleadoAntiguo = empleadoRepository.findById(dto.getIdEmpleado())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado para actualizar con id: " + dto.getIdEmpleado()));
            empleado.setContrasena(empleadoAntiguo.getContrasena());
        } else if (empleado.getContrasena() == null || empleado.getContrasena().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria para nuevos empleados");
        }

        return convertirADTO(empleadoRepository.save(empleado));
    }

    public void eliminar(Long id) {
        empleadoRepository.deleteById(id);
    }
}
