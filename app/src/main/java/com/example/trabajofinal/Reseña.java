package com.example.trabajofinal;

import android.widget.ProgressBar;

public class Reseña {
    String restaurantName;
    String direccio;
    Double latitud;
    Double longitud;
    String resena;
    String url;

    public Reseña() {
    }

    public Reseña(String restaurantName, String direccio, Double latitud, Double longitud, String resena, String url) {
        this.restaurantName = restaurantName;
        this.direccio = direccio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.resena = resena;
        this.url = url;
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

    public void setDireccio(String direccio) {
        this.direccio = direccio;
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
}

