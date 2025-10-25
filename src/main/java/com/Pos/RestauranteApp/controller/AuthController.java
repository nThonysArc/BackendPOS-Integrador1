package com.Pos.RestauranteApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // <-- AÑADIR IMPORT
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
        // 1. Autenticar al usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // 2. Si es exitoso, cargar los detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        
        // 3. Generar el token JWT
        final String jwt = jwtService.generateToken(userDetails);

        // 4. --- MODIFICACIÓN: OBTENER DATOS DEL USUARIO ---
        //    (Sabemos que nuestro UserDetails es en realidad un objeto Empleado)
        Empleado empleadoLogueado = (Empleado) userDetails;

        Long id = empleadoLogueado.getIdEmpleado();
        String nombre = empleadoLogueado.getNombre();
        // Obtener el rol (ej. "ROLE_ADMIN", "ROLE_MESERO")
        // .getAuthorities() devuelve una colección, tomamos el primero.
        String rol = empleadoLogueado.getAuthorities().stream().findFirst()
                         .map(Object::toString)
                         .orElse("ROLE_USER"); // Fallback por si acaso

        // 5. Devolver el token Y los datos del usuario
        return ResponseEntity.ok(new AuthResponse(jwt, id, nombre, rol));
    }
}