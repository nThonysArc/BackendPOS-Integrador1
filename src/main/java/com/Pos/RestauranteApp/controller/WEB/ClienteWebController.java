package com.Pos.RestauranteApp.controller.WEB;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.dto.WEB.RegistroClienteDTO;
import com.Pos.RestauranteApp.model.WEB.ClienteWeb;
import com.Pos.RestauranteApp.service.WEB.ClienteWebService;

@RestController
@RequestMapping("/api/web/cliente")
@CrossOrigin(origins = "*")
public class ClienteWebController {

    private final ClienteWebService clienteWebService;

    public ClienteWebController(ClienteWebService clienteWebService) {
        this.clienteWebService = clienteWebService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteWeb> obtenerPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(clienteWebService.obtenerClientePorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteWeb> actualizarPerfil(@PathVariable Long id, @RequestBody RegistroClienteDTO dto) {
        return ResponseEntity.ok(clienteWebService.actualizarCliente(id, dto));
    }
}