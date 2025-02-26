package com.jamith.booksformecustomer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.model.BookItem;
import com.jamith.booksformecustomer.model.CartItem;
import com.jamith.booksformecustomer.util.DateUtil;

import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ModelMapper modelMapper = new ModelMapper();
    ImageButton backButton;
    Button buyNowButton;
    Button addToCartButton;

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
        addToCartButton = findViewById(R.id.add_to_cart_button);
        buyNowButton = findViewById(R.id.buy_now_button);
        TextView sellerName = findViewById(R.id.seller_name);
        ImageView sellerImage = findViewById(R.id.seller_image);
        ImageView contactDetails = findViewById(R.id.contact_details);

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
                            String sellerNameText = document.getString("fullNameOrRepresentative");
                            String sellerImageUrl = document.getString("imageUrl");
                            String contactNumber = document.getString("phoneNumber");

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

        addToCartButton.setOnClickListener(v -> {
            addToCart(bookItem);
        });

        buyNowButton.setOnClickListener(v -> {
            CartItem cartItem = modelMapper.map(bookItem, CartItem.class);
            cartItem.setImageUrl(bookItem.getImageUrl());
            cartItem.setQuantity(1);
            List<CartItem> selectedItems = new ArrayList<>();
            selectedItems.add(cartItem);
            Intent intent = new Intent(BookDetailsActivity.this, CheckoutActivity.class);
            intent.putExtra("selectedCartItems", (Serializable) selectedItems);
            startActivity(intent);
        });
        backButton = findViewById(R.id.activity_book_details_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
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
        cartItem.put("createdAt", DateUtil.fromFirestoreTimestamp());

        db.collection("customers").document(userId).collection("cart")
                .document()
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