package com.Pos.RestauranteApp.config;

import com.Pos.RestauranteApp.dto.EmpleadoDTO;
import com.Pos.RestauranteApp.model.Rol;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.RolRepository;
import com.Pos.RestauranteApp.service.EmpleadoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoService empleadoService;

    public DataInitializer(RolRepository rolRepository, EmpleadoRepository empleadoRepository, EmpleadoService empleadoService) {
        this.rolRepository = rolRepository;
        this.empleadoRepository = empleadoRepository;
        this.empleadoService = empleadoService;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- 1. CREAR ROLES BÁSICOS ---
        Rol rolAdmin;
        if (rolRepository.count() == 0) {
            System.out.println("Creando roles por defecto...");
            rolAdmin = rolRepository.save(new Rol("ADMIN"));
            rolRepository.save(new Rol("MESERO"));
            rolRepository.save(new Rol("CAJERO"));
            rolRepository.save(new Rol("COCINA"));
        } else {
            rolAdmin = rolRepository.findById(1L).orElse(null); // Asumimos que ADMIN es ID 1
        }

        // --- 2. CREAR USUARIO ADMIN POR DEFECTO ---
        if (empleadoRepository.count() == 0 && rolAdmin != null) {
            System.out.println("Creando usuario ADMIN por defecto...");
            EmpleadoDTO adminDto = new EmpleadoDTO();
            adminDto.setNombre("Anthony Miguel Palomino Arce");
            adminDto.setDni("72376198");
            adminDto.setUsuario("ThonysAdmin"); // <-- Usuario para LOGIN
            adminDto.setContrasena("admin123"); // <-- CLAVE
            adminDto.setIdRol(rolAdmin.getIdRol());

            empleadoService.guardar(adminDto);
            System.out.println("Usuario ADMIN creado con éxito (admin/admin123)");
        }
    }
}