package com.example.choicemart1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choicemart1.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private List<Product> selectedProducts = new ArrayList<>();
    private boolean isSelectionMode = false;

    public ProductAdapter(Context context) {
        this.context = context;
    }

    public void setProducts(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public void toggleSelection(Product product) {
        if (selectedProducts.contains(product)) {
            selectedProducts.remove(product);
        } else {
            selectedProducts.add(product);
        }
        notifyDataSetChanged();
    }

    public void setSelectionMode(boolean isSelectionMode) {
        this.isSelectionMode = isSelectionMode;
        selectedProducts.clear();
        notifyDataSetChanged();
    }

    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }

    public void removeProduct(Product product) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Products");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isProductFound = false;

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        String productName = productSnapshot.child("ProductName").getValue(String.class);
                        String productImageUri = productSnapshot.child("ProductImage").getValue(String.class);

                        if (productName != null && productName.equals(product.getTitle())) {
                            // Remove from the database
                            productSnapshot.getRef().removeValue();

                            // Remove from the list
                            productList.remove(product);
                            selectedProducts.remove(product);
                            notifyDataSetChanged();

                            // Delete the image from storage
                            if (productImageUri != null) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(productImageUri);
                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Image deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors that occurred during image deletion
                                    }
                                });
                            }

                            isProductFound = true;
                            break; // Assuming there's only one product with the same name
                        }
                    }
                }

                if (!isProductFound) {
                    // Product not found in the database
                    // Remove from the list only
                    productList.remove(product);
                    selectedProducts.remove(product);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error, if any
            }
        });
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                selectedProducts.add(product);
                notifyDataSetChanged();
                return true;
            }
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleSelection(product);
            } else {
                // Deselect the item if it is selected
                if (selectedProducts.contains(product)) {
                    toggleSelection(product);
                }
                // Perform your desired action on a normal click here
                // For example, open a detail activity or perform some other action
            }
        });

        holder.productName.setText(product.getTitle());
        if (product.getPriceString() != null) {

            String priceWithSymbol = context.getString(R.string.rupee_symbol, product.getPriceString());
            holder.productPrice.setText(priceWithSymbol);
        } else {
            double conversionRate = 75.0;
            double priceInRupees = product.getPrice() * conversionRate;

            // Format the price with the currency symbol
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", priceInRupees);
            String priceWithSymbol = context.getString(R.string.rupee_symbol, formattedPrice);

            holder.productPrice.setText(priceWithSymbol);
        }



        Picasso.get()
                .load(product.getImage())
                .resize(300, 300) // Set the desired width and height in pixels
                .centerInside()
                .into(holder.productImage);

        // Update the UI based on selection mode
        if (isSelectionMode) {
            if (selectedProducts.contains(product)) {
                holder.itemView.setBackgroundResource(R.drawable.selected_background);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.input_design2);
            }
        } else {
            holder.itemView.setBackgroundResource(R.drawable.input_design2);
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
