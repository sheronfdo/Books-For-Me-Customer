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

import com.airbnb.lottie.LottieAnimationView;
import com.jamith.booksformecustomer.R;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class OrderCompleteActivity extends AppCompatActivity {
    private TextView tvOrderId, tvTransactionId;
    private Button btnContinueShopping;
    String orderId;
    String transactionId;
    LottieAnimationView lottieAnimationView;
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
        lottieAnimationView = findViewById(R.id.orderStatusAnimation);

        if(getIntent().getBooleanExtra("is_success", false)){
            orderId = getIntent().getStringExtra("order_id");
            transactionId = getIntent().getStringExtra("transaction_id");
            tvOrderId.setText("Order ID: " + orderId);
            tvTransactionId.setText("Transaction ID: " + transactionId);
            String json = loadJsonFromRaw(R.raw.order_complete);
            lottieAnimationView.setAnimationFromJson(json, "order_complete");
        } else {
            String json = loadJsonFromRaw(R.raw.order_failed);
            lottieAnimationView.setAnimationFromJson(json, "order_failed");
        }

        lottieAnimationView.playAnimation();
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderCompleteActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private String loadJsonFromRaw(int rawResourceId) {
        InputStream inputStream = getResources().openRawResource(rawResourceId);
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}