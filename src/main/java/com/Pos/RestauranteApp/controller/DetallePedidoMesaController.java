package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.model.DetallePedidoMesa;
import com.Pos.RestauranteApp.service.DetallePedidoMesaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-pedido-mesa")
public class DetallePedidoMesaController {
    private final DetallePedidoMesaService detalleService;

    public DetallePedidoMesaController(DetallePedidoMesaService detalleService) {
        this.detalleService = detalleService;
    }

    @GetMapping
    public List<DetallePedidoMesa> listar() { return detalleService.listarDetalles(); }

    @PostMapping
    public DetallePedidoMesa crear(@RequestBody DetallePedidoMesa detalle) { return detalleService.guardarDetalle(detalle); }
}

