package com.example.choicemart1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.choicemart1.databinding.ActivityHomePageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        // Inflate the navigation header layout
        View headerView = binding.navView.getHeaderView(0);

        // Find the user_title TextView within the header layout
        TextView userTitle = headerView.findViewById(R.id.user_title);
        String username = UserData.getInstance().getUserName();

        if (username != null) {
            userTitle.setText("Welcome, " + username);

        }
        String userId = getIntent().getStringExtra("userId");
        UserData.getInstance().setUserId(userId);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId);
        CircleImageView profileImageView = headerView.findViewById(R.id.imageView);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userImageUri = snapshot.child("imageUri").getValue(String.class);

                    if (userImageUri != null && !userImageUri.isEmpty()) {
                        Glide.with(HomePage.this)
                                .load(userImageUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        setSupportActionBar(binding.appBarHomePage.toolbar);
        binding.appBarHomePage.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_orders,R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    navigateToFragment(R.id.nav_home);
                    return true;
                } else if (id == R.id.nav_settings) {
                    navigateToFragment(R.id.nav_settings);
                    // ...
                    return true;
                } else if (id == R.id.nav_orders) {
                    navigateToFragment(R.id.nav_orders);
                    // ...
                    return true;
                } else if (id == R.id.nav_logout) {
                    Intent intent = new Intent(HomePage.this, SignupLogin.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
                // Handle other menu item selections if needed
                // ...
                return false;
            }
        });

        binding.appBarHomePage.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(HomePage.this, R.id.nav_host_fragment_content_home_page);
                int currentDestinationId = navController.getCurrentDestination().getId();

                if (currentDestinationId == R.id.nav_cart) {
                    // Close the ClothFragment
                    navController.navigateUp();
                } else {
                    // Open the ClothFragment
                    navController.navigate(R.id.nav_cart);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void navigateToFragment(int destinationId) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        navController.navigate(destinationId, null, new NavOptions.Builder().setLaunchSingleTop(true).build());
    }
}