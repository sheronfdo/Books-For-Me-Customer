package com.jamith.booksformecustomer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.activity.fragment.FilterBottomSheetDialogFragment;
import com.jamith.booksformecustomer.adapter.SearchResultAdapter;
import com.jamith.booksformecustomer.adapter.SearchSuggestionAdapter;
import com.jamith.booksformecustomer.model.Book;
import com.jamith.booksformecustomer.viewModel.CategoriesViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchResultsActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView suggestionsRecycler, resultsRecycler;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private ImageButton filterButton;
    private SearchSuggestionAdapter suggestionAdapter;
    private SearchResultAdapter resultAdapter;
    private Intent intent;
    private CategoriesViewModel categoriesViewModel;
    private Set<String> selectedCategoryIds = new HashSet<>();
    private List<Book> allBooks = new ArrayList<>();
    private FilterBottomSheetDialogFragment bottomSheet;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        categoriesViewModel.fetchCategories();
        intent = getIntent();

        searchEditText = findViewById(R.id.search_results_edit_text);
        suggestionsRecycler = findViewById(R.id.suggestions_recycler);
        resultsRecycler = findViewById(R.id.search_results_recycler);
        progressBar = findViewById(R.id.search_results_progress_bar);
        emptyStateText = findViewById(R.id.empty_state_text);
        filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> showFilterBottomSheet());

        suggestionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        resultsRecycler.setLayoutManager(new LinearLayoutManager(this));

        suggestionAdapter = new SearchSuggestionAdapter(new SearchSuggestionAdapter.OnSuggestionClickListener() {
            @Override
            public void onClick(String suggestion) {
                Log.d("suggest clicked", "clicked");
                onSuggestionSelected(suggestion);
            }
        });
        resultAdapter = new SearchResultAdapter();

        suggestionsRecycler.setAdapter(suggestionAdapter);
        suggestionsRecycler.bringToFront();
        suggestionsRecycler.setElevation(10f);
        resultsRecycler.setAdapter(resultAdapter);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    fetchSuggestions(s.toString());
                } else if (s.length() == 0) {
                    performSearch();
                } else {
                    suggestionsRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Handle search button
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        if (intent.getBooleanExtra("isCategory", false)) {
            performSearch();
            String categoryId = intent.getStringExtra("categoryId");
            selectedCategoryIds.add(categoryId);
        } else {
            searchEditText.setText(intent.getStringExtra("query"));
            performSearch();
        }

        backButton = findViewById(R.id.activity_search_results_back_button);
        backButton.setOnClickListener(view->finish());
    }

    private void showFilterBottomSheet() {
        bottomSheet = new FilterBottomSheetDialogFragment();
        categoriesViewModel.getCategoriesLiveData().observe(this, categories -> {
            bottomSheet.setCategories(categories);
            bottomSheet.setFilterListener(new FilterBottomSheetDialogFragment.FilterListener() {
                @Override
                public void onFilterApplied(Set<String> selectedCategories) {
                    Log.d("filtered", "");
                    selectedCategoryIds = selectedCategories;
                    applyFilters();
                }
            });
        });
        bottomSheet.setSelectedCategories(selectedCategoryIds);
        bottomSheet.show(getSupportFragmentManager(), "FilterBottomSheet");
    }

    private void applyFilters() {
        if (selectedCategoryIds.isEmpty()) {
            resultAdapter.updateResults(allBooks);
        } else {
            List<Book> filteredBooks = new ArrayList<>();
            for (Book book : allBooks) {
                if (selectedCategoryIds.contains(book.getCategory())) {
                    filteredBooks.add(book);
                }
            }
            resultAdapter.updateResults(filteredBooks);
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        emptyStateText.setVisibility(resultAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void fetchSuggestions(String query) {
        FirebaseFirestore.getInstance().collection("books")
                .orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    List<String> suggestions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        suggestions.add(doc.getString("title"));
                    }
                    if (!suggestions.isEmpty()) {
                        suggestionsRecycler.setVisibility(View.VISIBLE);
                        suggestionAdapter.updateSuggestions(suggestions);
                    } else {
                        suggestionsRecycler.setVisibility(View.GONE);
                    }
                });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
        resultsRecycler.setVisibility(View.GONE);

        FirebaseFirestore.getInstance().collection("books")
                .orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(100)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    suggestionsRecycler.setVisibility(View.GONE);
                    allBooks = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Book book = doc.toObject(Book.class);
                        book.setId(doc.getId());
                        allBooks.add(book);
                    }
                    if (!allBooks.isEmpty()) {
                        resultsRecycler.setVisibility(View.VISIBLE);
                        resultAdapter.updateResults(allBooks);
                    } else {
                        updateEmptyState();
                    }
                    applyFilters();
                });
    }

    private void onSuggestionSelected(String suggestion) {
        Log.d("search suggestions", "search performs");
        suggestionsRecycler.setVisibility(View.GONE);
        searchEditText.setText(suggestion);
        performSearch();
    }
}