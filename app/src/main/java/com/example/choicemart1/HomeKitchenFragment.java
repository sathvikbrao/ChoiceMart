package com.example.choicemart1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.choicemart1.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeKitchenFragment extends Fragment {
    private LinearLayout productsLayout;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public static HomeKitchenFragment newInstance(String param1, String param2) {
        HomeKitchenFragment fragment = new HomeKitchenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeKitchenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_kitchen, container, false);
        productsLayout = view.findViewById(R.id.productsHomeKitchen);
        fetchProducts();
        return view;
    }

    private void fetchProducts() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Products");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        String productCategory = productSnapshot.child("ProductCategory").getValue(String.class);

                        if (productCategory != null && productCategory.trim().equalsIgnoreCase("Home & Kitchen")) {
                            String productName = productSnapshot.child("ProductName").getValue(String.class);
                            String productPrice = productSnapshot.child("ProductPrice").getValue(String.class);
                            String productImage = productSnapshot.child("ProductImage").getValue(String.class);
                            String productDescription=productSnapshot.child("ProductDescription").getValue(String.class);
                            if (productName != null && productPrice != null && productImage != null) {
                                addProductView(productName, productPrice, productImage,productDescription);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error, if any
            }
        });
    }

    private void addProductView(String productName, String productPrice, String productImage,String productDescription) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View productView = inflater.inflate(R.layout.item_product, productsLayout, false);

        ImageView productImageView = productView.findViewById(R.id.productImage);
        TextView productNameTextView = productView.findViewById(R.id.productName);
        TextView productPriceTextView = productView.findViewById(R.id.productPrice);

        Glide.with(requireContext())
                .load(productImage)
                .into(productImageView);

        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);

        productsLayout.addView(productView);
        Product product = new Product(productName, productPrice, productImage);
        product.setDescription( productDescription);
        product.setPriceString(productPrice);
        productView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProductDetailFragment(product);

            }

            private void navigateToProductDetailFragment(Product product) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_home_page);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                navController.navigate(R.id.nav_ProductDetails, bundle);
            }
        });
    }
}
