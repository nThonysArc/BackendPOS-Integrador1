package com.Pos.RestauranteApp.repository.WEB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Pos.RestauranteApp.model.WEB.DetallePedidoWeb;

@Repository
public interface DetallePedidoWebRepository extends JpaRepository<DetallePedidoWeb, Long> {
    // Métodos CRUD básicos ya incluidos por JpaRepository
}