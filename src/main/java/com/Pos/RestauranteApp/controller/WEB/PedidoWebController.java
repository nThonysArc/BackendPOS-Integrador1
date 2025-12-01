package com.Pos.RestauranteApp.controller.WEB;

import java.util.List;
import java.util.Map; // Import necesario para recibir el JSON {"estado": "..."}

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import necesario para /{id}
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;    // Import necesario para @PutMapping
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.dto.WEB.PedidoWebDTO;
import com.Pos.RestauranteApp.service.WEB.PedidoWebService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/web/pedidos")
@CrossOrigin(origins = "*") // Recuerda ajustar esto en producción
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
    // Lista los pedidos pendientes de atender
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COCINA', 'CAJERO', 'MESERO')")
    public ResponseEntity<List<PedidoWebDTO>> listarPedidosActivos() {
        return ResponseEntity.ok(pedidoWebService.listarPedidosPendientes());
    }

    // --- NUEVO ENDPOINT PARA CAMBIAR ESTADO (DESPACHAR/ENTREGAR) ---
    // Permite a la cocina o cajero avanzar el estado del pedido (ej. de PENDIENTE a EN_CAMINO)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COCINA', 'CAJERO', 'MESERO')")
    public ResponseEntity<PedidoWebDTO> cambiarEstado(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        
        // Esperamos un JSON body como: { "estado": "EN_CAMINO" }
        String nuevoEstado = payload.get("estado");
        
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Llama al servicio (asegúrate de haber agregado el método 'actualizarEstadoPedido' en PedidoWebService también)
        return ResponseEntity.ok(pedidoWebService.actualizarEstadoPedido(id, nuevoEstado));
    }
}