package com.Pos.RestauranteApp.repository;

import java.util.List;
import java.util.Optional; // <-- AÑADIDO

import org.springframework.data.jpa.repository.JpaRepository;

import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.model.PedidoMesa.EstadoPedido; // <-- AÑADIR IMPORT

public interface PedidoMesaRepository extends JpaRepository<PedidoMesa, Long> {

       List<PedidoMesa> findByMesaIdMesaAndEstadoIn(Long mesaId, List<EstadoPedido> estados);
       
       // --- ¡¡NUEVO MÉTODO AÑADIDO!! ---
       /**
        * Busca el primer pedido (el más reciente, si hay varios) que coincida con la mesa
        * y esté en uno de los estados activos (ABIERTO, EN_COCINA, LISTO_PARA_ENTREGAR).
        */
       Optional<PedidoMesa> findFirstByMesaIdMesaAndEstadoInOrderByFechaHoraCreacionDesc(Long mesaId, List<EstadoPedido> estados);
}