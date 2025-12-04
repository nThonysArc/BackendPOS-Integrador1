package com.Pos.RestauranteApp.model;

public class ObtactuloMapa {
    // Atributos para implementacion de OBSTACILOS en el mapa de mesas
    private Double posX; // Coordenada X en el mapa
    private Double posY; // Coordenada Y en el mapa
    private Double width; // Ancho de la mesa en el mapa
    private Double height; // Altura de la mesa en el mapa
    private Double rotation; // Rotación de la mesa en grados
    private String shapeType; // Tipo de forma de la mesa (Rectangular, Circular, customizada)
    
    // Getters y Setters
    public Double getPosX() {
        return posX;
    }
    public void setPosX(Double posX) {
        this.posX = posX;
    }
    public Double getPosY() {
        return posY;
    }
    public void setPosY(Double posY) {
        this.posY = posY;
    }
    public Double getWidth() {
        return width;
    }
    public void setWidth(Double width) {
        this.width = width;
    }
    public Double getHeight() {
        return height;
    }
    public void setHeight(Double height) {
        this.height = height;
    }
    public Double getRotation() {
        return rotation;
    }
    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }
    public String getShapeType() {
        return shapeType;
    }
    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }
}
