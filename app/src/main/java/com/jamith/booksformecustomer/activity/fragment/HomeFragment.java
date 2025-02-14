package com.jamith.booksformecustomer.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.adapter.BookItemAdapter;
import com.jamith.booksformecustomer.adapter.CarouselAdapter;
import com.jamith.booksformecustomer.adapter.CategoryAdapter;
import com.jamith.booksformecustomer.viewModel.HomeViewModel;


public class HomeFragment extends Fragment {
    HomeViewModel homeViewModel;
    RecyclerView categoriesRecyclerView, featuredBooksRecyclerView, newArrivalRecyclerView,
            category1RecyclerView, category2RecyclerView, category3RecyclerView;
    ViewPager2 carouselView;
    TextView categoryOneTextView, categoryTwoTextView, categoryThreeTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        carouselView = view.findViewById(R.id.carousel_view);
        homeViewModel.getCarouselImages().observe(getViewLifecycleOwner(), images -> {
            CarouselAdapter carouselAdapter = new CarouselAdapter(images);
            carouselView.setAdapter(carouselAdapter);
        });


        featuredBooksRecyclerView = view.findViewById(R.id.featured_books_recycler_view);
        featuredBooksRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeViewModel.getFeaturedBooksLiveData().observe(getViewLifecycleOwner(), bookItems -> {
            BookItemAdapter featuredBooksAdapter = new BookItemAdapter(bookItems);
            featuredBooksRecyclerView.setAdapter(featuredBooksAdapter);
        });


        categoriesRecyclerView = view.findViewById(R.id.categories_scroll_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories1 -> {
            CategoryAdapter categoryAdapter = new CategoryAdapter(categories1);
            categoriesRecyclerView.setAdapter(categoryAdapter);
        });


        newArrivalRecyclerView = view.findViewById(R.id.new_arrivals_recycler_view);
        newArrivalRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeViewModel.getNewArrivalsLiveData().observe(getViewLifecycleOwner(), bookItems -> {
            BookItemAdapter newArrivalsAdapter = new BookItemAdapter(bookItems);
            newArrivalRecyclerView.setAdapter(newArrivalsAdapter);
        });


        category1RecyclerView = view.findViewById(R.id.category_1_recycler_view);
        category1RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        homeViewModel.getCategory1BooksLiveData().observe(getViewLifecycleOwner(), bookItems -> {
            BookItemAdapter category1Adapter = new BookItemAdapter(bookItems);
            category1RecyclerView.setAdapter(category1Adapter);
        });


        category2RecyclerView = view.findViewById(R.id.category_2_recycler_view);
        category2RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        homeViewModel.getCategory2BooksLiveData().observe(getViewLifecycleOwner(), bookItems -> {
            BookItemAdapter category2Adapter = new BookItemAdapter(bookItems);
            category2RecyclerView.setAdapter(category2Adapter);
        });


        category3RecyclerView = view.findViewById(R.id.category_3_recycler_view);
        category3RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeViewModel.getCategory3BooksLiveData().observe(getViewLifecycleOwner(), bookItems -> {
            BookItemAdapter category3Adapter = new BookItemAdapter(bookItems);
            category3RecyclerView.setAdapter(category3Adapter);
        });

        categoryOneTextView = view.findViewById(R.id.category_1_title);
        categoryTwoTextView = view.findViewById(R.id.category_2_title);
        categoryThreeTextView = view.findViewById(R.id.category_3_title);
        homeViewModel.getRandomCategories().observe(getViewLifecycleOwner(), randomcategories -> {
            categoryOneTextView.setText(randomcategories[0].getName());
            categoryTwoTextView.setText(randomcategories[1].getName());
            categoryThreeTextView.setText(randomcategories[2].getName());
        });

        return view;
    }
}