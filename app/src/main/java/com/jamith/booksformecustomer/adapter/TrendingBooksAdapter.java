package com.jamith.booksformecustomer.adapter;

import android.content.Context;
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
import com.jamith.booksformecustomer.activity.SearchItemsActivity;
import com.jamith.booksformecustomer.model.Book;

import java.util.List;

public class TrendingBooksAdapter extends RecyclerView.Adapter<TrendingBooksAdapter.ViewHolder> {
    private List<Book> trendingBooks;

    public TrendingBooksAdapter( List<Book> books) {
        this.trendingBooks = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trending_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = trendingBooks.get(position);
        holder.txtBookTitle.setText(book.getTitle());
        holder.txtAuthor.setText(book.getAuthor());

        Glide.with(holder.itemView)
                .load(book.getCoverImage())
                .into(holder.imgBookCover);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SearchItemsActivity.class);
            intent.putExtra("BOOK_ID", book.getId()); // Pass only book ID
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trendingBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView txtBookTitle, txtAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(com.jamith.booksformecustomer.R.id.imgBookCover);
            txtBookTitle = itemView.findViewById(R.id.txtBookTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
        }
    }
}
