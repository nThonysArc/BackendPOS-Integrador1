package com.Pos.RestauranteApp.service;

import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.repository.MesaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MesaService {
    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public List<Mesa> listarMesas() { return mesaRepository.findAll(); }
    public Mesa guardarMesa(Mesa mesa) { return mesaRepository.save(mesa); }
    public Mesa buscarPorId(Long id) { return mesaRepository.findById(id).orElse(null); }
    public void eliminarMesa(Long id) { mesaRepository.deleteById(id); }
}

