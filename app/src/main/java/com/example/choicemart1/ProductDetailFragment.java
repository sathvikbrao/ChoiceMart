package com.example.choicemart1;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.choicemart1.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailFragment extends Fragment {
    private static final String ARG_PRODUCT = "product";

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView productImageView = view.findViewById(R.id.productImage1);
        TextView productNameTextView = view.findViewById(R.id.productName1);
        TextView productPriceTextView = view.findViewById(R.id.productPrice1);
        TextView productDescriptionTextView = view.findViewById(R.id.productDescription1);
        String phone = UserData.getInstance().getUserId();
        System.out.println("hi this is productDetailFragment "+phone);
        // Get the product object from the arguments
        Product product = (Product) getArguments().getSerializable(ARG_PRODUCT);

        // Set the values to the views
        Glide.with(requireContext())
                .load(product.getImage())
                .into(productImageView);

        productNameTextView.setText(product.getTitle());
        if(product.getPriceString() == null) {


            double conversionRate = 75.0;
            double priceInRupees = product.getPrice() * conversionRate;
            productPriceTextView.setText(getString(R.string.rupee_symbol, priceInRupees));
            System.out.println(priceInRupees);
        }
        else {
            System.out.println(product.getPriceString());
            productPriceTextView.setText(getString(R.string.rupee_symbol, product.getPriceString()));

        }
        productDescriptionTextView.setText(product.getDescription());

        Button addToCartButton = view.findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartClicked();
            }
        });


    }
    private void addToCartClicked() {
        // Get the product object from the arguments
        Product product = (Product) getArguments().getSerializable(ARG_PRODUCT);

        // Get the user ID (assuming it is the same as the Phone)
        String userId = UserData.getInstance().getUserId();

        // Create a new Firebase reference to the user's cart
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .child("Cart");

        // Query to check if the product already exists in the cart
        Query productQuery = cartRef.orderByChild("ProductName").equalTo(product.getTitle());

        productQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Product already exists in the cart, show toast message
                    Toast.makeText(requireContext(), "Product already added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new unique key for the product
                    DatabaseReference productRef = cartRef.push();

                    // Set the product details as child values under the unique key
                    productRef.child("ProductName").setValue(product.getTitle());
                    productRef.child("ProductImageUrl").setValue(product.getImage());
                    // Assuming product.getPrice() returns a double value
                    // Assuming product.getPrice() returns a double value
                    if(product.getPriceString()==null) {
                        String productPriceString = String.valueOf(product.getPrice());
                        productRef.child("ProductPrice").setValue(productPriceString);
                    }

                    else{
                        String productPriceString = product.getPriceString();
                        productRef.child("ProductPrice").setValue(productPriceString);
                    }
                    productRef.child("ProductDescription").setValue(product.getDescription())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Show success message
                                        Toast.makeText(requireContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Show failure message
                                        Toast.makeText(requireContext(), "Failed to add product to cart", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(requireContext(), "Failed to add product to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
