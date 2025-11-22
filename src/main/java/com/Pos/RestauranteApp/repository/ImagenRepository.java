package com.Pos.RestauranteApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Pos.RestauranteApp.model.Imagen;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
}
