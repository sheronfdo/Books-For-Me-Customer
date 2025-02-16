package com.jamith.booksformecustomer.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.activity.CheckoutActivity;
import com.jamith.booksformecustomer.activity.HomeActivity;
import com.jamith.booksformecustomer.adapter.CartAdapter;
import com.jamith.booksformecustomer.model.CartItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RecyclerView cartRecyclerView;
    private List<CartItem> cartItems;
    private List<CartItem> selectedItems;
    TextView cartSubTotal, cartShipping, cartTotal;
    CartAdapter cartAdapter;
    Button checkoutButton;
    double total;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartItems = new ArrayList<>();
        selectedItems = new ArrayList<>();
        cartRecyclerView = view.findViewById(R.id.cart_items_recycler_view);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        cartAdapter = new CartAdapter(cartItems, this::onItemChecked);
        cartRecyclerView.setAdapter(cartAdapter);
        cartSubTotal = view.findViewById(R.id.cartSubTotal);
        cartShipping = view.findViewById(R.id.cartShipping);
        cartTotal = view.findViewById(R.id.cartTotal);
        fetchCartItems();
        checkoutButton = view.findViewById(R.id.cartCheckoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), CheckoutActivity.class);
                intent.putExtra("selectedCartItems", (Serializable) selectedItems);
                startActivity(intent);
            }
        });
        return view;
    }

    private void onItemChecked(CartItem item, boolean isChecked) {
        if (isChecked) {
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }
        total = 0;
        selectedItems.stream().forEach(e->{total = total+ (e.getQuantity() * e.getPrice());});
        cartSubTotal.setText(Double.toString(total));
        cartTotal.setText(Double.toString(total));
    }

    private void fetchCartItems() {
        String userId = firebaseAuth.getUid();
        db.collection("customers").document(userId).collection("cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CartItem bookItem = document.toObject(CartItem.class);
                            bookItem.setCartItemId(document.getId());
                            cartItems.add(bookItem);
                        }
                        setupCartAdapter();
                    } else {
                        // Handle error
                    }
                });
    }

    private void setupCartAdapter() {
        cartAdapter.notifyDataSetChanged();
    }
}