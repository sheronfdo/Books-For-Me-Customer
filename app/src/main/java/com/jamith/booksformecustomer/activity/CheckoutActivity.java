package com.jamith.booksformecustomer.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.adapter.CheckoutItemAdapter;
import com.jamith.booksformecustomer.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CheckoutItemAdapter checkoutItemAdapter;
    List<CartItem> cartItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedCartItems");
        recyclerView = findViewById(R.id.checkout_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this, LinearLayoutManager.VERTICAL, false));
        checkoutItemAdapter = new CheckoutItemAdapter(cartItems);
        recyclerView.setAdapter(checkoutItemAdapter);
        checkoutItemAdapter.notifyDataSetChanged();
    }
}