package com.Pos.RestauranteApp.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea la URL /images/** a la carpeta local imagenes_productos/
        registry.addResourceHandler("/images/**")
        .addResourceLocations("file:D:/Utp/Ciclo 8/Integrador 1/PrincipalPOS/imagenes-productos/");

    }
}
