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
        // Validación básica de DNI (aunque @Valid debería cubrir esto)
        if (dto.getDni() == null || dto.getDni().isBlank()) {
            // Considera lanzar BadRequestException si creas esa clase
            throw new IllegalArgumentException("El DNI del empleado es obligatorio");
        }

        Empleado empleado;
        String contrasenaDelDto = dto.getContrasena(); // Guardamos la contraseña potencial del DTO

        if (dto.getIdEmpleado() != null) {
            // --- Lógica de ACTUALIZACIÓN ---
            empleado = empleadoRepository.findById(dto.getIdEmpleado())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado para actualizar con id: " + dto.getIdEmpleado()));

            // Actualizar campos que pueden cambiar
            empleado.setNombre(dto.getNombre());
            empleado.setDni(dto.getDni());
            empleado.setUsuario(dto.getUsuario()); // Asegúrate de manejar conflictos de unicidad si es necesario
            Rol rol = rolRepository.findById(dto.getIdRol())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + dto.getIdRol()));
            empleado.setRol(rol);

            // Actualizar contraseña SOLO si se envió una nueva en el DTO
            if (contrasenaDelDto != null && !contrasenaDelDto.isBlank()) {
                // Validar longitud mínima si es necesario (aunque @Valid debería cubrirlo)
                if (contrasenaDelDto.length() < 6) {
                    throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
                }
                empleado.setContrasena(passwordEncoder.encode(contrasenaDelDto));
                System.out.println("Contraseña actualizada y codificada para usuario: " + empleado.getUsuario()); // Log de depuración
            }
            // Si no se envió contraseña nueva en el DTO, no se toca la existente (ya hasheada).

        } else {
            // --- Lógica de CREACIÓN ---
            if (contrasenaDelDto == null || contrasenaDelDto.isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para nuevos empleados");
            }
            // Validar longitud mínima
            if (contrasenaDelDto.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }

            // Creamos una nueva instancia de Empleado
            empleado = new Empleado();
            empleado.setNombre(dto.getNombre());
            empleado.setDni(dto.getDni());
            empleado.setUsuario(dto.getUsuario()); // Manejar unicidad
            Rol rol = rolRepository.findById(dto.getIdRol())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + dto.getIdRol()));
            empleado.setRol(rol);

            // Codificamos la contraseña proporcionada
            empleado.setContrasena(passwordEncoder.encode(contrasenaDelDto));
            System.out.println("Contraseña codificada para nuevo usuario: " + empleado.getUsuario()); // Log de depuración
        }

        // Guardamos la entidad (nueva o actualizada)
        try {
            Empleado empleadoGuardado = empleadoRepository.save(empleado);
            System.out.println("Empleado guardado/actualizado en BD: ID=" + empleadoGuardado.getIdEmpleado() + ", Usuario=" + empleadoGuardado.getUsuario()); // Log de depuración
            return convertirADTO(empleadoGuardado);
        } catch (Exception e) {
            // Capturar posibles errores de base de datos (ej. DNI o usuario duplicado)
            System.err.println("Error al guardar empleado: " + e.getMessage());
            // Podrías lanzar una excepción específica aquí
            throw new RuntimeException("Error al guardar empleado: " + e.getMessage(), e);
        }
    }

    public void eliminar(Long id) {
        empleadoRepository.deleteById(id);
    }
}
