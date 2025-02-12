package com.jamith.booksformecustomer.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.adapter.BookAdapter;
import com.jamith.booksformecustomer.adapter.CarouselAdapter;
import com.jamith.booksformecustomer.adapter.CategoryAdapter;
import com.jamith.booksformecustomer.model.BookItem;
import com.jamith.booksformecustomer.model.Category;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private List<String> carouselImages;
    private List<Category> categories;
    private List<BookItem> featuredBooks, newArrivals, category1Books, category2Books, category3Books;
    RecyclerView categoriesRecyclerView;
    RecyclerView featuredBooksRecyclerView;
    RecyclerView newArrivalRecyclerView;
    RecyclerView category1RecyclerView;
    RecyclerView category2RecyclerView;
    RecyclerView category3RecyclerView;
    ViewPager2 carouselView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        carouselImages = new ArrayList<>();
        categories = new ArrayList<>();
        featuredBooks = new ArrayList<>();
        newArrivals = new ArrayList<>();
        category1Books = new ArrayList<>();
        category2Books = new ArrayList<>();
        category3Books = new ArrayList<>();


        carouselView = view.findViewById(R.id.carousel_view);
        carouselImages.add("https://th.bing.com/th/id/OIP.AjNwNT6sC_I6iDo03b9XrgHaGv?rs=1&pid=ImgDetMain");
        carouselImages.add("https://th.bing.com/th/id/OIP.AjNwNT6sC_I6iDo03b9XrgHaGv?rs=1&pid=ImgDetMain");
        carouselImages.add("https://th.bing.com/th/id/OIP.AjNwNT6sC_I6iDo03b9XrgHaGv?rs=1&pid=ImgDetMain");

        CarouselAdapter carouselAdapter = new CarouselAdapter(carouselImages);
        carouselView.setAdapter(carouselAdapter);
        categoriesRecyclerView = view.findViewById(R.id.categories_scroll_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Fetch categories from Firestore
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            categories.add(category);
                        }
                        CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
                        categoriesRecyclerView.setAdapter(categoryAdapter);
                    } else {
                        // Handle error
                    }
                });

        featuredBooksRecyclerView = view.findViewById(R.id.featured_books_recycler_view);
        featuredBooksRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Fetch featured books from Firestore
        db.collection("books")
                .whereEqualTo("featured", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookItem book = document.toObject(BookItem.class);
                            featuredBooks.add(book);
                        }
                        BookAdapter featuredBooksAdapter = new BookAdapter(featuredBooks);
                        featuredBooksRecyclerView.setAdapter(featuredBooksAdapter);
                    } else {
                        // Handle error
                    }
                });

        newArrivalRecyclerView = view.findViewById(R.id.new_arrivals_recycler_view);
        newArrivalRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        db.collection("books")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookItem book = document.toObject(BookItem.class);
                            newArrivals.add(book);
                        }
                        BookAdapter newArrivalsAdapter = new BookAdapter(newArrivals);
                        newArrivalRecyclerView.setAdapter(newArrivalsAdapter);
                    } else {
                        // Handle error
                    }
                });
        category1RecyclerView = view.findViewById(R.id.category_1_recycler_view);
        category1RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        db.collection("books")
                .whereEqualTo("category", "fiction")
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookItem book = document.toObject(BookItem.class);
                            category1Books.add(book);
                        }
                        BookAdapter category1Adapter = new BookAdapter(category1Books);
                        category1RecyclerView.setAdapter(category1Adapter);
                    } else {
                        // Handle error
                    }
                });
        category2RecyclerView = view.findViewById(R.id.category_2_recycler_view);
        category2RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        db.collection("books")
                .whereEqualTo("category", "biography")
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookItem book = document.toObject(BookItem.class);
                            category2Books.add(book);
                        }
                        BookAdapter category2Adapter = new BookAdapter(category2Books);
                        category2RecyclerView.setAdapter(category2Adapter);
                    } else {
                        // Handle error
                    }
                });
        category3RecyclerView = view.findViewById(R.id.category_3_recycler_view);
        category3RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false)); db.collection("books")
                .whereEqualTo("category", "cookbook")
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookItem book = document.toObject(BookItem.class);
                            category3Books.add(book);
                        }
                        BookAdapter category3Adapter = new BookAdapter(category3Books);
                        category3RecyclerView.setAdapter(category3Adapter);
                    } else {
                        // Handle error
                    }
                });
        return view;
    }


}