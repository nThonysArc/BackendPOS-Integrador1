package com.Pos.RestauranteApp.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; // Importar repositorio de clientes
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.model.WEB.ClienteWeb;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.WEB.ClienteWebRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;
    private final ClienteWebRepository clienteWebRepository; // Inyectar repositorio

    public UserDetailsServiceImpl(EmpleadoRepository empleadoRepository, ClienteWebRepository clienteWebRepository) {
        this.empleadoRepository = empleadoRepository;
        this.clienteWebRepository = clienteWebRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Intentar buscar como Empleado (por usuario)
        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuario(username);
        if (empleadoOpt.isPresent()) {
            return empleadoOpt.get(); // Empleado implementa UserDetails
        }

        // 2. Si no es empleado, intentar buscar como Cliente Web (por email)
        // El token de los clientes se generó usando el email como subject
        Optional<ClienteWeb> clienteOpt = clienteWebRepository.findByEmail(username);
        if (clienteOpt.isPresent()) {
            ClienteWeb cliente = clienteOpt.get();
            // Como ClienteWeb no implementa UserDetails, creamos un objeto User de Spring Security manual
            return new User(
                    cliente.getEmail(),
                    cliente.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENTE_WEB"))
            );
        }

        // 3. Si no se encuentra en ninguno
        throw new UsernameNotFoundException("Usuario o Cliente no encontrado con: " + username);
    }
}