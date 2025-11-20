package com.Pos.RestauranteApp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Pos.RestauranteApp.model.RegistroSesion;

@Repository
public interface RegistroSesionRepository extends JpaRepository<RegistroSesion, Long> {

    // Busca por usuario (contiene texto) y rango de fechas
    // Si los parámetros son nulos, los ignora
    @Query("SELECT r FROM RegistroSesion r WHERE " +
           "(:usuario IS NULL OR r.usuario LIKE %:usuario%) AND " +
           "(:fechaInicio IS NULL OR r.fechaLogin >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR r.fechaLogin <= :fechaFin) " +
           "ORDER BY r.fechaLogin DESC")
    List<RegistroSesion> buscarSesiones(
            @Param("usuario") String usuario,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}