package com.example.choicemart1.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.choicemart1.R;
import com.example.choicemart1.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        LinearLayout electricalAppliancesCat = root.findViewById(R.id.elctrical_appliances_cat);
        LinearLayout clothesCat = root.findViewById(R.id.clothes_cat);
        LinearLayout accessoriesCat = root.findViewById(R.id.accessories_cat);
        LinearLayout booksCat =root. findViewById(R.id.Books_cat);
        LinearLayout kitchenAppliancesCat =root.findViewById(R.id.Kitchen_appliances_cat);
        electricalAppliancesCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_electronic_appliances);
            }
        });
        booksCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event for books category
                navController.navigate(R.id.nav_book);
            }
        });
        clothesCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event for clothes category
                navController.navigate(R.id.nav_clothes);
            }
        });
        accessoriesCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event for fashion accessories category
                navController.navigate(R.id.nav_accessories);
            }
        });
        kitchenAppliancesCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event for fashion accessories category
                navController.navigate(R.id.nav_HomeKitchen);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
