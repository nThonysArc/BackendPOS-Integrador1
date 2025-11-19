package com.Pos.RestauranteApp.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.Pos.RestauranteApp.dto.EmpleadoDTO;
import com.Pos.RestauranteApp.model.Categoria;
import com.Pos.RestauranteApp.model.Mesa;
import com.Pos.RestauranteApp.model.Producto;
import com.Pos.RestauranteApp.model.Rol;
import com.Pos.RestauranteApp.repository.CategoriaRepository;
import com.Pos.RestauranteApp.repository.EmpleadoRepository;
import com.Pos.RestauranteApp.repository.MesaRepository;
import com.Pos.RestauranteApp.repository.ProductoRepository;
import com.Pos.RestauranteApp.repository.RolRepository;
import com.Pos.RestauranteApp.service.EmpleadoService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoService empleadoService;
    private final MesaRepository mesaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public DataInitializer(RolRepository rolRepository,
                           EmpleadoRepository empleadoRepository,
                           EmpleadoService empleadoService,
                           MesaRepository mesaRepository,
                           CategoriaRepository categoriaRepository,
                           ProductoRepository productoRepository) {
        this.rolRepository = rolRepository;
        this.empleadoRepository = empleadoRepository;
        this.empleadoService = empleadoService;
        this.mesaRepository = mesaRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
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
                System.out.println("Usuario ADMIN creado con éxito.");
            } catch (Exception e) {
                System.err.println("Error al crear usuario ADMIN: " + e.getMessage());
            }
        }

        // --- 3. CREAR LAS 5 MESAS DEL RESTAURANTE ---
        if (mesaRepository.count() == 0) {
            System.out.println("Creando las 5 mesas del restaurante...");
            Mesa mesa1 = new Mesa(); mesa1.setNumeroMesa(1); mesa1.setCapacidad(4); mesa1.setEstado(Mesa.EstadoMesa.DISPONIBLE);
            Mesa mesa2 = new Mesa(); mesa2.setNumeroMesa(2); mesa2.setCapacidad(6); mesa2.setEstado(Mesa.EstadoMesa.DISPONIBLE);
            Mesa mesa3 = new Mesa(); mesa3.setNumeroMesa(3); mesa3.setCapacidad(8); mesa3.setEstado(Mesa.EstadoMesa.DISPONIBLE);
            Mesa mesa4 = new Mesa(); mesa4.setNumeroMesa(4); mesa4.setCapacidad(4); mesa4.setEstado(Mesa.EstadoMesa.DISPONIBLE);
            Mesa mesa5 = new Mesa(); mesa5.setNumeroMesa(5); mesa5.setCapacidad(6); mesa5.setEstado(Mesa.EstadoMesa.DISPONIBLE);
            mesaRepository.saveAll(List.of(mesa1, mesa2, mesa3, mesa4, mesa5));
            System.out.println("5 mesas creadas.");
        }

        // --- 4. CREAR MENU (CATEGORÍAS Y PRODUCTOS) ---
        if (categoriaRepository.count() == 0) {
            System.out.println("Inicializando Menú (Categorías y Productos)...");

            // ================= ENTRADAS =================
            Categoria entradas = categoriaRepository.save(new Categoria("Entradas", null));

            Categoria entradasFrias = categoriaRepository.save(new Categoria("Entradas Frías", entradas));
            crearProducto("Causa Rellena de pollo", 15.0, entradasFrias);
            crearProducto("Causa Rellena de atún", 16.0, entradasFrias);
            crearProducto("Papa a la Huancaina", 14.0, entradasFrias);
            crearProducto("Ocopa Arequipeña", 14.0, entradasFrias);
            crearProducto("Solterito", 12.0, entradasFrias);
            crearProducto("Choritos a la Chalaca", 18.0, entradasFrias);

            Categoria entradasCalientes = categoriaRepository.save(new Categoria("Entradas Calientes", entradas));
            crearProducto("Papa Rellena", 8.0, entradasCalientes);
            crearProducto("Tamal Criollo", 6.0, entradasCalientes);
            crearProducto("Tamal Verde", 6.0, entradasCalientes);
            crearProducto("Rocoto Relleno", 20.0, entradasCalientes);
            crearProducto("Chicharrón de Calamar", 22.0, entradasCalientes);

            Categoria sopas = categoriaRepository.save(new Categoria("Sopas y Cremas", entradas));
            crearProducto("Sopa a la Minuta", 18.0, sopas);
            crearProducto("Sopa Criolla", 20.0, sopas);
            crearProducto("Chupe de Camarones", 35.0, sopas);


            // ================= PLATOS A LA CARTA =================
            Categoria carta = categoriaRepository.save(new Categoria("Platos a la carta", null));

            Categoria criollos = categoriaRepository.save(new Categoria("Clásicos Criollos", carta));
            crearProducto("Lomo Saltado", 38.0, criollos);
            crearProducto("Ají de Gallina", 28.0, criollos);
            crearProducto("Seco de Res con Frejoles", 32.0, criollos);
            crearProducto("Carapulcra", 25.0, criollos);

            Categoria pescados = categoriaRepository.save(new Categoria("Pescados y Mariscos", carta));
            crearProducto("Ceviche Clásico", 35.0, pescados);
            crearProducto("Jalea Mixta", 40.0, pescados);
            crearProducto("Arroz con Mariscos", 38.0, pescados);
            crearProducto("Sudado de Pescado", 36.0, pescados);

            Categoria arroces = categoriaRepository.save(new Categoria("Arroces y Tacu Tacus", carta));
            crearProducto("Arroz Chaufa de Carne", 25.0, arroces);
            crearProducto("Arroz Chaufa de Pollo", 22.0, arroces);
            crearProducto("Arroz Chaufa de Chancho", 25.0, arroces);
            crearProducto("Tacu Tacu con Lomo", 42.0, arroces);
            crearProducto("Arroz con Pato", 45.0, arroces);

            Categoria pastas = categoriaRepository.save(new Categoria("Pastas Criollas", carta));
            crearProducto("Tallarines Verdes con Bistec", 30.0, pastas);
            crearProducto("Tallarines a la Huancaina con Lomo", 40.0, pastas);


            // ================= FONDO =================
            Categoria fondo = categoriaRepository.save(new Categoria("Fondo", null));

            Categoria guisos = categoriaRepository.save(new Categoria("Guisos del Día", fondo));
            crearProducto("Olluquito", 15.0, guisos);
            crearProducto("Cau Cau", 15.0, guisos);
            crearProducto("Mondonguito a la italiana", 15.0, guisos);
            crearProducto("Chanfainita", 15.0, guisos);

            Categoria aves = categoriaRepository.save(new Categoria("Aves", fondo));
            crearProducto("Milanesa de Pollo", 22.0, aves);
            crearProducto("Pollo al Horno", 20.0, aves);
            crearProducto("Escabeche de Pollo", 22.0, aves);

            Categoria carnes = categoriaRepository.save(new Categoria("Carnes Rojas", fondo));
            crearProducto("Bistec a lo Pobre", 28.0, carnes);
            crearProducto("Asado de Res", 25.0, carnes);

            Categoria cerdo = categoriaRepository.save(new Categoria("Cerdo", fondo));
            crearProducto("Adobo de Cerdo", 28.0, cerdo);
            crearProducto("Chuleta frita", 24.0, cerdo);


            // ================= BEBIDAS =================
            Categoria bebidas = categoriaRepository.save(new Categoria("Bebidas", null));

            Categoria refrescos = categoriaRepository.save(new Categoria("Refrescos Naturales", bebidas));
            crearProducto("Chicha Morada (Jarra)", 15.0, refrescos);
            crearProducto("Chicha Morada (Vaso)", 5.0, refrescos);
            crearProducto("Limonada (Jarra)", 12.0, refrescos);
            crearProducto("Maracuyá (Jarra)", 12.0, refrescos);
            crearProducto("Emoliente frío", 4.0, refrescos);

            Categoria gaseosas = categoriaRepository.save(new Categoria("Gaseosas y Aguas", bebidas));
            crearProducto("Inka Cola 500ml", 5.0, gaseosas);
            crearProducto("Coca Cola 500ml", 5.0, gaseosas);
            crearProducto("Agua con gas", 3.0, gaseosas);
            crearProducto("Agua sin gas", 3.0, gaseosas);

            Categoria cervezas = categoriaRepository.save(new Categoria("Cervezas", bebidas));
            crearProducto("Pilsen", 8.0, cervezas);
            crearProducto("Cusqueña Trigo", 10.0, cervezas);
            crearProducto("Cusqueña Negra", 10.0, cervezas);
            crearProducto("Cusqueña Dorada", 10.0, cervezas);

            Categoria cocteles = categoriaRepository.save(new Categoria("Cocteles y Vinos", bebidas));
            crearProducto("Pisco Sour", 20.0, cocteles);
            crearProducto("Chilcano", 18.0, cocteles);
            crearProducto("Algarrobina", 18.0, cocteles);
            crearProducto("Copa de Vino", 15.0, cocteles);

            Categoria calientes = categoriaRepository.save(new Categoria("Infusiones y Calientes", bebidas));
            crearProducto("Café pasado", 6.0, calientes);
            crearProducto("Té", 4.0, calientes);
            crearProducto("Anís", 4.0, calientes);
            crearProducto("Manzanilla", 4.0, calientes);

            System.out.println("Menú inicializado correctamente.");
        }
    }

    // Método auxiliar para crear productos rápidamente
    private void crearProducto(String nombre, Double precio, Categoria categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        p.setActivo(true);
        p.setDescripcion(nombre); 
        productoRepository.save(p);
    }
}