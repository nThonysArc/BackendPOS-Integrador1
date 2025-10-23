package com.Pos.RestauranteApp.config;

import com.Pos.RestauranteApp.dto.EmpleadoDTO;
import com.Pos.RestauranteApp.model.Mesa; // Importar Mesa
import com.Pos.RestauranteApp.model.Rol;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.MesaRepository; // Importar MesaRepository
import com.Pos.RestauranteApp.repository.RolRepository;
import com.Pos.RestauranteApp.service.EmpleadoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List; // Importar List

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoService empleadoService;
    private final MesaRepository mesaRepository; // Inyectar MesaRepository

    public DataInitializer(RolRepository rolRepository,
                           EmpleadoRepository empleadoRepository,
                           EmpleadoService empleadoService,
                           MesaRepository mesaRepository) { // Añadir al constructor
        this.rolRepository = rolRepository;
        this.empleadoRepository = empleadoRepository;
        this.empleadoService = empleadoService;
        this.mesaRepository = mesaRepository; // Asignar
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
            System.out.println("Roles creados.");
        } else {
            rolAdmin = rolRepository.findById(1L).orElse(null);
        }

        // --- 2. CREAR USUARIO ADMIN POR DEFECTO ---
        if (empleadoRepository.count() == 0 && rolAdmin != null) {
            System.out.println("Creando usuario ADMIN por defecto...");
            EmpleadoDTO adminDto = new EmpleadoDTO();
            adminDto.setNombre("Anthony Miguel Palomino Arce");
            adminDto.setDni("72376198");
            adminDto.setUsuario("ThonysAdmin");
            adminDto.setContrasena("admin123");
            adminDto.setIdRol(rolAdmin.getIdRol());

            try {
                empleadoService.guardar(adminDto);
                System.out.println("Usuario ADMIN creado con éxito (ThonysAdmin/admin123)");
            } catch (Exception e) {
                System.err.println("Error al crear usuario ADMIN: " + e.getMessage());
            }
        }

        // --- 3. CREAR LAS 5 MESAS DEL RESTAURANTE ---
        if (mesaRepository.count() == 0) {
            System.out.println("Creando las 5 mesas del restaurante...");

            // Mesa 1
            Mesa mesa1 = new Mesa();
            mesa1.setNumeroMesa(1);
            mesa1.setCapacidad(4); // Capacidad entre 4 y 8
            mesa1.setEstado(Mesa.EstadoMesa.DISPONIBLE); // Todas disponibles

            // Mesa 2
            Mesa mesa2 = new Mesa();
            mesa2.setNumeroMesa(2);
            mesa2.setCapacidad(6); // Capacidad entre 4 y 8
            mesa2.setEstado(Mesa.EstadoMesa.DISPONIBLE);

            // Mesa 3
            Mesa mesa3 = new Mesa();
            mesa3.setNumeroMesa(3);
            mesa3.setCapacidad(8); // Capacidad entre 4 y 8
            mesa3.setEstado(Mesa.EstadoMesa.DISPONIBLE);

            // Mesa 4
            Mesa mesa4 = new Mesa();
            mesa4.setNumeroMesa(4);
            mesa4.setCapacidad(4); // Capacidad entre 4 y 8
            mesa4.setEstado(Mesa.EstadoMesa.DISPONIBLE);

            // Mesa 5
            Mesa mesa5 = new Mesa();
            mesa5.setNumeroMesa(5);
            mesa5.setCapacidad(6); // Capacidad entre 4 y 8
            mesa5.setEstado(Mesa.EstadoMesa.DISPONIBLE);

            // Guardar todas las mesas
            mesaRepository.saveAll(List.of(mesa1, mesa2, mesa3, mesa4, mesa5));
            System.out.println("5 mesas creadas y disponibles.");
        }
        // --- FIN CREAR MESAS ---
    }
}