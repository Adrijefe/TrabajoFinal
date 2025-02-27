package com.example.trabajofinal;

public class Reseña {
    private String restaurantName;
    private String direccio;
    private Double latitud;
    private Double longitud;
    private String resena;
    private String url;
    private float calificacion;

    public Reseña() {
    }

    public Reseña(String restaurantName, String direccio, Double latitud, Double longitud, String resena, String url, float calificacion) {
        this.restaurantName = restaurantName;
        this.direccio = direccio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.resena = resena;
        this.url = url;
        this.calificacion = calificacion;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDireccio() {
        return direccio;
    }

    public void setDireccio(String direccio) {
        this.direccio = direccio;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getResena() {
        return resena;
    }

    public void setResena(String resena) {
        this.resena = resena;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }
}
