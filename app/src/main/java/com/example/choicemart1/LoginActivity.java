package com.example.choicemart1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneEditText;
    private EditText passwordEditText;
    private TextView adminTextView;
    private TextView notAdminTextView;
    private Button loginButton;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize views
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        adminTextView = findViewById(R.id.adminTextView);
        notAdminTextView = findViewById(R.id.notAdminTextView);
        loginButton = findViewById(R.id.loginButton);

        // Set click listener for adminTextView
        adminTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdmin = true;
                adminTextView.setVisibility(View.GONE);
                notAdminTextView.setVisibility(View.VISIBLE);
            }
        });

        // Set click listener for notAdminTextView
        notAdminTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdmin = false;
                notAdminTextView.setVisibility(View.GONE);
                adminTextView.setVisibility(View.VISIBLE);
            }
        });

        // Set click listener for loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String phoneNumber = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if the phone number contains only numbers
        if (!phoneNumber.matches("\\d+")) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform the login process based on admin status
        if (isAdmin) {
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Admin");

            if (phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
            } else {
                adminRef.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String storedPassword = dataSnapshot.child("password").getValue(String.class);

                            if (password.equals(storedPassword)) {
                                Toast.makeText(LoginActivity.this, "Admin login successful", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,AdminHomePage.class);
                                intent.putExtra("phone",phoneNumber);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Error occurred while fetching admin data
                        Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
                    }
                });
            }
        }

        else {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

            if (phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Invalid user credentials", Toast.LENGTH_SHORT).show();
            } else {
                usersRef.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String storedPassword = dataSnapshot.child("password").getValue(String.class);
                            String username = dataSnapshot.child("name").getValue(String.class);
                            System.out.println(username);

                            if (password.equals(storedPassword)) {
                                Toast.makeText(LoginActivity.this, "User login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,   HomePage.class);
                                //intent.putExtra("username", username);//--
                                UserData.getInstance().setUserName(username);
                                intent.putExtra("userId", phoneNumber);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid user credentials", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Error occurred while fetching user data
                        Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
                    }
                });
            }
        }

    }



}