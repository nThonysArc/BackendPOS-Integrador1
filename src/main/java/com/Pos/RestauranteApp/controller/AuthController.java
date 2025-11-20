package com.Pos.RestauranteApp.controller;

import java.time.LocalDateTime; // Importar LocalDateTime

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.dto.auth.AuthRequest;
import com.Pos.RestauranteApp.dto.auth.AuthResponse;
import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.model.RegistroSesion;
import com.Pos.RestauranteApp.repository.RegistroSesionRepository; 
import com.Pos.RestauranteApp.service.JwtService;

import jakarta.servlet.http.HttpServletRequest; 

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") 
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RegistroSesionRepository registroSesionRepository;

    // Actualizar constructor
    public AuthController(AuthenticationManager authenticationManager, 
                          UserDetailsService userDetailsService, 
                          JwtService jwtService,
                          RegistroSesionRepository registroSesionRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.registroSesionRepository = registroSesionRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        // 1. Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // 2. Cargar detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtService.generateToken(userDetails);

        // 3. Obtener datos del empleado
        Empleado empleadoLogueado = (Empleado) userDetails;
        RegistroSesion registro = new RegistroSesion();
        registro.setIdEmpleado(empleadoLogueado.getIdEmpleado());
        registro.setNombreEmpleado(empleadoLogueado.getNombre());
        registro.setUsuario(empleadoLogueado.getUsuario());
        registro.setRol(empleadoLogueado.getRol().getNombre());
        registro.setFechaLogin(LocalDateTime.now());
        registro.setIpAddress(request.getRemoteAddr()); // Captura la IP
        
        registroSesionRepository.save(registro);

        Long id = empleadoLogueado.getIdEmpleado();
        String nombre = empleadoLogueado.getNombre();
        String rol = empleadoLogueado.getAuthorities().stream().findFirst()
                         .map(Object::toString)
                         .orElse("ROLE_USER"); 
                         
        return ResponseEntity.ok(new AuthResponse(jwt, id, nombre, rol));
    }
}