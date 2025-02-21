package com.jamith.booksformecustomer.activity.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.activity.HomeActivity;
import com.jamith.booksformecustomer.dto.requestDTO.CustomerUpdateDTO;
import com.jamith.booksformecustomer.dto.responseDTO.CustomerSignUpResponseDTO;
import com.jamith.booksformecustomer.model.Profile;
import com.jamith.booksformecustomer.service.FirebaseStorageService;
import com.jamith.booksformecustomer.service.SignUpService;
import com.jamith.booksformecustomer.util.StorageFolders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ProfileInfoFragment extends Fragment {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ImageView profileImage;
    private Button imageButton, submitButton;
    private ProgressBar progressBar;
    private String imageUrl;
    private EditText firstName, lastName, displayName, email, phoneNumber;
    private int PERMISSION_REQUEST_CODE = 100;
    private int PICK_IMAGE_REQUEST = 101;
    private int CAMERA_REQUEST_CODE = 102;
    private Uri selectedFileUri;
    private HomeActivity homeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);
        profileImage = view.findViewById(R.id.profile_info_image);
        imageButton = view.findViewById(R.id.profile_info_btn_change_image);
        firstName = view.findViewById(R.id.profile_info_et_first_name);
        lastName = view.findViewById(R.id.profile_info_et_last_name);
        displayName = view.findViewById(R.id.profile_info_et_display_name);
        email = view.findViewById(R.id.profile_info_et_email);
        phoneNumber = view.findViewById(R.id.profile_info_et_phone_number);
        progressBar = view.findViewById(R.id.profileInfoProgressBar);
        homeActivity  = (HomeActivity) getActivity();
        loadProfileData();
        checkPermissions();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePickerDialog();
            }
        });
        submitButton = view.findViewById(R.id.profile_info_btn_save_changes);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });
        return view;
    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(getContext(), "Permissions Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permissions Denied! Camera will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFilePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openGallery();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), "No camera available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedFileUri = data.getData();
                Toast.makeText(getContext(), "Image Selected from Gallery", Toast.LENGTH_SHORT).show();
                profileImage.setImageURI(selectedFileUri);

            } else if (requestCode == CAMERA_REQUEST_CODE) {
                // Camera Image Captured
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                selectedFileUri = getImageUri(imageBitmap);
                profileImage.setImageURI(selectedFileUri);
                Toast.makeText(getContext(), "Image Captured from Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Captured Image", null);
        return Uri.parse(path);
    }

    private void loadProfileData() {
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference userRef = firebaseFirestore.collection("customers").document(userId);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {
                        Profile profile = value.toObject(Profile.class);
                        imageUrl = profile.getImageUri();

                        File profileImageFile = new File(homeActivity.getFilesDir(), "profile.jpg");
                        SharedPreferences prefs = homeActivity.getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                        long lastUpdatedTime = prefs.getLong("profile_image_timestamp", 0);
                        long currentTime = System.currentTimeMillis();

                        if (profileImageFile.exists() && (currentTime - lastUpdatedTime) < IMAGE_EXPIRY_DURATION) {
                            Log.d("info_using_cache", "from storage");
                            Bitmap bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
                            profileImage.setImageBitmap(bitmap);
                        } else {
                            Log.d("info_using_live", "from firebase");
                            downloadAndCacheImage(imageUrl, profileImageFile);
                        }

                        displayName.setText(profile.getDisplayName());
                        phoneNumber.setText(profile.getPhoneNumber());
                        email.setText(profile.getEmail());
                        firstName.setText(profile.getFirstName());
                        lastName.setText(profile.getLastName());
                    }
                }
            });
        }
    }

    private final long IMAGE_EXPIRY_DURATION = 24 * 60 * 60 * 1000;

    private void downloadAndCacheImage(String imageUrl, File file) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.profile)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Display image
                        profileImage.setImageBitmap(resource);
                        // Save image to internal storage
                        saveImageToInternalStorage(resource, file);
                        // Update timestamp
                        SharedPreferences prefs = homeActivity.getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                        prefs.edit().putLong("profile_image_timestamp", System.currentTimeMillis()).apply();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    private void saveImageToInternalStorage(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveProfileChanges() {
        progressBar.setVisibility(View.VISIBLE);
        // Collect updated data
        String firstName = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();
        String displayName = this.displayName.getText().toString();
        String phoneNumber = this.phoneNumber.getText().toString();

        CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO();
        customerUpdateDTO.setDisplayName(displayName);
        customerUpdateDTO.setFirstName(firstName);
        customerUpdateDTO.setLastName(lastName);
        customerUpdateDTO.setPhoneNumber(phoneNumber);
        customerUpdateDTO.setImageUrl(imageUrl);
        customerUpdateDTO.setId(firebaseAuth.getCurrentUser().getUid());


        if (selectedFileUri == null) {
            customerUpdateDTO.setImageUrl(imageUrl);
            saveData(customerUpdateDTO);
        } else {
            new FirebaseStorageService().uploadFile(selectedFileUri, StorageFolders.IMAGES, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    requireActivity().runOnUiThread(() -> {
                        String sellerImageDownloadUrl = o.toString();
                        Log.d("image upload success", o.toString());
                        customerUpdateDTO.setImageUrl(sellerImageDownloadUrl);
                        saveData(customerUpdateDTO);
                        File profileImageFile = new File(homeActivity.getFilesDir(), "profile.jpg");
                        if (profileImageFile.exists()) {
                            if (profileImageFile.delete()) {
                                Log.d("Profile Image", "Cached profile image deleted successfully.");
                            } else {
                                Log.e("Profile Image", "Failed to delete cached profile image.");
                            }
                        }

                        // Also clear the timestamp in SharedPreferences
                        SharedPreferences prefs = homeActivity.getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                        prefs.edit().remove("profile_image_timestamp").apply();
                    });
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            });
        }

    }

    private void saveData(CustomerUpdateDTO customerUpdateDTO) {
        SignUpService signUpService = new SignUpService();
        signUpService.customerUpdate(customerUpdateDTO, new SignUpService.SignUpServiceCallback() {
            @Override
            public void onSuccess(CustomerSignUpResponseDTO response) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Customer Updated Successfully!", Toast.LENGTH_LONG).show();
                    homeActivity.setFragmentHome();
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}