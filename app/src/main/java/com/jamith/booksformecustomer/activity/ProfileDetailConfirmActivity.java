package com.jamith.booksformecustomer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.dto.requestDTO.CustomerSignUpDTO;
import com.jamith.booksformecustomer.dto.responseDTO.CustomerSignUpResponseDTO;
import com.jamith.booksformecustomer.service.FirebaseStorageService;
import com.jamith.booksformecustomer.service.SignUpService;
import com.jamith.booksformecustomer.util.StorageFolders;

public class ProfileDetailConfirmActivity extends AppCompatActivity {

    private EditText displayNameEditText, firstNameEditText, lastNameEditText, phoneNumberEditText;
    private ImageButton profileImageView;
    private Button confirmButton;
    private Uri currentImageUri;
    private String imageUri;
    private boolean googleImage = true;
    private final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_detail_confirm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        displayNameEditText = findViewById(R.id.display_name_edit_text);
        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        profileImageView = findViewById(R.id.profile_image);
        confirmButton = findViewById(R.id.confirm_button);

        Intent intent = getIntent();
        String displayName = intent.getStringExtra("displayName");
        String email = intent.getStringExtra("email");
        String photoUrl = intent.getStringExtra("photoUrl");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String uid = intent.getStringExtra("uid");

        if (displayName != null) {
            displayNameEditText.setText(displayName);
        }
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(profileImageView);
            currentImageUri = Uri.parse(photoUrl);
        }
        if (phoneNumber != null) {
            phoneNumberEditText.setText(phoneNumber);
        }
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(uid, email);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        profileImageView.setOnClickListener(button -> openImagePicker());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).into(profileImageView);
            currentImageUri = selectedImageUri;
            googleImage =false;
        }
    }


    private void saveData(String uid, String email) {
        String displayName = displayNameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        CustomerSignUpDTO customerSignUpDTO = CustomerSignUpDTO.builder().uid(uid).email(email).displayName(displayName).firstName(firstName).lastName(lastName).phoneNumber(phoneNumber).build();

        if (currentImageUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        } else if(!googleImage) {
            new FirebaseStorageService().uploadFile(currentImageUri, StorageFolders.IMAGES, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    imageUri = o.toString();
                    Log.d("image upload success", imageUri);
                    customerSignUpDTO.setImageUri(imageUri);
                    saveData(customerSignUpDTO);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileDetailConfirmActivity.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageUri = currentImageUri.toString();
            customerSignUpDTO.setImageUri(imageUri);
            saveData(customerSignUpDTO);
        }

    }

    private void saveData(CustomerSignUpDTO customerSignUpDTO) {
        SignUpService signUpService = new SignUpService();
        signUpService.customerSignUp(customerSignUpDTO, new SignUpService.SignUpServiceCallback() {
            @Override
            public void onSuccess(CustomerSignUpResponseDTO response) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileDetailConfirmActivity.this, "Seller registered successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ProfileDetailConfirmActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileDetailConfirmActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String failureMessage) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileDetailConfirmActivity.this, "Failure: " + failureMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }


}