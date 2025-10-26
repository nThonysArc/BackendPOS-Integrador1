package com.Pos.RestauranteApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.model.PedidoMesa.EstadoPedido; // <-- AÑADIR IMPORT

public interface PedidoMesaRepository extends JpaRepository<PedidoMesa, Long> {

       List<PedidoMesa> findByMesaIdMesaAndEstadoIn(Long mesaId, List<EstadoPedido> estados);
}
