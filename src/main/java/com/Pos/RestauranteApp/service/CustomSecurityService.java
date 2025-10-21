package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("customSecurityService") // ⬅️ Este es el nombre del bean
public class CustomSecurityService {

    private final EmpleadoRepository empleadoRepository;

    public CustomSecurityService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Comprueba si el ID proporcionado pertenece al usuario
     * que está actualmente autenticado.
     *
     * @param id El ID del empleado a comprobar (viene del @PathVariable)
     * @return true si el ID pertenece al usuario autenticado, false en caso contrario
     */
    public boolean esElMismoUsuario(Long id) {
        // 1. Obtener la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false; // No hay usuario autenticado
        }

        // 2. Obtener el 'username' (en nuestro caso, el campo 'usuario')
        String username = authentication.getName();

        // 3. Buscar al empleado en la BD por su 'usuario'
        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuario(username);

        if (empleadoOpt.isEmpty()) {
            return false; // El usuario del token no existe en la BD
        }

        // 4. Comparar el ID del empleado autenticado con el ID de la URL
        return empleadoOpt.get().getIdEmpleado().equals(id);
    }
}