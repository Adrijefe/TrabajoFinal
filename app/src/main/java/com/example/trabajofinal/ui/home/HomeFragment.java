package com.example.trabajofinal.ui.home;


import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.trabajofinal.Reseña;
import com.example.trabajofinal.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser authUser;



    //Todo esto lo que hace es para mostrar los datos en el controls de formulari que hemos creado.

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        HomeViewModel sharedViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        HomeViewModel.getCurrentAddress().observe(getViewLifecycleOwner(), address -> {

            binding.txtAddress.setText(String.format(
                    "Direcció: %1$s \n Hora: %2$tr",
                    address, System.currentTimeMillis()));
        });

        sharedViewModel.getCurrentLatLng().observe(getViewLifecycleOwner(), latlng -> {
            binding.txtLatitud.setText(String.valueOf(latlng.latitude));
            binding.txtLongitud.setText(String.valueOf(latlng.longitude));
        });

        sharedViewModel.getProgressBar().observe(getViewLifecycleOwner(), visible -> {
            if (visible)
                binding.loading.setVisibility(ProgressBar.VISIBLE);
            else
                binding.loading.setVisibility(ProgressBar.INVISIBLE);
        });
        sharedViewModel.switchTrackingLocation();

        sharedViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            authUser = user;
        });



        binding.buttonSubmit.setOnClickListener(view -> {
            Reseña reseña = new Reseña();
            reseña.setRestaurantName(binding.txtRestaurantName.getText().toString());
            reseña.setDireccio(binding.txtAddress.getText().toString());
            reseña.setLatitud(binding.txtLatitud.getText().toString());
            reseña.setLongitud(binding.txtLongitud.getText().toString());
            reseña.setProblema(binding.txtReview.getText().toString());

            DatabaseReference base = FirebaseDatabase.getInstance("https://adrianpeiro18-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

            DatabaseReference users = base.child("users");
            DatabaseReference uid = users.child(auth.getUid());
            DatabaseReference reseñass = uid.child("reseñas");

            DatabaseReference reference = reseñass.push();
            reference.setValue(reseña);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
