package com.example.trabajofinal.ui.home;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// Gestionamos la lógica de la ubicacion y autenticacion

public class HomeViewModel extends AndroidViewModel {
    private final Application app;
    private static final MutableLiveData<String> currentAddress = new MutableLiveData<>();
    private final MutableLiveData<String> checkPermission = new MutableLiveData<>();
    private final MutableLiveData<String> buttonText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBar = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentLatLng = new MutableLiveData<>();
    private boolean mTrackingLocation;

    FusedLocationProviderClient mFusedLocationClient;

    //Constructor
    public HomeViewModel(Application application){
        super(application);
        this.app = application;
    }


    //Configurar cliente de ubicacion
    public void setFusedLocationClient(FusedLocationProviderClient mFusedLocationClient) {
        this.mFusedLocationClient = mFusedLocationClient;
    }

    //obtener direccion actual
    public static LiveData<String> getCurrentAddress() {
        return currentAddress;
    }

    //Controlar la visibilidad
    public MutableLiveData<Boolean> getProgressBar() {
        return progressBar;
    }
    //chekear permisos
    public LiveData<String> getCheckPermission() {
        return checkPermission;
    }

    //Calback que se ejecuta cuando  recibe una actualizacion de ubi
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                fetchAddress(locationResult.getLastLocation());
            }
        }
    };

    //Solicitar ubi
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    //alterar rastreo
    public void switchTrackingLocation() {
        if (!mTrackingLocation) {
            startTrackingLocation(true);
        } else {
            stopTrackingLocation();
        }

    }
    //Obtener el usuario autentico
    public LiveData<FirebaseUser>getUser(){
        return user;
    }

    //Actualizar
    public void setUser(FirebaseUser passedUser){
        user.postValue(passedUser);
    }


    //Iniciar el rastreo

    @SuppressLint("MissingPermission")
    public void startTrackingLocation(boolean needsChecking) {
        if (needsChecking) {
            checkPermission.postValue("check");
        } else {
            mFusedLocationClient.requestLocationUpdates(
                    getLocationRequest(),
                    mLocationCallback, null
            );

            currentAddress.postValue("Carregant...");

            progressBar.postValue(true);
            mTrackingLocation = true;
            buttonText.setValue("Aturar el seguiment de la ubicació");
        }
    }

    //Detener el rastreo

    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mTrackingLocation = false;
            progressBar.postValue(false);
            buttonText.setValue("Comença a seguir la ubicació");
        }
    }




    //Convierte una ubicación en una dirección usando Geocoder

    private void fetchAddress(Location location) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        Geocoder geocoder = new Geocoder(app.getApplicationContext(), Locale.getDefault());

        executor.execute(() -> {
            List<Address> addresses;
            String resultMessage = "";

            try {
                //obtenemos la direccion a partir de la latitud y longitud
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

                currentLatLng.postValue(latlng);


                if (addresses == null || addresses.size() == 0) {
                    if (resultMessage.isEmpty()) {
                        resultMessage = "No s'ha trobat cap adreça";
                        Log.e("INCIVISME", resultMessage);
                    }
                } else {
                    Address address = addresses.get(0);
                    ArrayList<String> addressParts = new ArrayList<>();

                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressParts.add(address.getAddressLine(i));
                    }

                    resultMessage = TextUtils.join("\n", addressParts);
                    String finalResultMessage = resultMessage;
                    handler.post(() -> {
                        if (mTrackingLocation)
                            currentAddress.postValue(String.format("Direcció: %1$s \n Hora: %2$tr", finalResultMessage, System.currentTimeMillis()));
                    });
                }




            } catch (IOException ioException) {
                resultMessage = "Servei no disponible";
                Log.e("INCIVISME", resultMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                resultMessage = "Coordenades no vàlides";
                Log.e("INCIVISME", resultMessage + ". " + "Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude(), illegalArgumentException);
            }
        });
    }

}