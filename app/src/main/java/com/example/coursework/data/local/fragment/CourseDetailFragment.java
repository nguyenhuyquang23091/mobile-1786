package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coursework.data.local.entities.YogaClassWithDetail;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.Toolbar;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaRepository;
import com.example.coursework.databinding.FragmentClassDetailBinding;

public class CourseDetailFragment extends Fragment {

    private FragmentClassDetailBinding binding;
    private YogaRepository yogaRepository;

    private String instanceId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         binding = FragmentClassDetailBinding.inflate(inflater, container, false);
         return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        yogaRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        setupToolbar();
        if (getArguments() != null) {
            instanceId = getArguments().getString("instanceId");
        }

        if (instanceId == null || instanceId.isEmpty()) {
            Snackbar.make(requireView(), "Error: Class instance not found", Snackbar.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }
        loadClassDetail();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.topAppBar;
        
        // Handle navigation icon click (back navigation to SearchFragment)
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(requireView()).popBackStack();
        });
    }

    private void loadClassDetail(){
        AppDatabase.databaseWriteExecutor.execute(() ->{
            YogaClassWithDetail detail = yogaRepository.getInstanceWithDetails(instanceId);
            bind(detail);
        });

    }

    private void bind(YogaClassWithDetail detail){


        binding.courseTitle.setText(detail.yogaCourse.getType());
         binding.dayValue.setText(detail.yogaCourse.getDay());
         binding.timeValue.setText(detail.yogaCourse.getTime());
         String capacity = detail.yogaCourse.getCapacity() + "people";
         binding.capacityValue.setText(capacity);
        String duration = detail.yogaCourse.getDuration() + "minutes";
        binding.durationValue.setText(duration);

        String price = detail.yogaCourse.getPrice() + "$";
        binding.priceValue.setText(price);
         binding.descriptionValue.setText(detail.yogaCourse.getDescription());

        binding.dateValue.setText(detail.yogaClass.getDate());
        binding.teacherValue.setText(detail.yogaClass.getTeacher());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
