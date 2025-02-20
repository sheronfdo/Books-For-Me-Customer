package com.jamith.booksformecustomer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.activity.fragment.CartFragment;
import com.jamith.booksformecustomer.activity.fragment.HomeFragment;
import com.jamith.booksformecustomer.activity.fragment.OrderFragment;
import com.jamith.booksformecustomer.activity.fragment.ProfileInfoFragment;

public class HomeActivity extends AppCompatActivity {
    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView navHeaderImage;
    private TextView navHeaderName;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fragmentContainer = findViewById(R.id.fragmentContainer);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        navHeaderImage = headerView.findViewById(R.id.nav_profile_image);
        navHeaderName = headerView.findViewById(R.id.nav_profile_name);

        loadProfileData();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (itemId == R.id.nav_cart) {
                loadFragment(new CartFragment());
            } else if (itemId == R.id.nav_orders) {
                loadFragment(new OrderFragment());
            } else if (itemId == R.id.nav_profile) {
//                loadFragment(new ProfileFragment());
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
            return true;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                loadFragment(new ProfileInfoFragment());
            } else if (item.getItemId() == R.id.nav_logout) {
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadProfileData() {
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference userRef = firebaseFirestore.collection("customers").document(userId);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {
                        String imageUrl = value.getString("imageUri");
                        Log.d("image url", imageUrl);
                        Glide.with(HomeActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.profile)
                                .into(navHeaderImage);
                        String fullName = value.getString("displayName");
                        navHeaderName.setText(fullName);
                    }
                }
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    public void setFragmentHome(){
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}