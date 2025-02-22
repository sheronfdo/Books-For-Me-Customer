package com.jamith.booksformecustomer.viewModel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jamith.booksformecustomer.model.Book;
import com.jamith.booksformecustomer.model.BookItem;
import com.jamith.booksformecustomer.model.BookStock;
import com.jamith.booksformecustomer.model.Category;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<BookItem>> featuredBooksLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> newArrivalsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> category1BooksLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> category2BooksLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BookItem>> category3BooksLiveData = new MutableLiveData<>();
    private MutableLiveData<Category[]> randomCategories = new MutableLiveData<>();
    private MutableLiveData<List<Book>> booksLiveData = new MutableLiveData<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ModelMapper modelMapper = new ModelMapper();


    HomeViewModel() {
        fetchCategories();
        fetchTrendingBooks();
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

    public MutableLiveData<List<Book>> getBooksLiveData() {
        return booksLiveData;
    }

    public MutableLiveData<Category[]> getRandomCategories() {
        return randomCategories;
    }

    public void fetchTrendingBooks() {
        long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000); // 7 days in milliseconds
        Timestamp lastWeek = new Timestamp(new Date(sevenDaysAgo));

        Log.d("sevenDaysAgo", String.valueOf(sevenDaysAgo));
        Log.d("lastWeek", lastWeek.toString());
        db.collection("orders").whereGreaterThanOrEqualTo("createdAt", lastWeek) // Fetch recent orders
                .get().addOnSuccessListener(querySnapshot -> {
                    Map<String, Integer> trendingBooksMap = new HashMap<>();
                    List<Task<QuerySnapshot>> orderItemTasks = new ArrayList<>();

                    // Fetch orderItem subcollections from recent orders
                    for (DocumentSnapshot orderDoc : querySnapshot.getDocuments()) {
                        Task<QuerySnapshot> orderItemTask = orderDoc.getReference().collection("orderItems").get();
                        orderItemTasks.add(orderItemTask);
                        Log.d("orderItemTask", orderItemTask.toString());
                    }

                    Log.d("orderItemTasks", orderItemTasks.toString());
                    // Process order items after fetching
                    Tasks.whenAllSuccess(orderItemTasks).addOnSuccessListener(results -> {
                        for (Object result : results) {
                            QuerySnapshot querySnapshot1 = (QuerySnapshot) result;
                            for (DocumentSnapshot document : querySnapshot1.getDocuments()) {
                                String bookId = document.getString("bookId");
                                int quantity = document.getLong("quantity").intValue();

                                trendingBooksMap.put(bookId, trendingBooksMap.getOrDefault(bookId, 0) + quantity);
                            }
                        }
                        Log.d("trendingBooksMap", trendingBooksMap.toString());
                        // Sort trending books
                        List<Map.Entry<String, Integer>> sortedBooks = new ArrayList<>(trendingBooksMap.entrySet());
                        sortedBooks.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Descending order

                        List<String> trendingBookIds = new ArrayList<>();
                        for (int i = 0; i < Math.min(3, sortedBooks.size()); i++) {
                            trendingBookIds.add(sortedBooks.get(i).getKey());
                        }

                        Log.d("trendingBookIds", trendingBookIds.toString());
                        fetchBookDetails(trendingBookIds);
                    });
                }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching trending orders", e));
    }

    private void fetchBookDetails(List<String> bookIds) {
        List<Book> books = new ArrayList<>();
        if (bookIds.isEmpty()) {
            booksLiveData.setValue(books);
            return;
        }
        for (String bookId : bookIds) {
            db.collection("books").document(bookId).get().addOnSuccessListener(document -> {
                Book book = document.toObject(Book.class);
                if (book != null) {
                    books.add(book);
                }

                // Check if all books are processed
                if (books.size() == bookIds.size()) {
                    booksLiveData.setValue(books);
                }
            }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching book details", e));
        }
        Log.d("books", books.toString());
    }

    public void fetchCategories() {
        db.collection("categories").get().addOnCompleteListener(task -> {
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
        if (categories == null || categories.isEmpty() || count <= 0) {
            return;
        }
        List<Category> shuffledCategories = new ArrayList<>(categories);
        Collections.shuffle(shuffledCategories); // Shuffle the copied list
        List<Category> selectedCategories = shuffledCategories.subList(0, Math.min(count, shuffledCategories.size()));
        Category[] randomCategoriesArray = selectedCategories.toArray(new Category[0]);
        randomCategories.setValue(randomCategoriesArray);
        fetchAndProcessBookStocks();
    }

    public void fetchAndProcessBookStocks() {
        db.collection("bookStocks").limit(100).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, BookItem> latestBookStocks = new HashMap<>();
                Log.d("bookcount", String.valueOf(task.getResult().size()));
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
                    if(latestBookStocks.size() == task.getResult().size()){
                        checkAndUpdateLists(latestBookStocks);
                    }
                }
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
        featuredBooksLiveData.setValue(featuredBooks);
        newArrivalsLiveData.setValue(newArrivals);
        category1BooksLiveData.setValue(category1Books);
        category2BooksLiveData.setValue(category2Books);
        category3BooksLiveData.setValue(category3Books);
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


    public interface OnBookFetchedListener {
        void onBookFetched(BookItem bookItem);
    }
}