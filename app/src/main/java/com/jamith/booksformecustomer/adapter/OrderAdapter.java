package com.jamith.booksformecustomer.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.activity.OrderDetailsActivity;
import com.jamith.booksformecustomer.model.OrderItem;
import com.jamith.booksformecustomer.util.OrderStatus;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderItemViewHolder> {

    private List<OrderItem> orderItems;

    public OrderAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }


    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderAdapter.OrderItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        Log.d("order item", orderItem.toString());

        Glide.with(holder.itemView).load(orderItem.getImageUrl()).into(holder.imageView);
        holder.title.setText(orderItem.getTitle());
        holder.price.setText(Double.toString(orderItem.getPrice()));
        holder.quantity.setText(Integer.toString(orderItem.getQuantity()));
        holder.status.setText(OrderStatus.valueOf(orderItem.getStatus()).toString());
        holder.itemView.setOnClickListener(n->{
            Intent intent = new Intent(n.getContext(), OrderDetailsActivity.class);
            intent.putExtra("orderItem", orderItem);
            n.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, price, status, quantity;


        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.orderItemImageView);
            title = itemView.findViewById(R.id.orderItemTitleValue);
            price = itemView.findViewById(R.id.orderItemTotal_priceValue);
            status = itemView.findViewById(R.id.orderItemStatusValue);
            quantity = itemView.findViewById(R.id.orderItemQuantityValue);
        }
    }
}
