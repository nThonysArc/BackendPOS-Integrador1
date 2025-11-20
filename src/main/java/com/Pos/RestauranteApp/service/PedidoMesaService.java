package com.Pos.RestauranteApp.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; 

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Pos.RestauranteApp.dto.DetallePedidoMesaDTO;
import com.Pos.RestauranteApp.dto.PedidoMesaDTO;
import com.Pos.RestauranteApp.dto.WebSocketMessageDTO; // <-- IMPORTACIÓN AÑADIDA
import com.Pos.RestauranteApp.exception.ResourceNotFoundException; 
import com.Pos.RestauranteApp.model.DetallePedidoMesa;
import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.model.PedidoMesa.EstadoPedido;
import com.Pos.RestauranteApp.repository.DetallePedidoMesaRepository;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.MesaRepository; 
import com.Pos.RestauranteApp.repository.PedidoMesaRepository;
import com.Pos.RestauranteApp.repository.ProductoRepository;

@Service
public class PedidoMesaService {

    private final PedidoMesaRepository pedidoMesaRepository;
    private final MesaRepository mesaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoRepository productoRepository;
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

    private PedidoMesaDTO convertirADTO(PedidoMesa pedidoMesa) {
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
                            detalle.getEstadoDetalle()
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

    private PedidoMesa convertirAEntidad(PedidoMesaDTO dto, boolean esActualizacion) {
        PedidoMesa pedido;

        if (esActualizacion && dto.getIdPedidoMesa() != null) {
            pedido = pedidoMesaRepository.findById(dto.getIdPedidoMesa())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + dto.getIdPedidoMesa()));
        } else {
            pedido = new PedidoMesa();
            pedido.setIdPedidoMesa(dto.getIdPedidoMesa());
            pedido.setMesa(mesaRepository.findById(dto.getIdMesa())
                    .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + dto.getIdMesa())));
            String usuarioLogueado = SecurityContextHolder.getContext().getAuthentication().getName();

            Empleado meseroLogueado = empleadoRepository.findByUsuario(usuarioLogueado)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario (Mesero) no encontrado: " + usuarioLogueado));
            pedido.setMesero(meseroLogueado);
            pedido.setEstado(PedidoMesa.EstadoPedido.valueOf(dto.getEstado()));
        }
        List<DetallePedidoMesa> detalles = dto.getDetalles().stream().map(d -> {
            DetallePedidoMesa detalle = new DetallePedidoMesa();
            detalle.setPedidoMesa(pedido);

            detalle.setProducto(productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + d.getIdProducto())));
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            detalle.setEstadoDetalle("PENDIENTE"); 
            return detalle;

        }).collect(Collectors.toList());
        if (esActualizacion) {
            pedido.getDetalles().addAll(detalles);
        } else {
            pedido.setDetalles(detalles);
        }
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
                .orElse(null); 
    }

    @Transactional
    public PedidoMesaDTO guardar(PedidoMesaDTO dto) {
        PedidoMesa pedido = convertirAEntidad(dto, false); 
        PedidoMesa pedidoGuardado = pedidoMesaRepository.save(pedido);
        Mesa mesa = pedidoGuardado.getMesa();
        mesa.setEstado(Mesa.EstadoMesa.OCUPADA);
        mesaRepository.save(mesa);

        PedidoMesaDTO respuestaDTO = convertirADTO(pedidoGuardado);

        // CAMBIO: Enviar objeto completo con tipo de evento
        messagingTemplate.convertAndSend("/topic/pedidos", 
            new WebSocketMessageDTO("PEDIDO_CREADO", respuestaDTO));

        return respuestaDTO;
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
            
            throw new IllegalArgumentException("No se puede modificar un pedido que ya está listo para entregar, cerrado o cancelado.");
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

        pedido.setTotal(pedido.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum());

        pedido.setEstado(EstadoPedido.ABIERTO);

        PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);

        PedidoMesaDTO respuestaDTO = convertirADTO(pedidoActualizado);

        // CAMBIO: Enviar objeto completo con tipo de evento
        messagingTemplate.convertAndSend("/topic/pedidos", 
            new WebSocketMessageDTO("PEDIDO_ACTUALIZADO", respuestaDTO));

        return respuestaDTO;
    }

    @Transactional
    public void eliminar(Long id) {
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

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

        PedidoMesaDTO respuestaDTO = convertirADTO(pedidoActualizado);

        // CAMBIO: Enviar objeto completo con tipo de evento
        messagingTemplate.convertAndSend("/topic/pedidos", 
            new WebSocketMessageDTO("PEDIDO_CERRADO", respuestaDTO));

        return respuestaDTO;
    }

    @Transactional
    public PedidoMesaDTO cambiarEstadoPedido(Long id, String nuevoEstadoStr) {
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        try {
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr.toUpperCase());

            if (pedido.getEstado() == EstadoPedido.CERRADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
                 throw new IllegalArgumentException("No se puede cambiar el estado de un pedido CERRADO o CANCELADO.");
            }
            pedido.setEstado(nuevoEstado);
            if (nuevoEstado == EstadoPedido.LISTO_PARA_ENTREGAR) {
                 for (DetallePedidoMesa detalle : pedido.getDetalles()) {
                    if ("PENDIENTE".equalsIgnoreCase(detalle.getEstadoDetalle())) {
                        detalle.setEstadoDetalle("LISTO");
                    }
                }
            }
            
            PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);
            return convertirADTO(pedidoActualizado);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pedido no válido: " + nuevoEstadoStr);
        }
    }

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
        
        if (itemsPendientesEncontrados) {
            pedido.setEstado(EstadoPedido.LISTO_PARA_ENTREGAR);
        } else {
            boolean hayPendientes = pedido.getDetalles().stream()
                .anyMatch(d -> "PENDIENTE".equalsIgnoreCase(d.getEstadoDetalle()));
                
            if (!hayPendientes) {
                pedido.setEstado(EstadoPedido.LISTO_PARA_ENTREGAR);
            }
        }

        PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);

        PedidoMesaDTO respuestaDTO = convertirADTO(pedidoActualizado);

        // CAMBIO: Enviar objeto completo con tipo de evento
        messagingTemplate.convertAndSend("/topic/pedidos", 
            new WebSocketMessageDTO("PEDIDO_LISTO", respuestaDTO));

        return respuestaDTO;
    }
}