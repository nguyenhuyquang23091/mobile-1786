package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.YogaClassAdapter;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;

import java.util.List;

public class ClassListFragment extends Fragment {
    private YogaClassRepository yogaClassRepository;
    private YogaClassAdapter yogaClassAdapter;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= view.findViewById(R.id.recyclerViewClasses);
        yogaClassRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        setupRecyclerView();

    }
    @Override
    public void onResume() {
        super.onResume();
        loadClasses();

    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        yogaClassAdapter = new YogaClassAdapter(new YogaClassAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(YogaCourse yogaCourse) {
                yogaClassRepository.delete(yogaCourse);
                Toast.makeText(getContext(), "Class deleted", Toast.LENGTH_SHORT).show();
                loadClasses();
            }

            @Override
            public void onItemClick(YogaCourse yogaCourse) {
                ClassListFragmentDirections.ActionClassListFragmentToClassInstanceFragment action =
                        ClassListFragmentDirections.actionClassListFragmentToClassInstanceFragment(yogaCourse.uid);
                Navigation.findNavController(requireView()).navigate(action);

            }
        });
        recyclerView.setAdapter(yogaClassAdapter);

    }
    private void loadClasses(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<YogaCourse> classes = yogaClassRepository.getAll();
            requireActivity().runOnUiThread(() -> {
                yogaClassAdapter.setClasses(classes);
            });
        });
    }

}
