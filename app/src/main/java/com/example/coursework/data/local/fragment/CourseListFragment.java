package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coursework.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coursework.databinding.FragmentCourseListBinding;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.YogaCourseAdapter;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.data.local.util.SyncFirebaseListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.transition.MaterialElevationScale;


import java.util.List;
import java.util.Objects;

public class CourseListFragment extends Fragment {
    private FragmentCourseListBinding binding;
    private YogaClassRepository yogaClassRepository;
    private YogaCourseAdapter yogaCourseAdapter;

    private RecyclerView recylerView;
    private ShimmerFrameLayout shimmerFrameLayout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCourseListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yogaClassRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        recylerView = binding.recyclerViewClasses;
        shimmerFrameLayout = binding.shimmerLayout;
        setExitTransition(new MaterialElevationScale(false));
        setReenterTransition(new MaterialElevationScale(true));
        setupRecyclerView();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadClasses();
    }
    private void setupRecyclerView() {
        binding.recyclerViewClasses.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        yogaCourseAdapter = new YogaCourseAdapter(new YogaCourseAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(YogaCourse yogaCourse) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to delete \"" + yogaCourse.type + "\"? This action cannot be undone.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            yogaClassRepository.delete(yogaCourse);
                            Snackbar.make(requireView(), "Class deleted successfully", Snackbar.LENGTH_SHORT).show();
                            loadClasses();
                        })
                        .setIcon(R.drawable.ic_red_alert)
                        .show();
            }

            @Override
            public void onItemClick(YogaCourse yogaCourse, FragmentNavigator.Extras extras) {
                CourseListFragmentDirections.ActionClassListFragmentToClassInstanceFragment action =
                        CourseListFragmentDirections.actionClassListFragmentToClassInstanceFragment(yogaCourse.uid);
                Navigation.findNavController(requireView()).navigate(action, extras);
            }




        });
        binding.recyclerViewClasses.setAdapter(yogaCourseAdapter);
    }
    private void loadClasses(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        binding.recyclerViewClasses.setVisibility(View.GONE);
        
        // First try to load from Firebase, then fall back to local data
        yogaClassRepository.loadAllCoursesFromFirebase(new SyncFirebaseListener() {
            @Override
            public void syncFirebasewithLocal() {
                Log.d("CourseListFragment", "Successfully loaded courses from Firebase");
                loadLocalCourses();
            }

            @Override
            public void syncFailure(String error) {
                Log.d("CourseListFragment", "Firebase load failed or no connection: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Snackbar.make(requireView(), "Loading from local storage", Snackbar.LENGTH_SHORT).show();
                    });
                }
                loadLocalCourses();
            }
        });
    }
    
    private void loadLocalCourses() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Thread.sleep(1000); // Reduced loading time
            } catch (InterruptedException e){
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            List<YogaCourse> courses = yogaClassRepository.getAll();
            if (getActivity() != null ){
                getActivity().runOnUiThread(() -> {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    binding.recyclerViewClasses.setVisibility(View.VISIBLE);
                    yogaCourseAdapter.setCourses(courses);
                    if (courses.isEmpty()) {
                        Snackbar.make(requireView(), "No courses found. Create your first course!", 
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        Log.d("CourseListFragment", "Loaded " + courses.size() + " courses");
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
