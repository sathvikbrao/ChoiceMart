package com.example.choicemart1;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.choicemart1.models.Product;
import com.example.choicemart1.ui.home.HomeFragment;
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
import com.razorpay.AutoReadOtpHelper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private Checkout razorpayCheckout;
    private AutoReadOtpHelper autoReadOtpHelper;
    private String formattedTotalAmount;
    NavController navController;
    public  Product product=new Product();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Checkout.preload(getApplicationContext());
        // Get the selected products from the intent
        List<Product> selectedProducts = (List<Product>) getIntent().getSerializableExtra("selectedProducts");

        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            // Calculate the total price
            TextView productNameTextView =findViewById(R.id.productNameTextView);
            TextView quantityTextView = findViewById(R.id.quantityTextView);
            TextView totalPriceTextView = findViewById(R.id.totalPriceTextView);

            double totalAmount = 0.0;
            for (Product product : selectedProducts) {
                totalAmount += product.getPrice() * product.getQuantity();
                String productName=product.getName();

                if (productName != null) {
                    productNameTextView.append(productName.toString() + "\n\n");
                }

                int productQuantity = product.getQuantity();
                quantityTextView.append(String.valueOf(productQuantity)+"\n\n");
                totalPriceTextView.append(String.valueOf(product.getPrice() * product.getQuantity())+"\n\n");

            }

            // Display the total amount

            TextView grandTotalTextView = findViewById(R.id.grandTotalTextView);
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
             formattedTotalAmount = decimalFormat.format(totalAmount);
            String grandTotalText = getString(R.string.rupee_symbol, formattedTotalAmount);
            grandTotalTextView.setText(grandTotalText);

        }
        Button proceedToCheckoutButton = findViewById(R.id.proceedToCheckoutButton);
        proceedToCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phoneNumberEditText = findViewById(R.id.shippment_phone_number);
                String phoneNumber = phoneNumberEditText.getText().toString();
                System.out.println(phoneNumber);

                // Retrieve the address from the EditText field
                EditText addressEditText = findViewById(R.id.addressEditText);
                String address = addressEditText.getText().toString();
                if (phoneNumber.isEmpty() || address.isEmpty()) {
                    Toast.makeText(PaymentActivity.this, "Please enter the required information", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    startPayment(formattedTotalAmount);
                }

            }
        });

    }
    public void startPayment(String grandTotal) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("");
        checkout.setImage(R.drawable.applogo);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();

            options.put("name", " "); //include your project name here
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
           // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.

            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(Double.parseDouble(grandTotal) * 100));
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact"," "); //include your phone number here
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        // Retrieve the phone number from the EditText field
        EditText phoneNumberEditText = findViewById(R.id.shippment_phone_number);
        String phoneNumber = phoneNumberEditText.getText().toString();
        System.out.println(phoneNumber);

        // Retrieve the address from the EditText field
        EditText addressEditText = findViewById(R.id.addressEditText);
        String address = addressEditText.getText().toString();

        // Store the data in the database under the "Orders" node
        String orderId = generateOrderId(); // Implement a method to generate a random order ID
        TextView productNameTextView =findViewById(R.id.productNameTextView);
        String productName = productNameTextView.getText().toString(); // Implement a method to get the product names from productNameTextView

        TextView grandTotalTextView = findViewById(R.id.grandTotalTextView);
        String grandTotal = grandTotalTextView.getText().toString(); // Implement a method to get the grand total from grandTotalTextView

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        String phone = UserData.getInstance().getUserId();

        Query query = ordersRef.child(phone).orderByKey().limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference userOrderRef;

                if (dataSnapshot.exists()) {
                    // User exists, get the existing node reference
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    String orderId = generateOrderId();
                    userOrderRef = ordersRef.child(phone).child(orderId);
                } else {
                    // User doesn't exist, create a new user ID and push data under that node

                    String orderId = generateOrderId();
                    userOrderRef = ordersRef.child(phone).child(orderId);
                }

                pushOrderData(userOrderRef, orderId, productName, grandTotal, phoneNumber, address);
            }

            private void pushOrderData(DatabaseReference userOrderRef, String orderId, String productName, String grandTotal, String phoneNumber, String address) {

                userOrderRef.child("productName").setValue(productName);
                userOrderRef.child("grandTotalPrice").setValue(grandTotal);
                userOrderRef.child("orderPhoneNo").setValue(phoneNumber);
                userOrderRef.child("shipmentAddress").setValue(address);
                String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                userOrderRef.child("orderDate").setValue(currentDate);

                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UserData.getInstance().getUserId()).child("Cart");

                String[] productsArray = productName.split("\n\n");

                for (String product : productsArray) {
                    product = product.trim();
                    Query productQuery = cartRef.orderByChild("ProductName").equalTo(product);
                    System.out.println(product);
                    removeProductFromCart(productQuery);
                }
            }

            private void removeProductFromCart(Query productQuery) {
                productQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                            // Get the key and delete the product node
                            String productKey = productSnapshot.getKey();
                            productSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Error occurred while querying the product
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any error during database read
            }
        });



        // Send an SMS message
        String message = "Payment successful,your payment id is "+s;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Payment Successful. SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }


      finish();

    }

    private String generateOrderId() {
        String orderPrefix = "Ord";
        int randomNumber = (int) (Math.random() * 1000000); // Generate a random number between 0 and 999999
        return orderPrefix + randomNumber;
    }
    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed and cause is "+response, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
