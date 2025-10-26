package com.Pos.RestauranteApp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.Pos.RestauranteApp.dto.DetallePedidoMesaDTO; // <-- AÑADIR IMPORT
 import com.Pos.RestauranteApp.dto.PedidoMesaDTO;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;
import com.Pos.RestauranteApp.model.DetallePedidoMesa;
import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.model.PedidoMesa;
import com.Pos.RestauranteApp.model.PedidoMesa.EstadoPedido;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.MesaRepository;
import com.Pos.RestauranteApp.repository.PedidoMesaRepository; // <-- Añadir import
import com.Pos.RestauranteApp.repository.ProductoRepository;
@Service
public class PedidoMesaService {

    private final PedidoMesaRepository pedidoMesaRepository;
    private final MesaRepository mesaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoRepository productoRepository;

    public PedidoMesaService(PedidoMesaRepository pedidoMesaRepository,
                             MesaRepository mesaRepository,
                             EmpleadoRepository empleadoRepository,
                             ProductoRepository productoRepository) {
        this.pedidoMesaRepository = pedidoMesaRepository;
        this.mesaRepository = mesaRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
    }

    // Conversión Entidad → DTO
    private PedidoMesaDTO convertirADTO(PedidoMesa pedidoMesa) {
        List<DetallePedidoMesaDTO> detallesDTO = pedidoMesa.getDetalles()
                .stream()
                .map(detalle -> new DetallePedidoMesaDTO(
                        detalle.getIdDetallePedidoMesa(),
                        detalle.getProducto().getIdProducto(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario()
                ))
                .collect(Collectors.toList());

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

    // Conversión DTO → Entidad
    private PedidoMesa convertirAEntidad(PedidoMesaDTO dto) {
        PedidoMesa pedido = new PedidoMesa();
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
        List<DetallePedidoMesa> detalles = dto.getDetalles().stream().map(d -> {
            DetallePedidoMesa detalle = new DetallePedidoMesa();
            detalle.setPedidoMesa(pedido);

            detalle.setProducto(productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + d.getIdProducto())));
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            return detalle;

        }).collect(Collectors.toList());

        pedido.setDetalles(detalles);
        pedido.setTotal(detalles.stream()
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

    public PedidoMesaDTO guardar(PedidoMesaDTO dto) {
        PedidoMesa pedido = convertirAEntidad(dto);

        pedido.setDetalles(null);
        PedidoMesa pedidoGuardado = pedidoMesaRepository.save(pedido);

        pedidoGuardado.setDetalles(
                dto.getDetalles().stream().map(d -> {
                    DetallePedidoMesa detalle = new DetallePedidoMesa();
                    detalle.setPedidoMesa(pedidoGuardado);
                    detalle.setProducto(productoRepository.findById(d.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + d.getIdProducto())));
                    detalle.setCantidad(d.getCantidad());
                    detalle.setPrecioUnitario(d.getPrecioUnitario());
                    return detalle;
                }).collect(Collectors.toList())
        );

        // Calcula total del pedido
        pedidoGuardado.setTotal(pedidoGuardado.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum());

        // 🔹 Cambiar el estado de la mesa a OCUPADA automáticamente
        Mesa mesa = pedidoGuardado.getMesa();
        mesa.setEstado(Mesa.EstadoMesa.OCUPADA);
        mesaRepository.save(mesa);

        // Guarda nuevamente el pedido con los detalles y el total
        pedidoMesaRepository.save(pedidoGuardado);

        return convertirADTO(pedidoGuardado);
    }

    public void eliminar(Long id) {
        PedidoMesa pedido = pedidoMesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        //  Liberar la mesa al eliminar el pedido
        Mesa mesa = pedido.getMesa();
        mesa.setEstado(Mesa.EstadoMesa.DISPONIBLE);
        mesaRepository.save(mesa);

        pedidoMesaRepository.delete(pedido);
    }
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

        return convertirADTO(pedidoActualizado);
    }
    // === NUEVO MÉTODO PARA CAMBIAR ESTADO ===
    /**
     * Cambia el estado de un pedido existente.
     * @param id El ID del pedido a modificar.
     * @param nuevoEstadoStr El nuevo estado como String (debe coincidir con un valor del Enum EstadoPedido).
     * @return El PedidoMesaDTO actualizado.
     * @throws ResourceNotFoundException Si el pedido no se encuentra.
     * @throws IllegalArgumentException Si el nuevoEstadoStr no es válido.
     */
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
            PedidoMesa pedidoActualizado = pedidoMesaRepository.save(pedido);
            return convertirADTO(pedidoActualizado);

        } catch (IllegalArgumentException e) {
            // Ocurre si el nuevoEstadoStr no es un valor válido del Enum
            throw new IllegalArgumentException("Estado de pedido no válido: " + nuevoEstadoStr);
        }
    }

}
