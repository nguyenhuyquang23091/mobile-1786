package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.ClassInstanceAdapter;
import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment"; // Tag for logging
    private YogaClassRepository yogaClassRepository;
    private ClassInstanceAdapter adapter;
    private SearchView searchView;
    private SearchBar searchBar;

    // *** FIX: Add a boolean flag to guard the TextWatcher ***
    private boolean isProgrammaticTextChange = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yogaClassRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        setUpRecyclerView(view);
        setUpSearch(view);
    }

    private void setUpRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassInstanceAdapter(new ClassInstanceAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(ClassInstance classInstance) { /* Not needed here */ }
            @Override
            public void onEditCLick(ClassInstance classInstance) { /* Not needed here */ }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setUpSearch(View view){
        searchView = view.findViewById(R.id.search_view);
        searchBar = view.findViewById(R.id.search_bar);

        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                // *** FIX: Only search if the change was not programmatic ***
                if (!isProgrammaticTextChange) {
                    performSearchByTeacher(s.toString());
                }
            }
        });

        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_filter) {
                showFilterDialog();
                return true;
            }
            return false;
        });
    }

    private void showFilterDialog() {
        final String[] options = {"Search by Date", "Search by Day of the Week"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Advanced Search")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showDatePicker();
                    } else {
                        showDayPicker();
                    }
                })
                .show();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String selectedDate = sdf.format(new Date(selection));
            performSearchByDate(selectedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void showDayPicker() {
        final String[] days = getResources().getStringArray(R.array.days_array);
        final String[] displayDays = new String[days.length - 1];
        System.arraycopy(days, 1, displayDays, 0, days.length - 1);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select a Day")
                .setItems(displayDays, (dialog, which) -> {
                    String selectedDay = displayDays[which];
                    performSearchByDay(selectedDay);
                })
                .show();
    }

    private void performSearchByTeacher(String query){
        Log.d(TAG, "Searching for teacher: " + query);
        if (query.isEmpty()) {
            adapter.setClasses(Collections.emptyList());
            return;
        }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ClassInstance> results = yogaClassRepository.searchByTeacher(query);
            Log.d(TAG, "Found " + results.size() + " teachers matching '" + query + "'");
            requireActivity().runOnUiThread(() -> adapter.setClasses(results));
        });
    }

    private void performSearchByDate(String date) {
        Log.d(TAG, "Searching for date: " + date);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ClassInstance> results = yogaClassRepository.searchByDate(date);
            Log.d(TAG, "Found " + results.size() + " classes on date '" + date + "'");
            requireActivity().runOnUiThread(() -> {
                adapter.setClasses(results);
                updateSearchBarText("Date: " + date);
            });
        });
    }

    private void performSearchByDay(String day) {
        Log.d(TAG, "Searching for day: " + day);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ClassInstance> results = yogaClassRepository.searchByDay(day);
            Log.d(TAG, "Found " + results.size() + " classes on day '" + day + "'");
            requireActivity().runOnUiThread(() -> {
                adapter.setClasses(results);
                updateSearchBarText("Day: " + day);
            });
        });
    }

    // *** FIX: Helper method to safely update search bar text ***
    private void updateSearchBarText(String text) {
        isProgrammaticTextChange = true;
        searchBar.setText(text);
        isProgrammaticTextChange = false;
    }
}
