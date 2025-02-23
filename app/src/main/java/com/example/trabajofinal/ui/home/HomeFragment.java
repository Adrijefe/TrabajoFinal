package com.example.trabajofinal.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.trabajofinal.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    //Todo esto lo que hace es para mostrar los datos en el controls de formulari que hemos creado.

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        HomeViewModel sharedViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        sharedViewModel.getCurrentAddress().observe(getViewLifecycleOwner(), address -> {
            binding.txtAddress.setText(String.format(
                    "Direcció: %1$s \n Hora: %2$tr",
                    address, System.currentTimeMillis()));
        });
        sharedViewModel.getButtonText().observe(getViewLifecycleOwner(), s -> binding.buttonSubmit.setText(s));
        sharedViewModel.getProgressBar().observe(getViewLifecycleOwner(), visible -> {
            if (visible)
                binding.txtReview.setVisibility(RatingBar.VISIBLE);
            else
                binding.txtReview.setVisibility(RatingBar.INVISIBLE);
        });

        binding.buttonSubmit.setOnClickListener(view -> {
            Log.d("DEBUG", "Clicked Get Location");
            sharedViewModel.switchTrackingLocation();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
