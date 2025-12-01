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
import com.Pos.RestauranteApp.service.WhatsAppService;

@Service
public class PedidoWebService {

    private final PedidoWebRepository pedidoWebRepository;
    private final ClienteWebRepository clienteWebRepository;
    private final ProductoRepository productoRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WhatsAppService whatsAppService; // Inyección del servicio de WhatsApp

    public PedidoWebService(PedidoWebRepository pedidoWebRepository,
                            ClienteWebRepository clienteWebRepository,
                            ProductoRepository productoRepository,
                            SimpMessagingTemplate messagingTemplate,
                            WhatsAppService whatsAppService) { // Constructor actualizado
        this.pedidoWebRepository = pedidoWebRepository;
        this.clienteWebRepository = clienteWebRepository;
        this.productoRepository = productoRepository;
        this.messagingTemplate = messagingTemplate;
        this.whatsAppService = whatsAppService;
    }

    @Transactional
    public PedidoWebDTO crearPedido(PedidoWebDTO dto) {
        // 1. Validar Cliente
        ClienteWeb cliente = clienteWebRepository.findById(dto.getIdClienteWeb())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // 2. Crear Cabecera del Pedido
        PedidoWeb pedido = new PedidoWeb();
        pedido.setClienteWeb(cliente);
        pedido.setFechaHoraPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedidoWeb.PENDIENTE);
        
        pedido.setDireccionEntrega(dto.getDireccionEntrega());
        pedido.setTelefonoContacto(dto.getTelefonoContacto());
        pedido.setReferencia(dto.getReferencia());
        pedido.setMetodoPago(dto.getMetodoPago());

        // 3. Procesar Detalles
        List<DetallePedidoWeb> detallesEntidad = new ArrayList<>();
        double totalCalculado = 0.0;

        for (DetallePedidoWebDTO detDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detDto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + detDto.getIdProducto()));

            DetallePedidoWeb detalle = new DetallePedidoWeb();
            detalle.setPedidoWeb(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(detDto.getCantidad());
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

        // 5. Convertir a DTO
        PedidoWebDTO respuestaDTO = convertirADTO(pedidoGuardado);

        // 6. Notificación WebSocket
        WebSocketMessageDTO mensajeWS = new WebSocketMessageDTO("NUEVO_PEDIDO_WEB", respuestaDTO);
        messagingTemplate.convertAndSend("/topic/pedidos", mensajeWS); 
        
        // 7. --- NOTIFICACIÓN WHATSAPP (Nueva implementación) ---
        // Construimos el mensaje para el motorizado
        String mensajeMotorizado = String.format(
            "🛵 *NUEVO PEDIDO EN PREPARACIÓN* (ID: %d)\n\n" +
            "👤 *Cliente:* %s %s\n" +
            "📞 *Teléfono:* %s\n" +
            "📍 *Dirección:* %s\n" +
            "📝 *Referencia:* %s\n" +
            "💰 *Método Pago:* %s\n\n" +
            "El pedido se está preparando en cocina. Mantente atento para el despacho.",
            pedidoGuardado.getIdPedidoWeb(),
            cliente.getNombre(), (cliente.getApellidos() != null ? cliente.getApellidos() : ""),
            pedido.getTelefonoContacto(), // Usamos el de contacto del pedido, que puede ser distinto al del perfil
            pedido.getDireccionEntrega(),
            pedido.getReferencia(),
            pedido.getMetodoPago()
        );
        
        // Ejecutamos en un hilo separado para no bloquear la respuesta al usuario web si WhatsApp tarda
        new Thread(() -> whatsAppService.enviarMensajeAlMotorizado(mensajeMotorizado)).start();

        return respuestaDTO;
    }
    
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

        EstadoPedidoWeb estadoAnterior = pedido.getEstado();

        // 2. Validar y convertir el Enum
        EstadoPedidoWeb nuevoEstado;
        try {
            nuevoEstado = EstadoPedidoWeb.valueOf(nuevoEstadoStr.toUpperCase());
            pedido.setEstado(nuevoEstado);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstadoStr);
        }

        // 3. Guardar cambios
        PedidoWeb pedidoGuardado = pedidoWebRepository.save(pedido);
        PedidoWebDTO dto = convertirADTO(pedidoGuardado);

        // 4. Notificar vía WebSocket
        WebSocketMessageDTO mensajeWS = new WebSocketMessageDTO("PEDIDO_WEB_ACTUALIZADO", dto);
        messagingTemplate.convertAndSend("/topic/pedidos", mensajeWS);

        // 5. --- NOTIFICACIÓN WHATSAPP AL DESPACHAR ---
        // Si el estado cambia a EN_CAMINO (que asumo es lo que pasa al dar "Despachar"), avisamos
        if (nuevoEstado == EstadoPedidoWeb.EN_CAMINO && estadoAnterior != EstadoPedidoWeb.EN_CAMINO) {
            String mensajeListo = String.format(
                "🚀 *PEDIDO LISTO PARA DESPACHO* (ID: %d)\n\n" +
                "El pedido está empaquetado y listo para salir.\n" +
                "📍 *Destino:* %s",
                pedido.getIdPedidoWeb(),
                pedido.getDireccionEntrega()
            );
            new Thread(() -> whatsAppService.enviarMensajeAlMotorizado(mensajeListo)).start();
        }

        return dto;
    }

    // ... (El resto del método convertirADTO sigue igual) ...
    private PedidoWebDTO convertirADTO(PedidoWeb entidad) {
        // (Tu código existente aquí)
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