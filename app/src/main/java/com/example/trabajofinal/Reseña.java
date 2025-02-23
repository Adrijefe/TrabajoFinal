package com.example.trabajofinal;

import android.widget.ProgressBar;

public class Reseña {
    private String restaurantName;
    private String direccio;
    private String latitud;
    private String longitud;
    private String resena;

    public Reseña() {
    }

    // Constructor con
    public Reseña(String restaurantName, String direccio, String latitud, String longitud, String problema) {
        this.restaurantName = restaurantName;
        this.direccio = direccio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.resena = problema;
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

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getProblema() {
        return resena;
    }

    public void setProblema(String problema) {
        this.resena = problema;
    }
}