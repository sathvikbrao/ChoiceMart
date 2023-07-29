package com.example.choicemart1.api;

import com.example.choicemart1.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FakeStoreApi {
    @GET("products/category/{category}")
    Call<List<Product>> getProductsByCategory(@Path("category") String category, @Query("limit") int limit);

}