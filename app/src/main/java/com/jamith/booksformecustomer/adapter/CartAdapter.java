package com.jamith.booksformecustomer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.model.BookItem;
import com.jamith.booksformecustomer.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private OnItemCheckedListener onItemCheckedListener;

    public CartAdapter(List<CartItem> cartItems, OnItemCheckedListener onItemCheckedListener) {
        this.cartItems = cartItems;
        this.onItemCheckedListener = onItemCheckedListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem bookItem = cartItems.get(position);

        Glide.with(holder.itemView).load(bookItem.getImageUrl()).into(holder.imageView);
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked)->{
            onItemCheckedListener.onItemChecked(bookItem, isChecked);
        });

        holder.title.setText(bookItem.getTitle());
        holder.price.setText("$" + bookItem.getPrice());
        holder.quantity.setText(String.valueOf(bookItem.getQuantity()));

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title, price, quantity;
        ImageView imageView;

        CartViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cartitemcheckbox);
            title = itemView.findViewById(R.id.cartitemtitle);
            price = itemView.findViewById(R.id.cartitemprice);
            quantity = itemView.findViewById(R.id.cartitemquantity);
            imageView = itemView.findViewById(R.id.cartitemimage);
        }
    }

    public interface OnItemCheckedListener {
        void onItemChecked(CartItem item, boolean isChecked);
    }
}