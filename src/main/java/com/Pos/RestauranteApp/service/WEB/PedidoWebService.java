package com.Pos.RestauranteApp.service.WEB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Pos.RestauranteApp.dto.WEB.DetallePedidoWebDTO;
import com.Pos.RestauranteApp.dto.WEB.PedidoWebDTO;
import com.Pos.RestauranteApp.dto.WebSocketMessageDTO;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;
import com.Pos.RestauranteApp.model.Producto;
import com.Pos.RestauranteApp.model.WEB.ClienteWeb;
import com.Pos.RestauranteApp.model.WEB.DetallePedidoWeb;
import com.Pos.RestauranteApp.model.WEB.PedidoWeb;
import com.Pos.RestauranteApp.model.WEB.PedidoWeb.EstadoPedidoWeb;
import com.Pos.RestauranteApp.repository.ProductoRepository;
import com.Pos.RestauranteApp.repository.WEB.ClienteWebRepository;
import com.Pos.RestauranteApp.repository.WEB.PedidoWebRepository;

@Service
public class PedidoWebService {

    private final PedidoWebRepository pedidoWebRepository;
    private final ClienteWebRepository clienteWebRepository;
    private final ProductoRepository productoRepository;
    private final SimpMessagingTemplate messagingTemplate; // ¡Para WebSockets!

    public PedidoWebService(PedidoWebRepository pedidoWebRepository,
                            ClienteWebRepository clienteWebRepository,
                            ProductoRepository productoRepository,
                            SimpMessagingTemplate messagingTemplate) {
        this.pedidoWebRepository = pedidoWebRepository;
        this.clienteWebRepository = clienteWebRepository;
        this.productoRepository = productoRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional // Importante para que todo se guarde o nada se guarde (Rollback)
    public PedidoWebDTO crearPedido(PedidoWebDTO dto) {
        // 1. Validar Cliente
        ClienteWeb cliente = clienteWebRepository.findById(dto.getIdClienteWeb())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // 2. Crear Cabecera del Pedido
        PedidoWeb pedido = new PedidoWeb();
        pedido.setClienteWeb(cliente);
        pedido.setFechaHoraPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedidoWeb.PENDIENTE); // Estado inicial para Cocina
        
        pedido.setDireccionEntrega(dto.getDireccionEntrega());
        pedido.setTelefonoContacto(dto.getTelefonoContacto());
        pedido.setReferencia(dto.getReferencia());
        pedido.setMetodoPago(dto.getMetodoPago());

        // 3. Procesar Detalles y Calcular Totales Reales
        List<DetallePedidoWeb> detallesEntidad = new ArrayList<>();
        double totalCalculado = 0.0;

        for (DetallePedidoWebDTO detDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detDto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + detDto.getIdProducto()));

            DetallePedidoWeb detalle = new DetallePedidoWeb();
            detalle.setPedidoWeb(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(detDto.getCantidad());
            
            // Usamos el precio de la base de datos, NO el del JSON (seguridad)
            detalle.setPrecioUnitario(producto.getPrecio()); 
            
            double subtotal = producto.getPrecio() * detDto.getCantidad();
            detalle.setSubtotal(subtotal);
            detalle.setObservaciones(detDto.getObservaciones());

            detallesEntidad.add(detalle);
            totalCalculado += subtotal;
        }

        pedido.setDetalles(detallesEntidad);
        pedido.setTotal(totalCalculado);

        // 4. Guardar en BD
        PedidoWeb pedidoGuardado = pedidoWebRepository.save(pedido);

        // 5. Convertir a DTO para respuesta y notificación
        PedidoWebDTO respuestaDTO = convertirADTO(pedidoGuardado);

        // 6. ¡NOTIFICACIÓN WEBSOCKET A LA COCINA!
        // Enviamos al tópico '/topic/pedidosWeb' que escuchará el Frontend de Escritorio
        WebSocketMessageDTO mensajeWS = new WebSocketMessageDTO("NUEVO_PEDIDO_WEB", respuestaDTO);
        messagingTemplate.convertAndSend("/topic/pedidos", mensajeWS); 
        // Nota: Reutilizo "/topic/pedidos" para simplificar la conexión en el frontend, 
        // pero con un 'type' diferente ("NUEVO_PEDIDO_WEB") para que el Dashboard sepa qué hacer.

        return respuestaDTO;
    }
    
    // Método auxiliar para listar pedidos activos (para la pantalla de cocina al iniciar)
    public List<PedidoWebDTO> listarPedidosPendientes() {
        List<EstadoPedidoWeb> estadosActivos = List.of(EstadoPedidoWeb.PENDIENTE, EstadoPedidoWeb.EN_COCINA);
        return pedidoWebRepository.findByEstadoIn(estadosActivos).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public PedidoWebDTO actualizarEstadoPedido(Long idPedido, String nuevoEstadoStr) {
        // 1. Buscar el pedido
        PedidoWeb pedido = pedidoWebRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido Web no encontrado con ID: " + idPedido));

        // 2. Validar y convertir el Enum
        try {
            EstadoPedidoWeb nuevoEstado = EstadoPedidoWeb.valueOf(nuevoEstadoStr.toUpperCase());
            pedido.setEstado(nuevoEstado);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstadoStr);
        }

        // 3. Guardar cambios
        PedidoWeb pedidoGuardado = pedidoWebRepository.save(pedido);
        PedidoWebDTO dto = convertirADTO(pedidoGuardado);

        // 4. Notificar vía WebSocket (Crucial para sincronización en tiempo real)
        // Esto permite que si hay dos pantallas de cocina, ambas se actualicen, 
        // o que la web del cliente sepa que su pedido va en camino.
        WebSocketMessageDTO mensajeWS = new WebSocketMessageDTO("PEDIDO_WEB_ACTUALIZADO", dto);
        messagingTemplate.convertAndSend("/topic/pedidos", mensajeWS);

        return dto;
    }

    // Mapeo Entidad -> DTO
    private PedidoWebDTO convertirADTO(PedidoWeb entidad) {
        PedidoWebDTO dto = new PedidoWebDTO();
        dto.setIdPedidoWeb(entidad.getIdPedidoWeb());
        dto.setIdClienteWeb(entidad.getClienteWeb().getIdClienteWeb());
        dto.setNombreCliente(entidad.getClienteWeb().getNombre() + " " + (entidad.getClienteWeb().getApellidos() != null ? entidad.getClienteWeb().getApellidos() : ""));
        dto.setFechaHora(entidad.getFechaHoraPedido());
        dto.setEstado(entidad.getEstado().name());
        dto.setDireccionEntrega(entidad.getDireccionEntrega());
        dto.setTelefonoContacto(entidad.getTelefonoContacto());
        dto.setReferencia(entidad.getReferencia());
        dto.setMetodoPago(entidad.getMetodoPago());
        dto.setTotal(entidad.getTotal());

        List<DetallePedidoWebDTO> detallesDto = entidad.getDetalles().stream().map(d -> {
            DetallePedidoWebDTO det = new DetallePedidoWebDTO();
            det.setIdProducto(d.getProducto().getIdProducto());
            det.setNombreProducto(d.getProducto().getNombre());
            det.setCantidad(d.getCantidad());
            det.setPrecioUnitario(d.getPrecioUnitario());
            det.setObservaciones(d.getObservaciones());
            return det;
        }).collect(Collectors.toList());

        dto.setDetalles(detallesDto);
        return dto;
    }
}