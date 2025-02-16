package com.jamith.booksformecustomer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jamith.booksformecustomer.R;

public class OrderCompleteActivity extends AppCompatActivity {
    private TextView tvOrderId, tvTransactionId;
    private Button btnContinueShopping;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_complete);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvOrderId = findViewById(R.id.tvOrderId);
        tvTransactionId = findViewById(R.id.tvTransactionId);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        String orderId = getIntent().getStringExtra("order_id");
        String transactionId = getIntent().getStringExtra("transaction_id");

        // Display Order ID and Transaction ID
        tvOrderId.setText("Order ID: " + orderId);
        tvTransactionId.setText("Transaction ID: " + transactionId);

        // Continue Shopping Button
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderCompleteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}