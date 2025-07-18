package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coursework.R;
import com.example.coursework.databinding.FragmentClassListBinding;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.YogaClassAdapter;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;

import java.util.List;

public class CourseListFragment extends Fragment {
    private FragmentClassListBinding binding;
    private YogaClassRepository yogaClassRepository;
    private YogaClassAdapter yogaClassAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClassListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yogaClassRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        setupRecyclerView();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadClasses();
    }
    private void setupRecyclerView() {
        binding.recyclerViewClasses.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        yogaClassAdapter = new YogaClassAdapter(new YogaClassAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(YogaCourse yogaCourse) {
                yogaClassRepository.delete(yogaCourse);
                Snackbar.make(requireView(), "Class deleted successfully", Snackbar.LENGTH_SHORT).show();
                loadClasses();
            }

            @Override
            public void onItemClick(YogaCourse yogaCourse) {
                CourseListFragmentDirections.ActionClassListFragmentToClassInstanceFragment action =
                        CourseListFragmentDirections.actionClassListFragmentToClassInstanceFragment(yogaCourse.uid);
                Navigation.findNavController(requireView()).navigate(action);

            }
        });
        binding.recyclerViewClasses.setAdapter(yogaClassAdapter);
    }
    private void loadClasses(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<YogaCourse> classes = yogaClassRepository.getAll();
            requireActivity().runOnUiThread(() -> {
                yogaClassAdapter.setClasses(classes);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
