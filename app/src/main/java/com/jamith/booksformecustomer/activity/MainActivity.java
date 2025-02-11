package com.jamith.booksformecustomer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamith.booksformecustomer.R;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button buttonGoogle;
    FirebaseFirestore firebaseFirestore;
    GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        buttonGoogle = findViewById(R.id.googleSignInButton);
        firebaseFirestore = FirebaseFirestore.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                firebaseAuthenticate(googleSignInAccount.getIdToken());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void firebaseAuthenticate(String token) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(token, null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        firebaseFirestore.collection("customers").document(user.getUid()).get().addOnSuccessListener(customer -> {
                            if (customer.exists()) {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                String displayName = user.getDisplayName();
                                String email = user.getEmail();
                                Uri photoUrl = user.getPhotoUrl();
                                String number = user.getPhoneNumber();
                                String uid = user.getUid();

                                Intent intent = new Intent(MainActivity.this, ProfileDetailConfirmActivity.class);
                                intent.putExtra("displayName", displayName);
                                intent.putExtra("email", email);
                                if (photoUrl != null) {
                                    intent.putExtra("photoUrl", photoUrl.toString());
                                }
                                if (number != null) {
                                    intent.putExtra("phoneNumber", number.toString());
                                }
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        });
    }
}