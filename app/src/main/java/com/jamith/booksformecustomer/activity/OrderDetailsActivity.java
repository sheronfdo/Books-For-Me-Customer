package com.jamith.booksformecustomer.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.model.Order;
import com.jamith.booksformecustomer.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {
    private LinearLayout order_summary, statusTimeline, order_Item;
    private ProgressBar progressBar;
    private TextView orderConfirmed, paymentPending, processing, shipped, delivered, orderCompleted;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        order_summary = findViewById(R.id.order_summary);
        statusTimeline = findViewById(R.id.status_timeline);
        order_Item = findViewById(R.id.order_details_order_item);

        OrderItem orderItem = (OrderItem) getIntent().getSerializableExtra("orderItem");

        progressBar = findViewById(R.id.progress_bar);
        orderConfirmed = findViewById(R.id.order_confirmed);
        paymentPending = findViewById(R.id.payment_pending);
        processing = findViewById(R.id.processing);
        shipped = findViewById(R.id.shipped);
        delivered = findViewById(R.id.delivered);
        orderCompleted = findViewById(R.id.order_completed);



        db.collection("orders").document(orderItem.getOrderId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Order order = task.getResult().toObject(Order.class);
                    order.setOrderId(orderItem.getOrderId());
                    setupOrderSummary(order);
                    setupOrderItems(orderItem);
                    setupStatusTimeline(order.getStatus());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void setupOrderSummary(Order order) {
        TextView orderId = findViewById(R.id.order_id);
        TextView orderDate = findViewById(R.id.order_date);
        TextView totalPrice = findViewById(R.id.total_price);
        TextView paymentStatus = findViewById(R.id.payment_status);

        orderId.setText("Order ID: " + order.getOrderId());
        orderDate.setText("Order Date: " + order.getOrderDate());
        totalPrice.setText("Total Price: $" + order.getTotalPrice());
        paymentStatus.setText("Payment Status: " + order.getPaymentStatus());
    }

    private void setupOrderItems(OrderItem orderItems) {
        ImageView imageView = findViewById(R.id.image_view);
        TextView title = findViewById(R.id.title);
        TextView quantity = findViewById(R.id.quantity);
        TextView price = findViewById(R.id.price);

        Glide.with(imageView).load(orderItems.getImageUrl()).into(imageView);
        title.setText(orderItems.getTitle());
        quantity.setText(Integer.toString(orderItems.getQuantity()));
        price.setText(Double.toString(orderItems.getPrice()));
    }

    private void setupStatusTimeline(String status) {
        int[] statuses = {R.id.order_confirmed, R.id.payment_pending, R.id.processing, R.id.shipped, R.id.delivered, R.id.order_completed};
        String[] statusNames = {"ORDER_CONFIRMED", "PAYMENT_PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "ORDER_COMPLETED"};
        int progress = 0;

        for (int i = 0; i < statuses.length; i++) {
            TextView textView = findViewById(statuses[i]);
            textView.setTextColor(Color.GRAY);

            if (statusNames[i].equals(status)) {
                textView.setTextColor(Color.GREEN);
                progress = (i + 1) * (100 / statuses.length);
                break;
            }
        }
        progressBar.setProgress(progress);
    }


}