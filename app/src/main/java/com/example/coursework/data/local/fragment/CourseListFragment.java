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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coursework.databinding.FragmentCourseListBinding;
import com.example.coursework.data.local.adapter.YogaCourseAdapter;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaRepository;
import com.example.coursework.data.local.util.SyncYogaCourseListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.transition.MaterialElevationScale;


import java.util.List;

public class CourseListFragment extends Fragment {
    private FragmentCourseListBinding binding;
    private YogaRepository yogaRepository;
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
        yogaRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        recylerView = binding.recyclerViewClasses;
        shimmerFrameLayout = binding.shimmerLayout;
        setExitTransition(new MaterialElevationScale(false));
        setReenterTransition(new MaterialElevationScale(true));
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        loadClasses();
    }
    private void setupRecyclerView() {
        binding.recyclerViewClasses.setLayoutManager(new LinearLayoutManager(requireContext()));
        yogaCourseAdapter = new YogaCourseAdapter(new YogaCourseAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(YogaCourse yogaCourse) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to deleteYogaCourse \"" + yogaCourse.getType() + "\"? This action cannot be undone.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            yogaRepository.deleteYogaCourse(yogaCourse);
                            Snackbar.make(requireView(), "Class deleted successfully", Snackbar.LENGTH_SHORT).show();
                            loadClasses();
                        })
                        .setIcon(R.drawable.ic_red_alert)
                        .show();
            }

            @Override
            public void onItemClick(YogaCourse yogaCourse, FragmentNavigator.Extras extras) {
                CourseListFragmentDirections.ActionClassListFragmentToClassInstanceFragment action =
                        CourseListFragmentDirections.actionClassListFragmentToClassInstanceFragment(yogaCourse.getUid());
                Navigation.findNavController(requireView()).navigate(action, extras);
            }

            @Override
            public void onEditClick(YogaCourse yogaCourse) {
                // Create bundle with prefilled data for editing
                Bundle bundle = new Bundle();
                bundle.putString("prefilled_type", yogaCourse.getType());
                bundle.putString("prefilled_day", yogaCourse.getDay());
                bundle.putString("prefilled_time", yogaCourse.getTime());
                bundle.putInt("prefilled_capacity", yogaCourse.getCapacity());
                bundle.putInt("prefilled_duration", yogaCourse.getDuration());
                bundle.putString("prefilled_price", String.valueOf(yogaCourse.getPrice()));
                bundle.putString("prefilled_description", yogaCourse.getDescription());
                bundle.putString("prefilled_intensity", yogaCourse.getIntensity());
                bundle.putString("edit_course_uid", yogaCourse.getUid());
                
                // Navigate to CreateCourseFragment with prefilled data
                Navigation.findNavController(requireView()).navigate(
                    R.id.action_courseListFragment_to_createCourseFragment, bundle);
            }


        });
        binding.recyclerViewClasses.setAdapter(yogaCourseAdapter);
    }
    private void loadClasses(){
        Log.d("CourseListFragment", "Starting loadClasses() - showing shimmer and hiding RecyclerView");
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        binding.recyclerViewClasses.setVisibility(View.GONE);

        // First try to load from Firebase, then fall back to local data
        Log.d("CourseListFragment", "Calling yogaRepository.loadAllCoursesFromFirebase()");
        yogaRepository.loadAllCoursesFromFirebase(new SyncYogaCourseListener() {
            @Override
            public void syncFirebaseWithLocal() {
                Log.d("CourseListFragment", "Firebase sync successful, but not displaying anything yet");
            }

            @Override
            public void syncFirebaseWithLocal(List<YogaCourse> courses) {
                Log.d("CourseListFragment", "Firebase loaded " + courses.size() + " courses directly");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        yogaCourseAdapter.setCourses(courses);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        binding.recyclerViewClasses.setVisibility(View.VISIBLE);
                        Log.d("CourseListFragment", "UI updated with Firebase courses");
                    });
                }
            }

            @Override
            public void syncFailure(String error) {
                Log.e("CourseListFragment", "Firebase load failed: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        Snackbar.make(requireView(), "Failed to load courses: " + error, Snackbar.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
