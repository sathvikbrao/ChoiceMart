package com.example.choicemart1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminAddProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView productImage;
    private EditText productName, productPrice, productDescription, productCategory;
    private Button addProductButton;

    private DatabaseReference productsRef;
    private StorageReference productImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        // Initialize the Firebase database and storage references
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productImageRef = FirebaseStorage.getInstance().getReference().child("ProductImages");

        productImage = findViewById(R.id.admin_product_image);
        productName = findViewById(R.id.admin_product_name);
        productPrice = findViewById(R.id.admin_product_price);
        productDescription = findViewById(R.id.admin_product_description);
        productCategory = findViewById(R.id.admin_product_category);
        addProductButton = findViewById(R.id.addAdminProductBtn);

        productImage.setOnClickListener(view -> openImageGallery());

        addProductButton.setOnClickListener(view -> {
            if (imageUri != null) {
                uploadImageAndProduct();
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadImageAndProduct() {
        String name = productName.getText().toString();
        String price = productPrice.getText().toString();
        String description = productDescription.getText().toString();
        String category = productCategory.getText().toString();

        // Generate a unique key for the product
        DatabaseReference categoryRef = productsRef.child(category);
        DatabaseReference productRef = categoryRef.push();
        String productKey = productRef.getKey();

        // Upload the image to Firebase Storage
        StorageReference imageRef = productImageRef.child(productKey + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image upload successful, get the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save the image URL in the product details
                    String imageUrl = uri.toString();
                    productRef.child("ProductImage").setValue(imageUrl);

                    // Save other product details
                    productRef.child("ProductName").setValue(name);
                    productRef.child("ProductPrice").setValue(price);
                    productRef.child("ProductDescription").setValue(description);
                    productRef.child("ProductCategory").setValue(category);

                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after adding the product
                }).addOnFailureListener(e -> {
                    // Handle failure to get the image download URL
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // Handle failure to upload the image
                Toast.makeText(this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }
}
