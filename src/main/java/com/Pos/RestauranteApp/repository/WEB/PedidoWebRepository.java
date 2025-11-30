package com.Pos.RestauranteApp.repository.WEB;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Pos.RestauranteApp.model.WEB.PedidoWeb;
import com.Pos.RestauranteApp.model.WEB.PedidoWeb.EstadoPedidoWeb;

@Repository
public interface PedidoWebRepository extends JpaRepository<PedidoWeb, Long> {

    // --- PARA EL SISTEMA POS (COCINA/CAJA) ---
    // Permite buscar todos los pedidos web que estén en un estado específico
    // (Ej: Buscar todos los 'PENDIENTE' para mostrarlos en la pantalla de cocina)
    List<PedidoWeb> findByEstado(EstadoPedidoWeb estado);
    
    // Variante para buscar por múltiples estados (Ej: PENDIENTE y EN_COCINA)
    List<PedidoWeb> findByEstadoIn(List<EstadoPedidoWeb> estados);

    // --- PARA LA PÁGINA WEB (CLIENTE) ---
    // Permite al cliente ver su historial de pedidos, ordenados del más reciente al más antiguo.
    // Navegamos por la propiedad 'clienteWeb' -> 'email'
    List<PedidoWeb> findByClienteWeb_EmailOrderByFechaHoraPedidoDesc(String email);
    
    // Alternativa por ID de cliente si prefieres usar el ID del token JWT
    List<PedidoWeb> findByClienteWeb_IdClienteWebOrderByFechaHoraPedidoDesc(Long idClienteWeb);
}