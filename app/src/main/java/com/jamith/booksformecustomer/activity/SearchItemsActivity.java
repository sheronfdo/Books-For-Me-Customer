package com.jamith.booksformecustomer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.adapter.SearchResultsItemAdapter;
import com.jamith.booksformecustomer.model.Book;
import com.jamith.booksformecustomer.model.BookItem;

import java.util.ArrayList;
import java.util.List;

public class SearchItemsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchResultsItemAdapter adapter;
    private String bookImageUrl;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.searchResultsRvStocks);
        progressBar = findViewById(R.id.searchResultsProgressBar);
        String bookId = getIntent().getStringExtra("BOOK_ID");
        setupRecyclerView();
        fetchBookDetails(bookId);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SearchResultsItemAdapter(new ArrayList<>(), bookImageUrl);
        recyclerView.setAdapter(adapter);
    }

    private void fetchBookDetails(String bookId) {
        db.collection("books").document(bookId).get().addOnSuccessListener(documentSnapshot -> {
            Book book = documentSnapshot.toObject(Book.class);
            if (book != null) {
                book.setId(bookId);
                bookImageUrl = book.getCoverImage();
                fetchStocks(book);
            }
        });
    }

    private void fetchStocks(Book book) {
        FirebaseFirestore.getInstance().collection("bookStocks").whereEqualTo("bookId", book.getId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<BookItem> stocks = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                BookItem stock = new BookItem();
                stock.setTitle(book.getTitle());
                stock.setAuthor(book.getAuthor());
                stock.setImageUrl(book.getCoverImage());
                stock.setBookId(book.getId());
                stock.setSellerId(doc.getString("sellerId"));
                stock.setBookStockId(doc.getId());
                stock.setStock(doc.getLong("stock").intValue());
                stock.setPrice(doc.getDouble("price"));
                stock.setCondition(doc.getString("condition"));
                stock.setIsbn(doc.getString("isbn"));
                stock.setPublisher(book.getPublisher());
                stock.setCategory(book.getCategory());
                stock.setDescription(book.getDescription());
                stock.setCoverImage(book.getCoverImage());
                stock.setPublicationYear(book.getPublicationYear());
                stock.setLanguage(book.getLanguage());
                stock.setTags(book.getTags());
                stock.setFeatured(book.isFeatured());


                stock.setCreatedAt(doc.getDate("createIdx"));
                stock.setUpdatedAt(doc.getDate("updatedAt"));

                stocks.add(stock);
            }
            adapter.updateData(stocks, bookImageUrl);
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Log.e("FetchStocks", "Error getting documents: ", e);
            progressBar.setVisibility(View.GONE);
        });
    }
}