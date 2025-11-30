package com.Pos.RestauranteApp.repository.WEB;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Pos.RestauranteApp.model.WEB.ClienteWeb;

@Repository
public interface ClienteWebRepository extends JpaRepository<ClienteWeb, Long> {

    // Buscar cliente por email (Login)
    Optional<ClienteWeb> findByEmail(String email);

    // Verificar si existe un email (Validación en Registro)
    boolean existsByEmail(String email);
}