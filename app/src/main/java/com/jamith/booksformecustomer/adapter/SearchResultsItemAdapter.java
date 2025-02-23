package com.jamith.booksformecustomer.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.activity.BookDetailsActivity;
import com.jamith.booksformecustomer.model.BookItem;

import java.util.List;

public class SearchResultsItemAdapter extends RecyclerView.Adapter<SearchResultsItemAdapter.StockViewHolder> {
    private List<BookItem> stockList;
    private String bookImageUrl;

    public SearchResultsItemAdapter(List<BookItem> stockList, String bookImageUrl) {
        this.stockList = stockList;
        this.bookImageUrl = bookImageUrl;
    }

    public void updateData(List<BookItem> newStocks, String imageUrl) {
        this.stockList = newStocks;
        this.bookImageUrl = imageUrl;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        BookItem stock = stockList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(bookImageUrl)
                .into(holder.imgBook);
        
        holder.tvPrice.setText(String.format("LKR %.2f", stock.getPrice()));
        holder.tvCondition.setText(stock.getCondition());
        holder.tvStock.setText("Available: " + stock.getStock());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BookDetailsActivity.class);
                intent.putExtra("selected_book", stock);
                v.getContext().startActivity(intent);
            }
        });
    }

    private String abbreviateSellerId(String sellerId) {
        return sellerId.substring(0, 6) + "..." + sellerId.substring(sellerId.length() - 4);
    }

    // ViewHolder class
    static class StockViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView tvPrice, tvCondition, tvStock;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCondition = itemView.findViewById(R.id.tvCondition);
            tvStock = itemView.findViewById(R.id.tvStock);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}