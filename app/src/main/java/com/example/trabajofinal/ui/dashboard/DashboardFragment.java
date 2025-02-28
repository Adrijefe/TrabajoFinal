package com.example.trabajofinal.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trabajofinal.Reseña;
import com.example.trabajofinal.databinding.FragmentDashboardBinding;
import com.example.trabajofinal.databinding.RvResenasBinding;
import com.example.trabajofinal.ui.home.HomeViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//Mostramos la lista de reseñas del firebase

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    //creamos y configuramos la vista del fragmento

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        sharedViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Referencia a la base de datos Firebase
                DatabaseReference base = FirebaseDatabase.getInstance("https://adrianpeiro18-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                DatabaseReference users = base.child("users");
                DatabaseReference uid = users.child(auth.getUid());
                DatabaseReference resenas = uid.child("reseñas");

                   FirebaseRecyclerOptions<Reseña> opciones = new FirebaseRecyclerOptions.Builder<Reseña>()
                        .setQuery(resenas, Reseña.class)
                        .setLifecycleOwner(this)
                        .build();

                // Crear el adaptador
                ReseñaAdapter adapter = new ReseñaAdapter(opciones);
                binding.rvResenas.setAdapter(adapter);
                binding.rvResenas.setLayoutManager(new LinearLayoutManager(requireContext()));

                adapter.startListening();
            }
        });

        //retorna la vista inflada

        return binding.getRoot();
    }
         //Método que se ejecuta cuando la vista del fragmento se destruye.


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //Adaptador para mostrar las reseñas en el RecyclerView.

    class ReseñaAdapter extends FirebaseRecyclerAdapter<Reseña, ReseñaAdapter.ReseñaViewholder> {

        public ReseñaAdapter(@NonNull FirebaseRecyclerOptions<Reseña> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull ReseñaViewholder holder, int position, @NonNull Reseña model) {
            holder.binding.txtDescripcio.setText(model.getResena());
            holder.binding.txtAdreca.setText(model.getDireccio());
        }

        @NonNull
        @Override
        public ReseñaViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReseñaViewholder(RvResenasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        //ViewHolder que contiene la vista de una reseña.
        class ReseñaViewholder extends RecyclerView.ViewHolder {
            RvResenasBinding binding;

            public ReseñaViewholder(RvResenasBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
