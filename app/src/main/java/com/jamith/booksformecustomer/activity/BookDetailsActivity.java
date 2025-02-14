package com.jamith.booksformecustomer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.model.BookItem;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class BookDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BookItem bookItem = (BookItem) getIntent().getSerializableExtra("selected_book");

        // Find views by ID
        ImageView coverImage = findViewById(R.id.book_cover_image);
        TextView title = findViewById(R.id.book_title);
        TextView author = findViewById(R.id.book_author);
        TextView price = findViewById(R.id.book_price);
        TextView condition = findViewById(R.id.book_condition);
        TextView description = findViewById(R.id.book_description);
        TextView category = findViewById(R.id.book_category);
        TextView publicationYear = findViewById(R.id.book_publication_year);
        TextView language = findViewById(R.id.book_language);
        TextView tags = findViewById(R.id.book_tags);
        TextView featured = findViewById(R.id.book_featured);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        Button buyNowButton = findViewById(R.id.buy_now_button);
        TextView sellerName = findViewById(R.id.seller_name);
        ImageView sellerImage = findViewById(R.id.seller_image);
        ImageView contactDetails = findViewById(R.id.contact_details);

        // Set data to views
        Glide.with(this).load(bookItem.getImageUrl()).into(coverImage);
        title.setText(bookItem.getTitle());
        author.setText(bookItem.getAuthor());
        price.setText("LKR " + bookItem.getPrice());
        condition.setText(bookItem.getCondition());
        description.setText(bookItem.getDescription());
        category.setText(bookItem.getCategory());
        publicationYear.setText(String.valueOf(bookItem.getPublicationYear()));
        language.setText(bookItem.getLanguage());

        StringBuilder tagsString = new StringBuilder();
        if (bookItem.getTags() != null && !bookItem.getTags().isEmpty()) {
            for (String tag : bookItem.getTags()) {
                tagsString.append(tag).append(", ");
            }
            tagsString.setLength(tagsString.length() - 2); // Remove the last comma and space
        }
        tags.setText(tagsString.toString());

        featured.setText(bookItem.isFeatured() ? "Yes" : "No");
        db.collection("sellers").document(bookItem.getSellerId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String sellerNameText = document.getString("fullNameOrRepresentative"); // Adjust according to your seller model
                            String sellerImageUrl = document.getString("imageUrl"); // Adjust according to your seller model
                            String contactNumber = document.getString("phoneNumber"); // Adjust according to your seller model

                            sellerName.setText(sellerNameText);
                            Glide.with(this).load(sellerImageUrl).into(sellerImage);

                            // Set click listener for contact details
                            contactDetails.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + contactNumber));
                                startActivity(intent);
                            });
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.e("Firestore", "Error getting seller info", task.getException());
                    }
                });

        // Set click listeners
        addToCartButton.setOnClickListener(v -> {
            addToCart(bookItem);
        });

        buyNowButton.setOnClickListener(v -> {

        });
    }

    private void addToCart(BookItem book) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("bookId", book.getBookId());
        cartItem.put("title", book.getTitle());
        cartItem.put("price", book.getPrice());
        cartItem.put("quantity", 1);
        cartItem.put("imageUrl", book.getImageUrl());
        cartItem.put("sellerId", book.getSellerId());
        cartItem.put("bookStockId", book.getBookStockId());
        cartItem.put("createdAt", FieldValue.serverTimestamp());

        db.collection("customers").document(userId).collection("cart")
                .document(book.getBookStockId())
                .set(cartItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BookDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookDetailsActivity.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                    Log.e("AddToCartError", e.getMessage());
                });
    }
}