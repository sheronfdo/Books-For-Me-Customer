package com.jamith.booksformecustomer.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.jamith.booksformecustomer.dto.requestDTO.PaymentDetailsDTO;
import com.jamith.booksformecustomer.dto.requestDTO.PaymentStatusDTO;
import com.jamith.booksformecustomer.dto.responseDTO.OrderResponseDTO;
import com.jamith.booksformecustomer.model.CartItem;
import com.jamith.booksformecustomer.service.OrderService;
import com.jamith.booksformecustomer.util.PaymentStatus;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 150;
    RecyclerView recyclerView;
    CheckoutItemAdapter checkoutItemAdapter;
    List<CartItem> cartItems;
    private TextView orderTotal;
    private EditText nameInput, addressInput, phoneInput, emailInput;
    private Button placeOrderButton;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private double totalAmount = 0.0;

    OrderResponseDTO orderResponseDTO;
    PayPalConfiguration config;

    String clientId;

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
        clientId = getString(R.string.paypal_client_id);
        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                .clientId(clientId);

        Log.d("pal client", clientId);
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
                handlePayment();
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

    private void handlePayment() {
        PayPalPayment payment = new PayPalPayment(
                new BigDecimal(totalAmount),
                "USD",
                "Order Payment",
                PayPalPayment.PAYMENT_INTENT_SALE
        );

        Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetailsFromPaypal = confirm.toJSONObject().toString(4);
                        Log.d("payment details", paymentDetailsFromPaypal);
                        JSONObject paymentDetails = new JSONObject(paymentDetailsFromPaypal);

                        // Extracting Response
                        JSONObject response = paymentDetails.getJSONObject("response");

                        // Get Payment Details
                        String paymentId = response.getString("id");
                        String paymentState = response.getString("state");
                        String paymentCreateTime = response.getString("create_time");
                        String paymentIntent = response.getString("intent");

                        PaymentDetailsDTO paymentDetailsDTO = new PaymentDetailsDTO();
                        paymentDetailsDTO.setPaymentId(paymentId);
                        paymentDetailsDTO.setPaymentStatus(paymentState);
                        paymentDetailsDTO.setCreatedTime(paymentCreateTime);
                        paymentDetailsDTO.setIntent(paymentIntent);

                        paymentStatus(paymentDetailsDTO);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(CheckoutActivity.this, "Payment Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void paymentStatus(PaymentDetailsDTO paymentDetailsDTO) {
        OrderService orderService = new OrderService();
        PaymentStatusDTO paymentStatusDTO = new PaymentStatusDTO();
        paymentStatusDTO.setOrderId(orderResponseDTO.getId());
        paymentStatusDTO.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_COMPLETED);
        paymentStatusDTO.setPaymentDetailsDTO(paymentDetailsDTO);

        orderService.paymentStatus(paymentStatusDTO, new OrderService.OrderServiceCallback() {
            @Override
            public void onSuccess(OrderResponseDTO response) {
                Log.d("order success", response.toString());
                orderResponseDTO = response;
                String transactionId = paymentDetailsDTO.getPaymentId();
                String orderId = orderResponseDTO.getId();

                // Navigate to OrderCompletionActivity
                Intent intent = new Intent(CheckoutActivity.this, OrderCompleteActivity.class);
                intent.putExtra("order_id", orderId);
                intent.putExtra("transaction_id", transactionId);
                startActivity(intent);
                finish();
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