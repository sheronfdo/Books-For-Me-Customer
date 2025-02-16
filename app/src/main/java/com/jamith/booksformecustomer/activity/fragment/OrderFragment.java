package com.jamith.booksformecustomer.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.adapter.OrderAdapter;
import com.jamith.booksformecustomer.model.OrderItem;

import java.util.ArrayList;
import java.util.List;


public class OrderFragment extends Fragment {
    private RecyclerView ordersRecyclerView;
    private List<OrderItem> orders;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ordersRecyclerView = view.findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        orders = new ArrayList<>();
        fetchOrders();
        return view;
    }

    private void fetchOrders() {
        db.collection("customers").document(auth.getUid()).collection("orderItems").
                get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderItem order = document.toObject(OrderItem.class);
                            order.setOrderItemId(document.getId());
                            orders.add(order);
                        }
                        setupOrdersAdapter();
                    } else {

                    }
                });
    }

    private void setupOrdersAdapter() {
        OrderAdapter ordersAdapter = new OrderAdapter(orders);
        ordersRecyclerView.setAdapter(ordersAdapter);
    }


}