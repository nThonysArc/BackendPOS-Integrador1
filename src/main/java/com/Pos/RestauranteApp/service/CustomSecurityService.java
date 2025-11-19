package com.Pos.RestauranteApp.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;

@Service("customSecurityService") // ⬅️ Este es el nombre del bean
public class CustomSecurityService {

    private final EmpleadoRepository empleadoRepository;

    public CustomSecurityService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }
    public boolean esElMismoUsuario(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false; 
        }

        String username = authentication.getName();

        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuario(username);

        if (empleadoOpt.isEmpty()) {
            return false; 
        }

        return empleadoOpt.get().getIdEmpleado().equals(id);
    }
}