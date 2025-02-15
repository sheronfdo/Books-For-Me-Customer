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
import com.jamith.booksformecustomer.model.CartItem;

import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder> {
    private List<CartItem> cartItems;

    public CheckoutItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CheckoutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
        return new CheckoutItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemViewHolder holder, int position) {
        CartItem bookItem = cartItems.get(position);

        Glide.with(holder.itemView).load(bookItem.getImageUrl()).into(holder.imageView);
        holder.title.setText(bookItem.getTitle());
        holder.price.setText("$" + bookItem.getPrice());
        holder.quantity.setText(String.valueOf(bookItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CheckoutItemViewHolder extends RecyclerView.ViewHolder{
        TextView title, price, quantity;
        ImageView imageView;

        CheckoutItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.checkoutitemtitle);
            price = itemView.findViewById(R.id.checkoutitemprice);
            quantity = itemView.findViewById(R.id.checkoutitemquantity);
            imageView = itemView.findViewById(R.id.checkoutitemimage);
        }
    }
}
