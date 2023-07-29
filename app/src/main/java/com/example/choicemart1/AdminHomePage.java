package com.example.choicemart1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.choicemart1.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminHomePage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Button deleteButton;
    private Button addButton;
    private Button logoutButton;
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);
        addButton=findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomePage.this,AdminAddProduct.class);
                startActivity(intent);
            }
        });
        logoutButton=findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomePage.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.productContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this);
        recyclerView.setAdapter(productAdapter);


        productList = new ArrayList<>();

        fetchDatabaseProducts();
        deleteButton = findViewById(R.id.buttonDelete); // Add this line

        // Add a click listener to the delete button
        deleteButton.setOnClickListener(view -> {
            List<Product> selectedProducts = productAdapter.getSelectedProducts();
            for (Product product : selectedProducts) {
                // Remove the selected products from the list
                productAdapter.removeProduct(product);
            }
            // Clear the selection mode after deletion
            productAdapter.setSelectionMode(false);
        });
    }

    private void fetchDatabaseProducts() {
        productsRef = FirebaseDatabase.getInstance().getReference("Products");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        String name = productSnapshot.child("ProductName").getValue(String.class);
                        String price = productSnapshot.child("ProductPrice").getValue(String.class);
                        String image = productSnapshot.child("ProductImage").getValue(String.class);
                        String category = categorySnapshot.getKey(); // Get the category key
                        System.out.println(price + "hi");
                        Product product = new Product(name, price, image);
                        product.setProductCategory(category); // Set the category for the product

                        productList.add(product);
                        System.out.println("hello ");
                    }
                }

                runOnUiThread(() -> {
                    productAdapter.setProducts(productList);
                });
                fetchProducts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminHomePage.this, "Failed to fetch products from database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void fetchProducts() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set a custom connection timeout
                .readTimeout(30, TimeUnit.SECONDS) // Set a custom read timeout
                .build();
        Request request = new Request.Builder()
                .url("https://fakestoreapi.com/products")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure to fetch products
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new GsonBuilder().create();
                    Product[] products = gson.fromJson(json, Product[].class);

                    runOnUiThread(() -> {
                        for (Product product : products) {
                            productList.add(product); // Add the API product to the existing list
                        }
                        productAdapter.setProducts(productList);
                    });
                } else {
                    // Handle the unsuccessful response
                    Log.e("AdminHomePage", "Failed to fetch products. Code: " + response.code());
                }
            }
        });
    }

}