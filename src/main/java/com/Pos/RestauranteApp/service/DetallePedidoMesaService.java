package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.model.DetallePedidoMesa;
import com.Pos.RestauranteApp.repository.DetallePedidoMesaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DetallePedidoMesaService {
    private final DetallePedidoMesaRepository detalleRepo;

    public DetallePedidoMesaService(DetallePedidoMesaRepository detalleRepo) {
        this.detalleRepo = detalleRepo;
    }

    public List<DetallePedidoMesa> listarDetalles() { return detalleRepo.findAll(); }
    public DetallePedidoMesa guardarDetalle(DetallePedidoMesa detalle) { return detalleRepo.save(detalle); }
    public void eliminarDetalle(Long id) { detalleRepo.deleteById(id); }
}

