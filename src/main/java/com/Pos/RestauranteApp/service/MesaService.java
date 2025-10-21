package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.dto.MesaDTO;
import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.repository.MesaRepository;
import org.springframework.stereotype.Service;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    // 🔹 Conversión Entidad → DTO
    public MesaDTO convertirADTO(Mesa mesa) {
        return new MesaDTO(
                mesa.getIdMesa(),
                mesa.getNumeroMesa(),
                mesa.getCapacidad(),
                mesa.getEstado().name()
        );
    }

    // 🔹 Conversión DTO → Entidad
    public Mesa convertirAEntidad(MesaDTO dto) {
        Mesa mesa = new Mesa();
        mesa.setIdMesa(dto.getIdMesa());
        mesa.setNumeroMesa(dto.getNumeroMesa());
        mesa.setCapacidad(dto.getCapacidad());
        mesa.setEstado(Mesa.EstadoMesa.valueOf(dto.getEstado()));
        return mesa;
    }

    // 🔹 Listar todas las mesas
    public List<MesaDTO> listar() {
        return mesaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    // 🔹 Buscar mesa por ID
    public Optional<MesaDTO> obtenerPorId(Long id) {
        return mesaRepository.findById(id).map(this::convertirADTO);
    }

    // 🔹 Guardar o actualizar mesa
    public MesaDTO guardar(MesaDTO dto) {
        Mesa mesa = convertirAEntidad(dto);
        return convertirADTO(mesaRepository.save(mesa));
    }

    // 🔹 Eliminar mesa
    public void eliminar(Long id) {
        mesaRepository.deleteById(id);
    }

    // 🔹 Cambiar estado (DISPONIBLE / OCUPADA)
    public MesaDTO cambiarEstado(Long id, String nuevoEstado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));

        mesa.setEstado(Mesa.EstadoMesa.valueOf(nuevoEstado.toUpperCase()));
        return convertirADTO(mesaRepository.save(mesa));
    }
}
