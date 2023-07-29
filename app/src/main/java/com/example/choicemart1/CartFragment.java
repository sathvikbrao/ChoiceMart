package com.example.choicemart1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.choicemart1.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private LinearLayout cartProductsLayout;
    private TextView cartEmptyTextView;
    private List<Product> selectedProducts = new ArrayList<>();

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartProductsLayout = view.findViewById(R.id.cartProductsLayout);
        cartEmptyTextView = view.findViewById(R.id.cartEmptyTextView);

        displayCartProducts();

        Button buyNowButton = view.findViewById(R.id.buy_now_button);
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProducts.isEmpty()) {
                    Toast.makeText(requireContext(), "No products selected", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(requireContext(), PaymentActivity.class);
                    intent.putExtra("selectedProducts", (Serializable) selectedProducts);
                    startActivity(intent);


                }
            }
        });


    }

    private void displayCartProducts() {
        String phone = UserData.getInstance().getUserId();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        usersRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    String userId = userSnapshot.getKey();

                    DatabaseReference cartRef = usersRef.child(userId).child("Cart");

                    cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                    // A product exists in the cart
                                    String productDescription = productSnapshot.child("ProductDescription").getValue(String.class);
                                    String productImageUrl = productSnapshot.child("ProductImageUrl").getValue(String.class);
                                    String productName = productSnapshot.child("ProductName").getValue(String.class);
                                    String productPrice = productSnapshot.child("ProductPrice").getValue(String.class);

                                    // Create a new product view and populate it with data
                                    View productView = LayoutInflater.from(requireContext())
                                            .inflate(R.layout.cart_product, cartProductsLayout, false);

                                    // Set product details
                                    ImageView productImageView = productView.findViewById(R.id.product_image);
                                    TextView productNameTextView = productView.findViewById(R.id.product_name);
                                    TextView productPriceTextView = productView.findViewById(R.id.product_price);
                                    TextView quantityTextView = productView.findViewById(R.id.quantity_text);

                                    // Set image using an image loading library like Glide
                                    Glide.with(requireContext())
                                            .load(productImageUrl)
                                            .into(productImageView);

                                    // Set product name and price
                                    productNameTextView.setText(productName);

                                    double conversionRate = 75.0;
                                    double productPriceValue = Double.parseDouble(productPrice);
                                    double priceInRupees = productPriceValue * conversionRate;
                                    @SuppressLint("StringFormatMatches") String formattedPrice = getString(R.string.rupee_symbol, priceInRupees);

                                    productPriceTextView.setText(formattedPrice);
                                    productName = productNameTextView.getText().toString();
                                    System.out.println(productName);
                                    String productPriceText = productPriceTextView.getText().toString().trim(); // Remove leading/trailing whitespaces
                                    String currencySymbol = getString(R.string.rupee_symbol);

                                    // Remove the currency symbol and any additional non-numeric characters
                                    String numericPrice = productPriceText.replace(currencySymbol, "").replaceAll("[^\\d.]", "");

                                    double productPrice1 = Double.parseDouble(numericPrice);
                                    int quantity = Integer.parseInt(quantityTextView.getText().toString());
                                    Product product=new Product(productName, productPrice1, quantity);
                                    productView.setTag(product);

                                    // Increment button
                                    ImageButton incrementButton = productView.findViewById(R.id.quantity_increase_button);
                                    incrementButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Get the current quantity
                                            int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());

                                            // Increment the quantity
                                            int newQuantity = currentQuantity + 1;

                                            // Update the quantity text view
                                            quantityTextView.setText(String.valueOf(newQuantity));
                                            product.setQuantity(Integer.parseInt(quantityTextView.getText().toString()));

                                        }
                                    });

                                    // Decrement button
                                    ImageButton decrementButton = productView.findViewById(R.id.quantity_decrease_button);
                                    decrementButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Get the current quantity
                                            int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());

                                            // Decrement the quantity, but ensure it doesn't go below 1
                                            int newQuantity = Math.max(currentQuantity - 1, 1);

                                            // Update the quantity text view
                                            quantityTextView.setText(String.valueOf(newQuantity));
                                            product.setQuantity(Integer.parseInt(quantityTextView.getText().toString()));
                                        }
                                    });




                                    productView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            // Toggle selection state
                                            productView.setSelected(!productView.isSelected());

                                            if (productView.isSelected()) {
                                                // Add the selected product to the list

                                                selectedProducts.add(getProductFromView(productView));
                                                View p=productView.findViewById(R.id.cart_product_card);
                                               p.setBackgroundResource(R.drawable.selected_background);
                                            }

                                            return true;
                                        }
                                    });
                                    productView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (productView.isSelected()) {
                                                // Item is selected, deselect it
                                                productView.setSelected(false);
                                                selectedProducts.remove(getProductFromView(productView));
                                                View p = productView.findViewById(R.id.cart_product_card);
                                                p.setBackgroundResource(R.drawable.roundcard3);
                                            }
                                        }
                                    });


                                    Button removeButton = productView.findViewById(R.id.remove_button);
                                    removeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                            builder.setTitle("Confirmation");
                                            builder.setMessage("Are you sure you want to remove this product from the cart?");

                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Get the product key from the database snapshot
                                                    String productKey = productSnapshot.getKey();

                                                    // Remove the product from the database
                                                    cartRef.child(productKey).removeValue();

                                                    // Remove the product view from the layout
                                                    cartProductsLayout.removeView(productView);
                                                }
                                            });

                                            builder.setNegativeButton("Cancel", null);

                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    });

                                    // Add the product view to the layout
                                    cartProductsLayout.addView(productView);
                                }
                            } else {
                                // Cart is empty
                                cartEmptyTextView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors that occur during data retrieval
                        }
                    });
                } else {
                    // User does not exist or is not logged in, display cart empty message
                    cartEmptyTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval
            }
        });
    }

    private Product getProductFromView(View productView) {


        return (Product) productView.getTag();
    }

}
