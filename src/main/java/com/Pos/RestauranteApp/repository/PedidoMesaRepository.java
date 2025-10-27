package com.Pos.RestauranteApp.repository;

import java.util.List;
import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.model.PedidoMesa.EstadoPedido; 

public interface PedidoMesaRepository extends JpaRepository<PedidoMesa, Long> {

       List<PedidoMesa> findByMesaIdMesaAndEstadoIn(Long mesaId, List<EstadoPedido> estados);
       
    
       /*
        Busca el primer pedido que coincida con la mesa
         y esté en uno de los estados activos.
        */
       Optional<PedidoMesa> findFirstByMesaIdMesaAndEstadoInOrderByFechaHoraCreacionDesc(Long mesaId, List<EstadoPedido> estados);
}