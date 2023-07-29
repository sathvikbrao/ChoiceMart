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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AcessoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcessoriesFragment extends Fragment {
    private LinearLayout productsLayout;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AcessoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AcessoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcessoriesFragment newInstance(String param1, String param2) {
        AcessoriesFragment fragment = new AcessoriesFragment();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsLayout = view.findViewById(R.id.productsLayoutaccessories);

        fetchAccessories();
    }

    private void fetchAccessories() {
        FakeStoreApi api = RetrofitClient.getClient().create(FakeStoreApi.class);
        Call<List<Product>> call = api.getProductsByCategory("jewelery", 10); // Update category to "accessories"

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
                Log.e("AccessoriesFragment", "Failed to fetch accessories: " + t.getMessage());
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acessories, container, false);
    }
}