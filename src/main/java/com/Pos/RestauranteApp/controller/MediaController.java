package com.Pos.RestauranteApp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Pos.RestauranteApp.model.Imagen;
import com.Pos.RestauranteApp.repository.ImagenRepository;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*") // Permitir acceso desde cualquier lado
public class MediaController {

    @Autowired
    private ImagenRepository imagenRepository;

    // 1. SUBIR IMAGEN (Guarda en BD Railway)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Imagen imagen = new Imagen();
            imagen.setNombre(file.getOriginalFilename());
            imagen.setTipo(file.getContentType());
            imagen.setDatos(file.getBytes()); // Convertir archivo a bytes

            Imagen guardada = imagenRepository.save(imagen);

            // Devolvemos la URL relativa. El frontend le pondrá el dominio.
            // Ejemplo de respuesta: "/api/media/15"
            String fileUrl = "/api/media/" + guardada.getId();

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al subir imagen a la base de datos");
        }
    }

    // 2. OBTENER IMAGEN (Lee de BD Railway)
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImagen(@PathVariable Long id) {
        return imagenRepository.findById(id)
                .map(imagen -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(imagen.getTipo() != null ? imagen.getTipo() : "image/jpeg"))
                        .body(imagen.getDatos())) // Devuelve los bytes directos
                .orElse(ResponseEntity.notFound().build());
    }
}