package com.example.choicemart1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.choicemart1.api.FakeStoreApi;
import com.example.choicemart1.api.RetrofitClient;
import com.example.choicemart1.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClothFragment extends Fragment {

    private LinearLayout productsLayout;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ClothFragment() {
        // Required empty public constructor
    }

    public static ClothFragment newInstance(String param1, String param2) {
        ClothFragment fragment = new ClothFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloth, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsLayout = view.findViewById(R.id.productsLayoutcloth);

        fetchClothingProducts();
    }

    private void fetchClothingProducts() {
        FakeStoreApi api = RetrofitClient.getClient().create(FakeStoreApi.class);
        Call<List<Product>> menCall = api.getProductsByCategory("men's clothing", 6);
        Call<List<Product>> womenCall = api.getProductsByCategory("women's clothing", 6);

        menCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> menProducts = response.body();
                    if (menProducts != null) {
                        displayProducts(menProducts);
                    }
                }
            }


            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ClothFragment", "Failed to fetch men's clothing products: " + t.getMessage());
            }
        });

        womenCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> womenProducts = response.body();
                    if (womenProducts != null) {
                        displayProducts(womenProducts);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ClothFragment", "Failed to fetch women's clothing products: " + t.getMessage());
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
