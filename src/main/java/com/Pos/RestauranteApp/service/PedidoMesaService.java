package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.repository.PedidoMesaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PedidoMesaService {
    private final PedidoMesaRepository pedidoMesaRepository;

    public PedidoMesaService(PedidoMesaRepository pedidoMesaRepository) {
        this.pedidoMesaRepository = pedidoMesaRepository;
    }

    public List<PedidoMesa> listarPedidos() { return pedidoMesaRepository.findAll(); }
    public PedidoMesa guardarPedido(PedidoMesa pedido) { return pedidoMesaRepository.save(pedido); }
    public PedidoMesa buscarPorId(Long id) { return pedidoMesaRepository.findById(id).orElse(null); }
    public void eliminarPedido(Long id) { pedidoMesaRepository.deleteById(id); }
}

