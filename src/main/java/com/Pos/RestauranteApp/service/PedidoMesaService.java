package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.dto.DetallePedidoMesaDTO;
import com.Pos.RestauranteApp.dto.PedidoMesaDTO;
import com.Pos.RestauranteApp.model.*;
import com.Pos.RestauranteApp.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada")));
        pedido.setMesero(empleadoRepository.findById(dto.getIdMesero())
                .orElseThrow(() -> new RuntimeException("Mesero no encontrado")));
        pedido.setEstado(PedidoMesa.EstadoPedido.valueOf(dto.getEstado()));

        // Crear los detalles
        List<DetallePedidoMesa> detalles = dto.getDetalles().stream().map(d -> {
            DetallePedidoMesa detalle = new DetallePedidoMesa();
            detalle.setPedidoMesa(pedido);
            detalle.setProducto(productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado")));
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
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public PedidoMesaDTO guardar(PedidoMesaDTO dto) {
        PedidoMesa pedido = convertirAEntidad(dto);

        // Guarda primero el pedido sin los detalles (por seguridad)
        pedido.setDetalles(null);
        PedidoMesa pedidoGuardado = pedidoMesaRepository.save(pedido);

        // Luego asigna los detalles al pedido guardado
        pedidoGuardado.setDetalles(
                dto.getDetalles().stream().map(d -> {
                    DetallePedidoMesa detalle = new DetallePedidoMesa();
                    detalle.setPedidoMesa(pedidoGuardado);
                    detalle.setProducto(productoRepository.findById(d.getIdProducto())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado")));
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

}
