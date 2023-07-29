package com.example.choicemart1;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.choicemart1.api.FakeStoreApi;
import com.example.choicemart1.api.RetrofitClient;
import com.example.choicemart1.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Electronic_appliances extends Fragment {
    private LinearLayout productsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_electronic_appliances, container, false);
        productsLayout = view.findViewById(R.id.productsLayout);
        fetchElectronicProducts();
        return view;
    }

    private void fetchElectronicProducts() {
        FakeStoreApi api = RetrofitClient.getClient().create(FakeStoreApi.class);
        Call<List<Product>> call = api.getProductsByCategory("electronics", 20);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> products = response.body();
                    if (products != null) {
                        displayProducts(products);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("Electronic_appliances", "Failed to fetch electronic products: " + t.getMessage());
            }
        });
    }

    @SuppressLint("StringFormatMatches")
    private void displayProducts(List<Product> products) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (Product product : products) {
            View productView = inflater.inflate(R.layout.item_product, productsLayout, false);

            ImageView productImage = productView.findViewById(R.id.productImage);
            TextView productName = productView.findViewById(R.id.productName);
            TextView productPrice = productView.findViewById(R.id.productPrice);

            Glide.with(requireContext())
                    .load(product.getImage())
                    .into(productImage);

            productName.setText(product.getTitle());

            double conversionRate = 75.0;
            double priceInRupees = product.getPrice() * conversionRate;
            productPrice.setText(getString(R.string.rupee_symbol, priceInRupees));

            productsLayout.addView(productView);

            productView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToProductDetailFragment(product);
                }
            });
        }
    }

    private void navigateToProductDetailFragment(Product product) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_home_page);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        navController.navigate(R.id.nav_ProductDetails, bundle);
    }
}
