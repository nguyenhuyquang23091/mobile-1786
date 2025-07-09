package com.example.coursework.data.local.fragment;

import android.content.Intent;
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
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.databinding.FragmentCreateClassBinding;

import java.util.List;
import java.util.Objects;

public class ClassListFragment extends Fragment {
    private YogaClassRepository yogaClassRepository;
    private YogaClassAdapter yogaClassAdapter;
    private RecyclerView recyclerView;

    private FragmentCreateClassBinding binding;

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
            public void onDeleteClick(YogaClass yogaClass) {
                yogaClassRepository.delete(yogaClass);
                Toast.makeText(getContext(), "Class deleted", Toast.LENGTH_SHORT).show();
                loadClasses();
            }

            @Override
            public void onItemClick(YogaClass yogaClass) {
                ClassListFragmentDirections.ActionClassListFragmentToClassInstanceFragment action =
                        ClassListFragmentDirections.actionClassListFragmentToClassInstanceFragment(yogaClass.uid);
                Navigation.findNavController(requireView()).navigate(action);

            }
        });
        recyclerView.setAdapter(yogaClassAdapter);

    }
    private void loadClasses(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<YogaClass> classes = yogaClassRepository.getAll();
            requireActivity().runOnUiThread(() -> {
                yogaClassAdapter.setClasses(classes);
            });
        });
    }

}
