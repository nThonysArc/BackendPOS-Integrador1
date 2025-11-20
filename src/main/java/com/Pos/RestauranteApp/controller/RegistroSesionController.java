package com.Pos.RestauranteApp.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.model.RegistroSesion;
import com.Pos.RestauranteApp.repository.RegistroSesionRepository;

@RestController
@RequestMapping("/api/admin/sesiones")
@PreAuthorize("hasRole('ADMIN')") 
public class RegistroSesionController {

    private final RegistroSesionRepository repositorio;

    public RegistroSesionController(RegistroSesionRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public ResponseEntity<List<RegistroSesion>> buscar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        // Convertir LocalDate a LocalDateTime para la búsqueda
        LocalDateTime fechaInicio = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime fechaFin = (hasta != null) ? hasta.atTime(23, 59, 59) : null;

        List<RegistroSesion> resultados = repositorio.buscarSesiones(usuario, fechaInicio, fechaFin);
        return ResponseEntity.ok(resultados);
    }
}