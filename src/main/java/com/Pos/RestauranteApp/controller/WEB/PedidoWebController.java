package com.Pos.RestauranteApp.controller.WEB;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.dto.WEB.PedidoWebDTO;
import com.Pos.RestauranteApp.service.WEB.PedidoWebService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/web/pedidos")
@CrossOrigin(origins = "*")
public class PedidoWebController {

    private final PedidoWebService pedidoWebService;

    public PedidoWebController(PedidoWebService pedidoWebService) {
        this.pedidoWebService = pedidoWebService;
    }

    // --- ENDPOINT PARA LA WEB (CLIENTES) ---
    // Recibe el carrito, crea el pedido y notifica a la cocina por WebSocket
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Requiere que el cliente web envíe su Token
    public ResponseEntity<PedidoWebDTO> crearPedido(@Valid @RequestBody PedidoWebDTO dto) {
        return ResponseEntity.ok(pedidoWebService.crearPedido(dto));
    }

    // --- ENDPOINT PARA EL POS DE ESCRITORIO (COCINA/ADMIN) ---
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COCINA', 'CAJERO', 'MESERO')")
    public ResponseEntity<List<PedidoWebDTO>> listarPedidosActivos() {
        return ResponseEntity.ok(pedidoWebService.listarPedidosPendientes());
    }
}