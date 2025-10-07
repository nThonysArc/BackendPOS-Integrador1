package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.PedidoMesaDTO;
import com.Pos.RestauranteApp.service.PedidoMesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidosMesa")
@CrossOrigin(origins = "*") // permite consumir desde el frontend
public class PedidoMesaController {

    private final PedidoMesaService pedidoMesaService;

    public PedidoMesaController(PedidoMesaService pedidoMesaService) {
        this.pedidoMesaService = pedidoMesaService;
    }

    //  Listar todos los pedidos
    @GetMapping
    public ResponseEntity<List<PedidoMesaDTO>> listarPedidos() {
        List<PedidoMesaDTO> pedidos = pedidoMesaService.listar();
        return ResponseEntity.ok(pedidos);
    }

    //  Obtener pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoMesaDTO> obtenerPedido(@PathVariable Long id) {
        PedidoMesaDTO pedido = pedidoMesaService.obtenerPorId(id);
        return ResponseEntity.ok(pedido);
    }

    //  Crear nuevo pedido
    @PostMapping
    public ResponseEntity<PedidoMesaDTO> crearPedido(@RequestBody PedidoMesaDTO pedidoDTO) {
        PedidoMesaDTO nuevoPedido = pedidoMesaService.guardar(pedidoDTO);
        return ResponseEntity.ok(nuevoPedido);
    }

    //  Eliminar pedido (también libera la mesa)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoMesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //  Cerrar pedido (sin eliminarlo)
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<PedidoMesaDTO> cerrarPedido(@PathVariable Long id) {
        PedidoMesaDTO pedidoCerrado = pedidoMesaService.cerrarPedido(id);
        return ResponseEntity.ok(pedidoCerrado);
    }
}
