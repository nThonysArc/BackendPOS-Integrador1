package com.Pos.RestauranteApp.controller.WEB;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.dto.WEB.LoginClienteDTO;
import com.Pos.RestauranteApp.dto.WEB.RegistroClienteDTO;
import com.Pos.RestauranteApp.dto.auth.AuthResponse;
import com.Pos.RestauranteApp.service.WEB.ClienteWebService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/web/auth")
@CrossOrigin(origins = "*") 
public class WebAuthController {

    private final ClienteWebService clienteWebService;

    public WebAuthController(ClienteWebService clienteWebService) {
        this.clienteWebService = clienteWebService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroClienteDTO dto) {
        return ResponseEntity.ok(clienteWebService.registrarCliente(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginClienteDTO dto) {
        return ResponseEntity.ok(clienteWebService.autenticarCliente(dto));
    }
}