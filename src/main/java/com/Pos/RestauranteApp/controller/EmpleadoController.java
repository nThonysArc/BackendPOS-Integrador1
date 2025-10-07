package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.EmpleadoDTO;
import com.Pos.RestauranteApp.service.EmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public List<EmpleadoDTO> listarTodos() {
        return empleadoService.listarTodos();
    }

    @GetMapping("/{id}")
    public EmpleadoDTO obtenerPorId(@PathVariable Long id) {
        return empleadoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado"));
    }

    @PostMapping
    public EmpleadoDTO crear(@RequestBody EmpleadoDTO empleadoDTO) {
        return empleadoService.guardar(empleadoDTO);
    }

    @PutMapping("/{id}")
    public EmpleadoDTO actualizar(@PathVariable Long id, @RequestBody EmpleadoDTO empleadoDTO) {
        empleadoDTO.setIdEmpleado(id);
        return empleadoService.guardar(empleadoDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }
}
