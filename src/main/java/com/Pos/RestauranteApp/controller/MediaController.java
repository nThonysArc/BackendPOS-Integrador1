package com.Pos.RestauranteApp.controller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    // Ruta absoluta fuera del repo (ajústala según tu servidor)
    private final String UPLOAD_DIR = "D:/Utp/Ciclo 8/Integrador 1/PrincipalPOS/imagenes-productos/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Crear directorio si no existe
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generar nombre único para evitar duplicados
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // Guardar archivo
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // URL accesible (gracias al ResourceHandler en WebConfig)
            String fileUrl = "http://localhost:8080/images/" + fileName;

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al subir imagen");
        }
    }
}

