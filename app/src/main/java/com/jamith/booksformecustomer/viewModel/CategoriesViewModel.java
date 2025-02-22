package com.jamith.booksformecustomer.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jamith.booksformecustomer.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoriesViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();

    public void fetchCategories() {
        FirebaseFirestore.getInstance().collection("categories")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Category> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Category category = document.toObject(Category.class);
                        category.setCategoryId(document.getId());
                        categories.add(category);
                    }
                    categoriesLiveData.postValue(categories);
                }
            });
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }
}