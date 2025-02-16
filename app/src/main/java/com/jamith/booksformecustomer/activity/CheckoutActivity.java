package com.jamith.booksformecustomer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.adapter.CheckoutItemAdapter;
import com.jamith.booksformecustomer.dto.requestDTO.OrderDTO;
import com.jamith.booksformecustomer.dto.requestDTO.PaymentStatusDTO;
import com.jamith.booksformecustomer.dto.responseDTO.OrderResponseDTO;
import com.jamith.booksformecustomer.model.CartItem;
import com.jamith.booksformecustomer.service.OrderService;
import com.jamith.booksformecustomer.util.PaymentStatus;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CheckoutItemAdapter checkoutItemAdapter;
    List<CartItem> cartItems;
    private TextView orderTotal;
    private EditText nameInput, addressInput, phoneInput, emailInput;
    private Button placeOrderButton;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private double totalAmount = 0.0;

    OrderResponseDTO orderResponseDTO;

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
        orderTotal = findViewById(R.id.order_total);
        nameInput = findViewById(R.id.name_input);
        addressInput = findViewById(R.id.address_input);
        phoneInput = findViewById(R.id.phone_input);
        emailInput = findViewById(R.id.email_input);
        placeOrderButton = findViewById(R.id.place_order_button);
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedCartItems");
        recyclerView = findViewById(R.id.checkout_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this, LinearLayoutManager.VERTICAL, false));
        checkoutItemAdapter = new CheckoutItemAdapter(cartItems);
        recyclerView.setAdapter(checkoutItemAdapter);
        checkoutItemAdapter.notifyDataSetChanged();
        calculateTotalAmount();
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void calculateTotalAmount() {
        totalAmount = 0.0;
        for (CartItem item : cartItems) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        orderTotal.setText("Total: $" + totalAmount);
    }

    private void placeOrder() {
        String name = nameInput.getText().toString();
        String address = addressInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(CheckoutActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setRecieverName(name);
        orderDTO.setRecieverAddress(address);
        orderDTO.setRecieverPhoneNumber(phone);
        orderDTO.setRecieverEmail(email);
        orderDTO.setCustomerId(firebaseAuth.getCurrentUser().getUid());
        orderDTO.setCartItems(cartItems);
        orderDTO.setItemCount(cartItems.size());
        orderDTO.setTotalPrice(totalAmount);

        Log.d("Order Object", orderDTO.toString());
        OrderService orderService = new OrderService();
        orderService.makeOrder(orderDTO, new OrderService.OrderServiceCallback() {
            @Override
            public void onSuccess(OrderResponseDTO response) {
                Log.d("order success", response.toString());
                orderResponseDTO = response;
                paymentStatus();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("order failed", errorMessage.toString());
            }

            @Override
            public void onFailure(String failureMessage) {
                Log.e("order failed", failureMessage.toString());
            }
        });
    }

    private void paymentStatus(){
        OrderService orderService = new OrderService();
        PaymentStatusDTO paymentStatusDTO = new PaymentStatusDTO();
        paymentStatusDTO.setOrderId(orderResponseDTO.getId());
        paymentStatusDTO.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_COMPLETED);
        orderService.paymentStatus(paymentStatusDTO, new OrderService.OrderServiceCallback() {
            @Override
            public void onSuccess(OrderResponseDTO response) {
                Log.d("order success", response.toString());
                orderResponseDTO = response;
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("order failed", errorMessage.toString());
            }

            @Override
            public void onFailure(String failureMessage) {
                Log.e("order failed", failureMessage.toString());
            }
        });
    }
}