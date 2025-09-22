package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.service.MesaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {
    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping
    public List<Mesa> listar() { return mesaService.listarMesas(); }

    @PostMapping
    public Mesa crear(@RequestBody Mesa mesa) { return mesaService.guardarMesa(mesa); }
}

