package com.example.choicemart1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.example.choicemart1.User;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private Button signupButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // Set click listener for signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        // Get user input values
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input values
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isDigitsOnly(phone)) {
            Toast.makeText(this, "Phone number should only contain numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user already exists
        databaseReference.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SignupActivity.this, "User already exists. Please log in.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create user object
                    User user = new User(name, phone, password);


                    // Save user to Firebase database
                    databaseReference.child(phone).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(SignupActivity.this,HomePage.class);
                                       // intent.putExtra("username", name);//--
                                        UserData.getInstance().setUserName(name);
                                        intent.putExtra("userId", phone);
                                        startActivity(intent);
                                        // Perform any additional actions after successful registration
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SignupActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
}