package com.example.trabajofinal.ui.home;


import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.trabajofinal.R;
import com.example.trabajofinal.Reseña;
import com.example.trabajofinal.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser authUser;
    String mCurrentPhotoPath;
    private Uri photoURI;
    private ImageView foto;
    static final int REQUEST_TAKE_PHOTO = 1;



    //Todo esto lo que hace es para mostrar los datos en el controls de formulari que hemos creado.

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        HomeViewModel sharedViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        // Esto sirve para actualizar la direccion
        HomeViewModel.getCurrentAddress().observe(getViewLifecycleOwner(), address -> {

            binding.txtAddress.setText(String.format(
                    "Direcció: %1$s \n Hora: %2$tr",
                    address, System.currentTimeMillis()));
        });


        //Esto sirve para el indicador de carga y para controlar la visibilidad

        sharedViewModel.getProgressBar().observe(getViewLifecycleOwner(), visible -> {
            if (visible)
                binding.loading.setVisibility(ProgressBar.VISIBLE);
            else
                binding.loading.setVisibility(ProgressBar.INVISIBLE);
        });
        //Iniciar Rastreo
        sharedViewModel.switchTrackingLocation();

        sharedViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            authUser = user;
        });



        //Lo que hacemos aquí es darle al boton y que nos de todo lo que pedimos
        binding.buttonSubmit.setOnClickListener(view -> {
            Reseña reseña = new Reseña();
            reseña.setRestaurantName(binding.txtRestaurantName.getText().toString());
            reseña.setDireccio(binding.txtAddress.getText().toString());
            reseña.setResena(binding.txtReview.getText().toString());
            reseña.setMensaje("hola carles que tal guapo");
            double longitud = Double.parseDouble(binding.txtLongitud.getText().toString().trim());
            double latitud = Double.parseDouble(binding.txtLatitud.getText().toString().trim());

            reseña.setLatitud(latitud);
            reseña.setLongitud(longitud);

            //Para la calificacion
            float rating = binding.ratingBar.getRating();
            reseña.setCalificacion(rating);


            //Almacenar la reseña en la base de datos

            DatabaseReference base = FirebaseDatabase.getInstance("https://adrianpeiro18-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
            DatabaseReference users = base.child("users");
            DatabaseReference uid = users.child(auth.getUid());
            DatabaseReference reseñass = uid.child("reseñas");

            DatabaseReference reference = reseñass.push();
            reference.setValue(reseña);
        });

        foto = root.findViewById(R.id.foto);
        Button buttonFoto = root.findViewById(R.id.button_foto);


        //Para las fotos
        buttonFoto.setOnClickListener(button -> {
            dispatchTakePictureIntent();
        });

        return root;
    }

    //Crear archivo de la imagen temporal

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
//Iniciamos la camara y tomamos la foto
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(
                getContext().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                //Para compartir la foto
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //Esto se hace cuando obtenemos el resultado de la camara

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Glide.with(this).load(photoURI).into(foto);
            } else {
                Toast.makeText(getContext(),
                        "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
