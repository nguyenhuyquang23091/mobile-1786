package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.search.SearchView;

import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {
    private YogaClassRepository yogaClassRepository;
    private ClassInstanceAdapter adapter;
    private SearchView searchView;

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
            public void onDeleteClick(ClassInstance classInstance) {

            }

            @Override
            public void onEditCLick(ClassInstance classInstance) {

            }
        });
        recyclerView.setAdapter(adapter);

    }
    private void setUpSearch(View view){
        searchView = view.findViewById(R.id.search_view);
        searchView.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString());

            }
        });
    }
    private  void performSearch(String query){
        if (query.isEmpty()) {
            adapter.setClasses(Collections.emptyList());
            return;
        }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ClassInstance> results = yogaClassRepository.searchByTeacher(query);
            requireActivity().runOnUiThread(() -> adapter.setClasses(results));
        });
    }
}
