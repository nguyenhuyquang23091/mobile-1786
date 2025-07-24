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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coursework.R;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.databinding.FragmentSearchBinding;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.YogaClassAdapter;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaRepository;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private YogaRepository yogaRepository;
    private YogaClassAdapter adapter;
    private boolean allowTextChange = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yogaRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        setUpRecyclerView();
        setUpSearch();
    }

    private void setUpRecyclerView(){
        binding.recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new YogaClassAdapter(new YogaClassAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(YogaClass yogaClass) {
            }
            @Override
            public void onEditCLick(YogaClass yogaClass) {

            }
            @Override
            public void onItemClick(YogaClass yogaClass) {
                SearchFragmentDirections.SearchToDetail action = SearchFragmentDirections.searchToDetail(yogaClass.id);
                Navigation.findNavController(requireView()).navigate(action);

            }
        }, false);
        binding.recyclerViewSearchResults.setAdapter(adapter);
    }

    private void setUpSearch(){
        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (!allowTextChange) {
                    performSearchByTeacher(s.toString());
                }
            }
        });

        binding.searchBar.setOnMenuItemClickListener(item -> {
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
        if (query.isEmpty()) {
            adapter.setClasses(Collections.emptyList());
            return;
        }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<YogaClass> results = yogaRepository.searchByTeacher(query);
            requireActivity().runOnUiThread(() -> adapter.setClasses(results));
        });
    }
    private void performSearchByDate(String date) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<YogaClass> results = yogaRepository.searchByDate(date);
            requireActivity().runOnUiThread(() -> {
                binding.searchView.show();
                adapter.setClasses(results);
                updateSearchBarText("Date: " + date);
            });
        });
    }
    private void performSearchByDay(String day) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Log.d("SearchFragment", "Searching for day: " + day);
            List<YogaClass> results = yogaRepository.searchByDay(day);
            Log.d("SearchFragment", "Search results count: " + results.size());
            if(results.isEmpty()){
                Log.d("SearchFragment", "No results found for day: " + day);
            }
            requireActivity().runOnUiThread(() -> {
                binding.searchView.show();
                adapter.setClasses(results);
                updateSearchBarText("Day: " + day);
            });
        });
    }
    private void updateSearchBarText(String text) {
        allowTextChange = true;
        binding.searchBar.setText(text);
        allowTextChange = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
