package com.jamith.booksformecustomer.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView navHeaderImage;
    private TextView navHeaderName;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String THEME_MODE_KEY = "theme_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        applyTheme();
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
            } else if (item.getItemId() == R.id.nav_theme) {
                showThemeSelectionDialog();
            } else if (item.getItemId() == R.id.nav_logout) {
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private final long IMAGE_EXPIRY_DURATION = 24 * 60 * 60 * 1000;

    private void loadProfileData() {
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference userRef = firebaseFirestore.collection("customers").document(userId);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {
                        String imageUrl = value.getString("imageUri");
                        String fullName = value.getString("displayName");
                        navHeaderName.setText(fullName);

                        File profileImageFile = new File(getFilesDir(), "profile.jpg");
                        SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                        long lastUpdatedTime = prefs.getLong("profile_image_timestamp", 0);
                        long currentTime = System.currentTimeMillis();

                        if (profileImageFile.exists() && (currentTime - lastUpdatedTime) < IMAGE_EXPIRY_DURATION) {
                            Log.d("using_cache", "from storage");
                            Bitmap bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
                            navHeaderImage.setImageBitmap(bitmap);
                        } else {
                            Log.d("using_live", "from firebase");
                            downloadAndCacheImage(imageUrl, profileImageFile);
                        }
                    }
                }
            });
        }
    }

    private void downloadAndCacheImage(String imageUrl, File file) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.profile)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        navHeaderImage.setImageBitmap(resource);
                        saveImageToInternalStorage(resource, file);
                        SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                        prefs.edit().putLong("profile_image_timestamp", System.currentTimeMillis()).apply();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void saveImageToInternalStorage(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showThemeSelectionDialog() {
        String[] themes = {"System Default", "Light Mode", "Dark Mode"};
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentMode = prefs.getInt(THEME_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        int checkedItem = (currentMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) ? 0 :
                (currentMode == AppCompatDelegate.MODE_NIGHT_NO) ? 1 : 2;

        new AlertDialog.Builder(this)
                .setTitle("Choose Theme")
                .setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
                    int selectedMode;
                    if (which == 0) {
                        selectedMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    } else if (which == 1) {
                        selectedMode = AppCompatDelegate.MODE_NIGHT_NO;
                    } else {
                        selectedMode = AppCompatDelegate.MODE_NIGHT_YES;
                    }
                    prefs.edit().putInt(THEME_MODE_KEY, selectedMode).apply();
                    AppCompatDelegate.setDefaultNightMode(selectedMode);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int themeMode = prefs.getInt(THEME_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    public void setFragmentHome() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}