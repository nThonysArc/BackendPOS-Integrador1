package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.service.PedidoMesaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos-mesa")
public class PedidoMesaController {
    private final PedidoMesaService pedidoService;

    public PedidoMesaController(PedidoMesaService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<PedidoMesa> listar() { return pedidoService.listarPedidos(); }

    @PostMapping
    public PedidoMesa crear(@RequestBody PedidoMesa pedido) { return pedidoService.guardarPedido(pedido); }
}

