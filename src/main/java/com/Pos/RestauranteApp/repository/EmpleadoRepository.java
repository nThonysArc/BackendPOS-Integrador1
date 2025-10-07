package com.Pos.RestauranteApp.repository;

import com.Pos.RestauranteApp.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> { }
