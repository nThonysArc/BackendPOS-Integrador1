package com.Pos.RestauranteApp.controller;

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
import com.Pos.RestauranteApp.service.JwtService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") 
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        // Autenticar al usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // Si funciona cargamos los detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        
        // Generar el token JWT
        final String jwt = jwtService.generateToken(userDetails);

        // OBTENER DATOS DEL USUARIO 
        // 
        Empleado empleadoLogueado = (Empleado) userDetails;

        Long id = empleadoLogueado.getIdEmpleado();
        String nombre = empleadoLogueado.getNombre();
        // Obtener el rol "ROLE_ADMIN", "ROLE_MESERO"

        String rol = empleadoLogueado.getAuthorities().stream().findFirst()
                         .map(Object::toString)
                         .orElse("ROLE_USER"); 

        // Devolver el token Y los datos del usuario
        return ResponseEntity.ok(new AuthResponse(jwt, id, nombre, rol));
    }
}