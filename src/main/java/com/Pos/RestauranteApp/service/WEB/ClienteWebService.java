package com.Pos.RestauranteApp.service.WEB;

import java.util.Collections;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Pos.RestauranteApp.dto.WEB.LoginClienteDTO;
import com.Pos.RestauranteApp.dto.WEB.RegistroClienteDTO;
import com.Pos.RestauranteApp.dto.auth.AuthResponse;
import com.Pos.RestauranteApp.model.WEB.ClienteWeb;
import com.Pos.RestauranteApp.repository.WEB.ClienteWebRepository;
import com.Pos.RestauranteApp.service.JwtService;

import com.Pos.RestauranteApp.dto.WEB.RegistroClienteDTO; 
import java.util.Optional;

@Service
public class ClienteWebService {

    private final ClienteWebRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ClienteWeb obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public ClienteWeb actualizarCliente(Long id, RegistroClienteDTO dto) {
        ClienteWeb cliente = obtenerClientePorId(id);
        
        cliente.setNombre(dto.getNombre());
        cliente.setApellidos(dto.getApellidos());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccionPrincipal(dto.getDireccion());
        cliente.setReferenciaDireccion(dto.getReferenciaDireccion());
        cliente.setEdad(dto.getEdad());
        
        // Opcional: Actualizar password si viene en el DTO y no está vacío
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            cliente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return clienteRepository.save(cliente);
    }

    public ClienteWebService(ClienteWebRepository clienteRepository, 
                             PasswordEncoder passwordEncoder, 
                             JwtService jwtService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // --- REGISTRO ---
    public AuthResponse registrarCliente(RegistroClienteDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }

        ClienteWeb cliente = new ClienteWeb();
        cliente.setNombre(dto.getNombre());
        cliente.setApellidos(dto.getApellidos());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccionPrincipal(dto.getDireccion());
        cliente.setEdad(dto.getEdad());
        cliente.setReferenciaDireccion(dto.getReferenciaDireccion());
        cliente.setPassword(passwordEncoder.encode(dto.getPassword()));

    ClienteWeb guardado = clienteRepository.save(cliente);

    return generarRespuestaAuth(guardado);
    }

    // --- LOGIN ---
    public AuthResponse autenticarCliente(LoginClienteDTO dto) {
        ClienteWeb cliente = clienteRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(dto.getPassword(), cliente.getPassword())) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        return generarRespuestaAuth(cliente);
    }

    // Método auxiliar para crear la respuesta con Token
    private AuthResponse generarRespuestaAuth(ClienteWeb cliente) {
        // Adaptamos el ClienteWeb a UserDetails para que JwtService lo acepte
        UserDetails userDetails = new User(
                cliente.getEmail(), 
                cliente.getPassword(), 
                Collections.emptyList() // Los clientes web no tienen roles complejos por ahora
        );

        String token = jwtService.generateToken(userDetails);

        // Retornamos la respuesta (reutilizamos el AuthResponse existente o creamos uno para Web)
        // Asumimos rol "CLIENTE_WEB" para diferenciarlo en el frontend si es necesario
        return new AuthResponse(token, cliente.getIdClienteWeb(), cliente.getNombre(), "CLIENTE_WEB");
    }
}