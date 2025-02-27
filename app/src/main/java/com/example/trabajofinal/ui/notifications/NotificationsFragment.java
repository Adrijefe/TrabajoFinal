package com.example.trabajofinal.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.trabajofinal.R;
import com.example.trabajofinal.Reseña;
import com.example.trabajofinal.ui.home.HomeFragment;
import com.example.trabajofinal.ui.home.HomeViewModel;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import com.example.trabajofinal.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private HomeViewModel sharedViewModel;
    private FirebaseAuth auth;
    private DatabaseReference incidencies;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Cargar configuración de OpenStreetMap
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Configuración del mapa
        binding.map.setTileSource(TileSourceFactory.MAPNIK);
        binding.map.setMultiTouchControls(true);
        IMapController mapController = binding.map.getController();
        mapController.setZoom(14.5);

        // Cargar Home
        sharedViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // ubicación actual
//        sharedViewModel.getCurrentLatLng().observe(getViewLifecycleOwner(), location -> {
//            if (location != null) {
//                GeoPoint geoPoint = new GeoPoint(location.latitude, location.longitude);
//                mapController.setCenter(geoPoint);
//            }
//        });

        // Para que me salga en la vall.
            GeoPoint miUbi = new GeoPoint(39.8233, -0.232562);
        mapController.setCenter(miUbi);

        Marker startMarker = new Marker(binding.map);
        startMarker.setPosition(miUbi);
        startMarker.setTitle("Casa");
        startMarker.setIcon(requireContext().getDrawable(R.drawable.ic_home_black_24dp));
        binding.map.getOverlays().add(startMarker);


        //ubicación del usuario
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), binding.map);
        myLocationNewOverlay.enableMyLocation();
        binding.map.getOverlays().add(myLocationNewOverlay);

        //brújula
        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), new InternalCompassOrientationProvider(requireContext()), binding.map);
        compassOverlay.enableCompass();
        binding.map.getOverlays().add(compassOverlay);


        // Conectar a Firebase
        auth = FirebaseAuth.getInstance();
        DatabaseReference base = FirebaseDatabase.getInstance("https://adrianpeiro18-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference users = base.child("users");
        DatabaseReference uid = users.child(auth.getUid());
        incidencies = uid.child("reseñas");
        Log.d("XXX",incidencies.toString());



        // Obtener incidencias de Firebase y mostrar marcadores
        incidencies.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if(binding == null || binding.map == null){
                    Log.e("FirebaseError", "El fragmento ya no está visible.");
                    return;
                }
                Reseña resena = dataSnapshot.getValue(Reseña.class);
                Double latitud = dataSnapshot.child("latitud").getValue(Double.class);
                Double longitud = dataSnapshot.child("longitud").getValue(Double.class);
                if (resena != null){

                    GeoPoint location = new GeoPoint(latitud,longitud);
                    Marker marker = new Marker(binding.map);
                    marker.setPosition(location);
                    marker.setTitle(resena.getRestaurantName());
                    marker.setSnippet(resena.getResena());
                    String snippetInfo = "Reseña: " + resena.getResena() + "\nCalificación: " + resena.getCalificacion() + " ★";
                    marker.setSnippet(snippetInfo);

                    binding.map.getOverlays().add(marker);
                }





                }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.map.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


