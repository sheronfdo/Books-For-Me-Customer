package com.jamith.booksformecustomer.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jamith.booksformecustomer.R;
import com.jamith.booksformecustomer.model.Category;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class FilterBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private FilterListener listener;
    private List<Category> categories;
    private final Set<String> selectedCategoryIds = new HashSet<>();

    public interface FilterListener {
        void onFilterApplied(Set<String> selectedCategoryIds);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout container = view.findViewById(R.id.category_container);

        if (categories != null) {
            for (Category category : categories) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText(category.getName());
                checkBox.setTag(category.getCategoryId());
                checkBox.setChecked(selectedCategoryIds.contains(category.getCategoryId()));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String categoryId = (String) buttonView.getTag();
                    if (isChecked) {
                        selectedCategoryIds.add(categoryId);
                    } else {
                        selectedCategoryIds.remove(categoryId);
                    }
                });
                
                container.addView(checkBox);
            }
        }

        view.findViewById(R.id.btn_apply_filter).setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterApplied(selectedCategoryIds);
            }
            dismiss();
        });
    }

    public void setSelectedCategories(Set<String> selectedIds) {
        this.selectedCategoryIds.clear();
        this.selectedCategoryIds.addAll(selectedIds);
        if (getView() != null) {
            LinearLayout container = getView().findViewById(R.id.category_container);
            for (int i = 0; i < container.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) container.getChildAt(i);
                String categoryId = (String) checkBox.getTag();
                checkBox.setChecked(selectedCategoryIds.contains(categoryId));
            }
        }
    }
}