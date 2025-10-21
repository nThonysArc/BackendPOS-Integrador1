package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.auth.AuthRequest;
import com.Pos.RestauranteApp.dto.auth.AuthResponse;
import com.Pos.RestauranteApp.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permitir CORS para el login
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
                new UsernamePasswordAuthenticationToken(authRequest.getUsuario(), authRequest.getContrasena())
        );

        // 2. Si es exitoso, cargar los detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsuario());

        // 3. Generar el token JWT
        final String jwt = jwtService.generateToken(userDetails);

        // 4. Devolver el token
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}