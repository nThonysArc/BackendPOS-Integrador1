package com.Pos.RestauranteApp.service;

import java.util.Arrays;
import java.util.List; // <-- AÑADIDO
import java.util.Optional;
import java.util.stream.Collectors; // <-- AÑADIDO

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- AÑADIDO

import com.Pos.RestauranteApp.dto.DetallePedidoMesaDTO;
import com.Pos.RestauranteApp.dto.PedidoMesaDTO;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException; // <-- AÑADIR IMPORT
 import com.Pos.RestauranteApp.model.DetallePedidoMesa;
import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.model.PedidoMesa.EstadoPedido;
import com.Pos.RestauranteApp.repository.DetallePedidoMesaRepository;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.MesaRepository; // <-- AÑADIDO
import com.Pos.RestauranteApp.repository.PedidoMesaRepository;
import com.Pos.RestauranteApp.repository.ProductoRepository;

@Service
public class PedidoMesaService {

    private final PedidoMesaRepository pedidoMesaRepository;
    private final MesaRepository mesaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoRepository productoRepository;
    // --- AÑADIDO ---
    private final DetallePedidoMesaRepository detallePedidoMesaRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public PedidoMesaService(PedidoMesaRepository pedidoMesaRepository,
                         MesaRepository mesaRepository,
                         EmpleadoRepository empleadoRepository,
                         ProductoRepository productoRepository,
                         DetallePedidoMesaRepository detallePedidoMesaRepository,
                         SimpMessagingTemplate messagingTemplate) {
    this.pedidoMesaRepository = pedidoMesaRepository;
    this.mesaRepository = mesaRepository;
    this.empleadoRepository = empleadoRepository;
    this.productoRepository = productoRepository;
    this.detallePedidoMesaRepository = detallePedidoMesaRepository;
    this.messagingTemplate = messagingTemplate;
}

    // Conversión Entidad → DTO
    private PedidoMesaDTO convertirADTO(PedidoMesa pedidoMesa) {
        // --- MODIFICADO: Asegurarse que los detalles no sean nulos ---
        List<DetallePedidoMesaDTO> detallesDTO = List.of();
        if (pedidoMesa.getDetalles() != null) {
            detallesDTO = pedidoMesa.getDetalles()
                    .stream()
                    .map(detalle -> new DetallePedidoMesaDTO(
                            detalle.getIdDetallePedidoMesa(),
                            detalle.getProducto().getIdProducto(),
                            detalle.getProducto().getNombre(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getEstadoDetalle() // <-- ¡¡NUEVO!!
                    ))
                    .collect(Collectors.toList());
        }

        return new PedidoMesaDTO(
                pedidoMesa.getIdPedidoMesa(),
                pedidoMesa.getMesa().getIdMesa(),
                pedidoMesa.getMesa().getNumeroMesa(),
                pedidoMesa.getMesero().getIdEmpleado(),
                pedidoMesa.getMesero().getNombre(),
                pedidoMesa.getFechaHoraCreacion(),
                pedidoMesa.getEstado().name(),
                pedidoMesa.getTotal(),
                detallesDTO
        );
    }

    // Conversión DTO → Entidad (Usado para CREAR)
    // --- ¡¡MODIFICADO!! ---
    private PedidoMesa convertirAEntidad(PedidoMesaDTO dto, boolean esActualizacion) {
        PedidoMesa pedido;

        if (esActualizacion && dto.getIdPedidoMesa() != null) {
            // Si es actualización, cargamos el pedido existente
            pedido = pedidoMesaRepository.findById(dto.getIdPedidoMesa())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + dto.getIdPedidoMesa()));
        } else {
            // Si es creación, creamos uno nuevo
            pedido = new PedidoMesa();
            pedido.setIdPedidoMesa(dto.getIdPedidoMesa());
            pedido.setMesa(mesaRepository.findById(dto.getIdMesa())
                    .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + dto.getIdMesa())));
            // 1. Obtener el usuario autenticado
            String usuarioLogueado = SecurityContextHolder.getContext().getAuthentication().getName();

            // 2. Buscar al empleado por su nombre de 'usuario'
            Empleado meseroLogueado = empleadoRepository.findByUsuario(usuarioLogueado)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario (Mesero) no encontrado: " + usuarioLogueado));
            // 3. Asignar el empleado logueado
            pedido.setMesero(meseroLogueado);
            pedido.setEstado(PedidoMesa.EstadoPedido.valueOf(dto.getEstado()));
        }


        // --- ¡¡MODIFICADO!!: Añadimos solo los detalles nuevos ---
        // El DTO solo traerá los detalles nuevos.
        List<DetallePedidoMesa> detalles = dto.getDetalles().stream().map(d -> {
            DetallePedidoMesa detalle = new DetallePedidoMesa();
            detalle.setPedidoMesa(pedido);

            detalle.setProducto(productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + d.getIdProducto())));
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            detalle.setEstadoDetalle("PENDIENTE"); // <-- ¡¡NUEVO!! Marcar como pendiente
            return detalle;

        }).collect(Collectors.toList());

        // Si es actualización, añadimos los nuevos detalles a los existentes
        if (esActualizacion) {
            pedido.getDetalles().addAll(detalles);
        } else {
            pedido.setDetalles(detalles);
        }

        // Recalculamos el total basado en TODOS los detalles
        pedido.setTotal(pedido.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum());

        return pedido;
    }

    // CRUD básico
    public List<PedidoMesaDTO> listar() {
        return pedidoMesaRepository.findAll()
                .stream().map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public PedidoMesaDTO obtenerPorId(Long id) {
        return pedidoMesaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }

    // --- ¡¡NUEVO MÉTODO AÑADIDO!! ---
    /**
     * Busca el pedido activo (ABIERTO, EN_COCINA, LISTO_PARA_ENTREGAR) de una mesa específica.
     */
    public PedidoMesaDTO obtenerPedidoActivoPorMesa(Long mesaId) {
        List<EstadoPedido> estadosActivos = Arrays.asList(
                EstadoPedido.ABIERTO,
                EstadoPedido.EN_COCINA,
                EstadoPedido.LISTO_PARA_ENTREGAR
        );

        Optional<PedidoMesa> pedidoActivoOpt = pedidoMesaRepository
                .findFirstByMesaIdMesaAndEstadoInOrderByFechaHoraCreacionDesc(mesaId, estadosActivos);

        return pedidoActivoOpt
                .map(this::convertirADTO)
                // --- MODIFICADO: No lanzar excepción si no se encuentra ---
                .orElse(null); // Devuelve null si no hay pedido activo
                //.orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún pedido activo para la mesa id: " + mesaId));
    }


    @Transactional
    public PedidoMesaDTO guardar(PedidoMesaDTO dto) {
        // --- ¡¡MODIFICADO!! ---
        PedidoMesa pedido = convertirAEntidad(dto, false); // false = no es actualización

        // --- Lógica de guardado simplificada ---
        // Como 'convertirAEntidad' ya asigna los detalles, solo guardamos.
        // El CascadeType.ALL en PedidoMesa.detalles se encarga de guardar los detalles.
        PedidoMesa pedidoGuardado = pedidoMesaRepository.save(pedido);

        // 🔹 Cambiar el estado de la mesa a OCUPADA automáticamente
        Mesa mesa = pedidoGuardado.getMesa();
        mesa.setEstado(Mesa.EstadoMesa.OCUPADA);
        mesaRepository.save(mesa);

        messagingTemplate.convertAndSend("/topic/pedidos", "NUEVO");

        return convertirADTO(pedidoGuardado);
    }
    @Transactional
    public PedidoMesaDTO actualizar(Long id, PedidoMesaDTO dto) {
        // 1. Buscar el pedido existente
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        // 2. Validar que no esté cerrado
       if (pedido.getEstado() == EstadoPedido.CERRADO || 
            pedido.getEstado() == EstadoPedido.CANCELADO ||
            pedido.getEstado() == EstadoPedido.LISTO_PARA_ENTREGAR) {
            
            throw new IllegalArgumentException("No se puede modificar un pedido que ya está listo para entregar, cerrado o cancelado."); // <-- Mensaje actualizado
        }
        //  Añadir nuevos detalles desde el DTO
        //    (El DTO solo trae los detalles nuevos)
        List<DetallePedidoMesa> nuevosDetalles = dto.getDetalles().stream().map(d -> {
            DetallePedidoMesa detalle = new DetallePedidoMesa();
            detalle.setPedidoMesa(pedido);
            detalle.setProducto(productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + d.getIdProducto())));
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            detalle.setEstadoDetalle("PENDIENTE");
            return detalle;
        }).collect(Collectors.toList());

        pedido.getDetalles().addAll(nuevosDetalles); 

        // 5. Recalcular total (con todos los detalles, viejos y nuevos)
        pedido.setTotal(pedido.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum());

        // 6. ¡Importante! Regresar el estado a ABIERTO
        //    para notificar a la cocina que hay nuevos items PENDIENTES.
        pedido.setEstado(EstadoPedido.ABIERTO);

        // 7. Guardar
        PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);

        messagingTemplate.convertAndSend("/topic/pedidos", "NUEVO");

        return convertirADTO(pedidoActualizado);
    }


    @Transactional
    public void eliminar(Long id) {
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        //  Liberar la mesa al eliminar el pedido
        Mesa mesa = pedido.getMesa();
        mesa.setEstado(Mesa.EstadoMesa.DISPONIBLE);
        mesaRepository.save(mesa);

        pedidoMesaRepository.delete(pedido);
    }
    @Transactional
    public PedidoMesaDTO cerrarPedido(Long id) {
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Cambiar estado del pedido
        pedido.setEstado(PedidoMesa.EstadoPedido.CERRADO);

        // Liberar la mesa
        Mesa mesa = pedido.getMesa();
        mesa.setEstado(Mesa.EstadoMesa.DISPONIBLE);
        mesaRepository.save(mesa);

        // Guardar el pedido actualizado
        PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);

        messagingTemplate.convertAndSend("/topic/pedidos", "CERRADO");

        return convertirADTO(pedidoActualizado);
    }
    // === MÉTODO MODIFICADO ===
    @Transactional
    public PedidoMesaDTO cambiarEstadoPedido(Long id, String nuevoEstadoStr) {
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        try {
            // Convertir el String a un valor del Enum
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr.toUpperCase());

            // Validaciones opcionales (ej. no se puede reabrir un pedido cerrado desde aquí)
            if (pedido.getEstado() == EstadoPedido.CERRADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
                 throw new IllegalArgumentException("No se puede cambiar el estado de un pedido CERRADO o CANCELADO.");
            }
            // Puedes añadir más lógica si es necesario (ej. solo Cocina puede poner LISTO)

            pedido.setEstado(nuevoEstado);
            
            // --- ¡¡MODIFICACIÓN!! ---
            // Si el nuevo estado es LISTO_PARA_ENTREGAR, marcamos todos los PENDIENTES como LISTO
            // Esto es lo que hacía el botón de cocina
            if (nuevoEstado == EstadoPedido.LISTO_PARA_ENTREGAR) {
                 for (DetallePedidoMesa detalle : pedido.getDetalles()) {
                    if ("PENDIENTE".equalsIgnoreCase(detalle.getEstadoDetalle())) {
                        detalle.setEstadoDetalle("LISTO");
                    }
                }
            }
            // --- FIN MODIFICACIÓN ---
            
            PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);
            return convertirADTO(pedidoActualizado);

        } catch (IllegalArgumentException e) {
            // Ocurre si el nuevoEstadoStr no es un valor válido del Enum
            throw new IllegalArgumentException("Estado de pedido no válido: " + nuevoEstadoStr);
        }
    }
    
    // === ¡¡NUEVO MÉTODO PARA COCINA!! ===
    /**
     * Marca todos los detalles PENDIENTES de un pedido como LISTO
     * y actualiza el estado del pedido principal a LISTO_PARA_ENTREGAR.
     */
    @Transactional
    public PedidoMesaDTO marcarPendientesComoListos(Long id) {
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        boolean itemsPendientesEncontrados = false;
        
        for (DetallePedidoMesa detalle : pedido.getDetalles()) {
            if ("PENDIENTE".equalsIgnoreCase(detalle.getEstadoDetalle())) {
                detalle.setEstadoDetalle("LISTO");
                itemsPendientesEncontrados = true;
            }
        }
        
        // Si encontramos y marcamos items, ponemos el pedido listo para caja.
        if (itemsPendientesEncontrados) {
             // El estado LISTO_PARA_ENTREGAR es para que Caja lo vea.
            pedido.setEstado(EstadoPedido.LISTO_PARA_ENTREGAR);
        } else {
            // Si no había pendientes, quizás ya estaba listo. Reafirmamos.
            // Opcional: Si no hay PENDIENTES, y SÍ hay LISTOS, ponerlo LISTO_PARA_ENTREGAR.
            boolean hayPendientes = pedido.getDetalles().stream()
                .anyMatch(d -> "PENDIENTE".equalsIgnoreCase(d.getEstadoDetalle()));
                
            if (!hayPendientes) {
                pedido.setEstado(EstadoPedido.LISTO_PARA_ENTREGAR);
            }
        }

        PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);

        messagingTemplate.convertAndSend("/topic/pedidos", "LISTO");

        return convertirADTO(pedidoActualizado);
    }

}