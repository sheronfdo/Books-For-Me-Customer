package com.jamith.booksformecustomer.viewModel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformecustomer.model.Book;
import com.jamith.booksformecustomer.model.BookItem;
import com.jamith.booksformecustomer.model.BookStock;
import com.jamith.booksformecustomer.model.Category;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<String>> carouselImages = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> featuredBooksLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> newArrivalsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> category1BooksLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> category2BooksLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> category3BooksLiveData = new MutableLiveData<>();
    private MutableLiveData<Category[]> randomCategories = new MutableLiveData<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ModelMapper modelMapper = new ModelMapper();


    HomeViewModel() {
        fetchCategories();
        fetchCarouselImages();

    }


    public LiveData<List<BookItem>> getFeaturedBooksLiveData() {
        return featuredBooksLiveData;
    }

    public LiveData<List<BookItem>> getNewArrivalsLiveData() {
        return newArrivalsLiveData;
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }

    public LiveData<List<BookItem>> getCategory1BooksLiveData() {
        return category1BooksLiveData;
    }

    public LiveData<List<BookItem>> getCategory2BooksLiveData() {
        return category2BooksLiveData;
    }

    public LiveData<List<BookItem>> getCategory3BooksLiveData() {
        return category3BooksLiveData;
    }

    public MutableLiveData<List<String>> getCarouselImages() {
        return carouselImages;
    }

    public MutableLiveData<Category[]> getRandomCategories() {
        return randomCategories;
    }

    private void fetchCarouselImages() {
        List<String> images = new ArrayList<>();
        images.add("https://th.bing.com/th/id/OIP.AjNwNT6sC_I6iDo03b9XrgHaGv?rs=1&pid=ImgDetMain");
        images.add("https://th.bing.com/th/id/OIP.AjNwNT6sC_I6iDo03b9XrgHaGv?rs=1&pid=ImgDetMain");
        images.add("https://th.bing.com/th/id/OIP.AjNwNT6sC_I6iDo03b9XrgHaGv?rs=1&pid=ImgDetMain");
        carouselImages.setValue(images);
    }

    public void fetchAndProcessBookStocks() {
        db.collection("bookStocks").limit(100)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, BookItem> latestBookStocks = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookStock bookStock = document.toObject(BookStock.class);
                            bookStock.setBookStockId(document.getId());

                            String bookId = bookStock.getBookId();
                            if (latestBookStocks.containsKey(bookId)) {
                                BookItem existingStock = latestBookStocks.get(bookId);
                                if (bookStock.getCreatedAt().after(existingStock.getCreatedAt())) {
                                    getBookItem(bookStock, bookItem -> {
                                        latestBookStocks.put(bookId, bookItem);
                                        checkAndUpdateLists(latestBookStocks);

                                    });
                                }
                            } else {
                                getBookItem(bookStock, bookItem -> {
                                    latestBookStocks.put(bookId, bookItem);
                                    checkAndUpdateLists(latestBookStocks);
                                });
                            }
                        }
//                        checkAndUpdateLists(latestBookStocks, featuredBooks, newArrivals, category1Books, category2Books, category3Books);

                    } else {
                        // Handle error
                    }
                });
    }

    private void checkAndUpdateLists(Map<String, BookItem> latestBookStocks) {

        List<BookItem> featuredBooks = new ArrayList<>();
        List<BookItem> newArrivals = new ArrayList<>();
        List<BookItem> category1Books = new ArrayList<>();
        List<BookItem> category2Books = new ArrayList<>();
        List<BookItem> category3Books = new ArrayList<>();
        for (BookItem bookItem : latestBookStocks.values()) {
            if (bookItem.isFeatured()) {
                featuredBooks.add(bookItem);
            }
            if (bookItem.getCategory().equals(getRandomCategories().getValue()[0].getCategoryId())) {
                category1Books.add(bookItem);
            }
            if (bookItem.getCategory().equals(getRandomCategories().getValue()[1].getCategoryId())) {
                category2Books.add(bookItem);
            }
            if (bookItem.getCategory().equals(getRandomCategories().getValue()[2].getCategoryId())) {
                category3Books.add(bookItem);
            }
            newArrivals.add(bookItem);
        }
        featuredBooksLiveData.setValue(featuredBooks);
        newArrivalsLiveData.setValue(newArrivals);
        category1BooksLiveData.setValue(category1Books);
        category2BooksLiveData.setValue(category2Books);
        category3BooksLiveData.setValue(category3Books);
    }

    private void getBookItem(BookStock bookStock, OnBookFetchedListener listener) {
        db.collection("books").document(bookStock.getBookId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Book book = task.getResult().toObject(Book.class);
                BookItem bookItem = modelMapper.map(book, BookItem.class);
                bookItem.setImageUrl(book.getCoverImage());
                bookItem.setBookId(bookStock.getBookId());
                bookItem.setBookStockId(bookStock.getBookStockId());
                bookItem.setSellerId(bookStock.getSellerId());
                bookItem.setStock(bookStock.getStock());
                bookItem.setPrice(bookStock.getPrice());
                bookItem.setCondition(bookStock.getCondition());
                bookItem.setCreatedAt(bookStock.getCreatedAt());
                bookItem.setUpdatedAt(bookStock.getUpdatedAt());
                listener.onBookFetched(bookItem);
            } else {
                Log.e("Firestore", "Error getting book item", task.getException());
            }
        });
    }

    public void fetchCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Category> categories = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            category.setCategoryId(document.getId());
                            categories.add(category);
                        }
                        categoriesLiveData.setValue(categories);
                        getRandomCategories(categories, 3);
                    } else {
                        // Handle error
                    }
                });
    }

    private void getRandomCategories(List<Category> categories, int count) {
        List<Category> shuffledCategories = new ArrayList<>(categories);
        Collections.shuffle(shuffledCategories);
        Category[] randomCategoriesArray = categories.subList(0, count).toArray(new Category[0]);
        randomCategories.setValue(randomCategoriesArray);
        fetchAndProcessBookStocks();
    }

    public interface OnBookFetchedListener {
        void onBookFetched(BookItem bookItem);
    }
}