package com.Pos.RestauranteApp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Pos.RestauranteApp.dto.MesaDTO;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;
import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.repository.MesaRepository;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<MesaDTO> getAllMesas() {
        return mesaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public MesaDTO getMesaById(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
        return convertirADTO(mesa);
    }

    public MesaDTO crearMesa(MesaDTO mesaDTO) {
        Mesa mesa = convertirAEntidad(mesaDTO);
        Mesa nuevaMesa = mesaRepository.save(mesa);
        return convertirADTO(nuevaMesa);
    }

    public MesaDTO actualizarMesa(Long id, MesaDTO mesaDTO) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));

        // Actualizar datos básicos
        mesa.setNumeroMesa(mesaDTO.getNumeroMesa());
        mesa.setCapacidad(mesaDTO.getCapacidad());
        
        // Convertir el String del DTO al Enum de la Entidad
        if (mesaDTO.getEstado() != null) {
            try {
                mesa.setEstado(Mesa.EstadoMesa.valueOf(mesaDTO.getEstado()));
            } catch (IllegalArgumentException e) {
                // Manejar caso donde envíen un estado inválido, o dejar el anterior
            }
        }

        // --- ACTUALIZACIÓN DE CAMPOS VISUALES (NUEVO) ---
        // Usamos valores por defecto si vienen nulos para evitar NullPointerException
        mesa.setPosX(mesaDTO.getPosX() != null ? mesaDTO.getPosX() : 0.0);
        mesa.setPosY(mesaDTO.getPosY() != null ? mesaDTO.getPosY() : 0.0);
        mesa.setWidth(mesaDTO.getWidth() != null ? mesaDTO.getWidth() : 80.0);
        mesa.setHeight(mesaDTO.getHeight() != null ? mesaDTO.getHeight() : 80.0);
        mesa.setRotation(mesaDTO.getRotation() != null ? mesaDTO.getRotation() : 0.0);
        mesa.setForma(mesaDTO.getForma() != null ? mesaDTO.getForma() : "RECTANGLE");
        mesa.setTipo(mesaDTO.getTipo() != null ? mesaDTO.getTipo() : "MESA");
        // ------------------------------------------------

        Mesa mesaActualizada = mesaRepository.save(mesa);
        return convertirADTO(mesaActualizada);
    }

    public void eliminarMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
        mesaRepository.delete(mesa);
    }

    // --- MÉTODOS DE CONVERSIÓN ACTUALIZADOS ---

    private MesaDTO convertirADTO(Mesa mesa) {
        MesaDTO dto = new MesaDTO();
        dto.setIdMesa(mesa.getIdMesa());
        dto.setNumeroMesa(mesa.getNumeroMesa());
        dto.setCapacidad(mesa.getCapacidad());
        dto.setEstado(mesa.getEstado().toString());

        // Mapeo de nuevos campos visuales (Entidad -> DTO)
        dto.setPosX(mesa.getPosX());
        dto.setPosY(mesa.getPosY());
        dto.setWidth(mesa.getWidth());
        dto.setHeight(mesa.getHeight());
        dto.setRotation(mesa.getRotation());
        dto.setForma(mesa.getForma());
        dto.setTipo(mesa.getTipo());

        return dto;
    }

    private Mesa convertirAEntidad(MesaDTO dto) {
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(dto.getNumeroMesa());
        mesa.setCapacidad(dto.getCapacidad());
        
        if (dto.getEstado() != null) {
            try {
                mesa.setEstado(Mesa.EstadoMesa.valueOf(dto.getEstado()));
            } catch (Exception e) {
                mesa.setEstado(Mesa.EstadoMesa.DISPONIBLE);
            }
        }

        // Mapeo de nuevos campos visuales (DTO -> Entidad)
        mesa.setPosX(dto.getPosX() != null ? dto.getPosX() : 0.0);
        mesa.setPosY(dto.getPosY() != null ? dto.getPosY() : 0.0);
        mesa.setWidth(dto.getWidth() != null ? dto.getWidth() : 80.0);
        mesa.setHeight(dto.getHeight() != null ? dto.getHeight() : 80.0);
        mesa.setRotation(dto.getRotation() != null ? dto.getRotation() : 0.0);
        mesa.setForma(dto.getForma() != null ? dto.getForma() : "RECTANGLE");
        mesa.setTipo(dto.getTipo() != null ? dto.getTipo() : "MESA");

        return mesa;
    }
}