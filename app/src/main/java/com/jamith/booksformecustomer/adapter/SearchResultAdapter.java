package com.jamith.booksformecustomer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.model.Book;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultsViewHolder> {
    private List<Book> results = new ArrayList<>();

    public void updateResults(List<Book> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new SearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsViewHolder holder, int position) {
        Book book = results.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());



        // Load image using Glide/Picasso
        Glide.with(holder.itemView.getContext())
                .load(book.getCoverImage())
                .into(holder.bookImage);

        holder.viewDetailsButton.setOnClickListener(v -> {
            // Handle navigation to book details
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class SearchResultsViewHolder extends RecyclerView.ViewHolder {
        TextView title, author;
        ImageView bookImage;
        Button viewDetailsButton;

        SearchResultsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.book_title);
            author = itemView.findViewById(R.id.book_author);
            bookImage = itemView.findViewById(R.id.book_image);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }
    }
}
