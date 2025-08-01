package com.example.coursework.data.local.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.YogaClassAdapter;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.util.SyncYogaClassesListener;
import com.example.coursework.databinding.FragmentClassInstanceBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.MaterialContainerTransform; // Add this

import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class YogaClassFragment extends Fragment {
    private FragmentClassInstanceBinding binding;
    private YogaRepositoryImplementation repository;
    private YogaClassAdapter adapter;
    private String courseId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClassInstanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new YogaRepositoryImplementation(requireActivity().getApplication());
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setScrimColor(ContextCompat.getColor(requireContext(), R.color.colorBackground));
        setSharedElementEnterTransition(transform);

        setupToolbar();

        binding.recyclerViewInstances.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                if (dy > 0 && binding.addInstanceFab.isExtended()) {
                    binding.addInstanceFab.shrink();
                } else if (dy < 0 && !binding.addInstanceFab.isExtended()) {
                    binding.addInstanceFab.extend();
                }
            }
        });
        if (getArguments() != null) {
            courseId = YogaClassFragmentArgs.fromBundle(getArguments()).getCourseId();
        }
        if (courseId == null || courseId.isEmpty()) {
            Snackbar.make(requireView(), "Error: Course ID not found", Snackbar.LENGTH_SHORT).show();
            //go back if no return
            Navigation.findNavController(view).popBackStack();
            return;
        }
        setupRecyclerView();
        setupFab();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadInstances();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.topAppBar;
        
        // Handle navigation icon click (back navigation to CourseFragment)
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(requireView()).popBackStack();
        });
    }

    private void setupRecyclerView() {
        binding.recyclerViewInstances.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new YogaClassAdapter(new YogaClassAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(YogaClass yogaClass) {
                androidx.appcompat.app.AlertDialog deleteDialog = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Instance")
                        .setMessage("Are you sure you want to delete the instance on \"" + yogaClass.getDate() + "\" with teacher \"" + yogaClass.getTeacher() + "\"? This action cannot be undone.")
                        .setIcon(R.drawable.ic_red_alert)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            repository.deleteYogaClass(yogaClass);
                            Snackbar.make(requireView(), "Deleting instance...", Snackbar.LENGTH_SHORT).show();
                            // Refresh instances after a short delay to allow Firebase sync
                            binding.getRoot().postDelayed(() -> loadInstances(), 1000);
                        })
                        .create();
                
                
                deleteDialog.show();
            }
            @Override
            public void onEditCLick(YogaClass yogaClass) {
                showAddEditInstanceFullScreenDialog(yogaClass);
            }

            @Override
            public void onItemClick(YogaClass yogaClass) {

            }
        }, true); // pass true to show Edit/Delete buttons
        binding.recyclerViewInstances.setAdapter(adapter);
    }
    private void setupFab() {
        binding.addInstanceFab.setOnClickListener(v -> showAddEditInstanceFullScreenDialog(null));
    }
    private void showAddEditInstanceFullScreenDialog(final YogaClass instanceToEdit) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_instance_fullscreen, null);
        final TextInputEditText titleInput = dialogView.findViewById(R.id.title_input);
        final TextInputEditText dateInput = dialogView.findViewById(R.id.date_input);
        final TextInputEditText teacherInput = dialogView.findViewById(R.id.teacher_input);
        final TextInputEditText descriptionInput = dialogView.findViewById(R.id.description_input);
        final View loadingIndicator = dialogView.findViewById(R.id.loading_indicator);
        final View saveButton = dialogView.findViewById(R.id.save_button);
        final View cancelButton = dialogView.findViewById(R.id.cancel_button);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            dateInput.setText(sdf.format(new java.util.Date(selection)));
        });

        dateInput.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));

        if (instanceToEdit != null) {
            titleInput.setText(instanceToEdit.getTitle());
            dateInput.setText(instanceToEdit.getDate());
            teacherInput.setText(instanceToEdit.getTeacher());
            descriptionInput.setText(instanceToEdit.getDescription());
        }

        MaterialContainerTransform containerTransform = new MaterialContainerTransform();
        containerTransform.setScrimColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
        containerTransform.setAllContainerColors(ContextCompat.getColor(requireContext(), R.color.surface));
        containerTransform.setDuration(300L);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        saveButton.setOnClickListener(v -> {
            String title = java.util.Objects.requireNonNull(titleInput.getText()).toString().trim();
            String date = java.util.Objects.requireNonNull(dateInput.getText()).toString().trim();
            String teacher = java.util.Objects.requireNonNull(teacherInput.getText()).toString().trim();
            String description = java.util.Objects.requireNonNull(descriptionInput.getText()).toString().trim();

            if (title.isEmpty() || date.isEmpty() || teacher.isEmpty()) {
                Snackbar.make(dialogView, "Please fill in all required fields", Snackbar.LENGTH_SHORT).show();
                return;
            }

            loadingIndicator.setVisibility(View.VISIBLE);
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);

            AppDatabase.databaseWriteExecutor.execute(() -> {
                String courseday = repository.findById(courseId).getDay();
                String courseType = repository.findById(courseId).getType();
                requireActivity().runOnUiThread(() -> {
                    if (!isValidDate(date, courseday)) {
                        loadingIndicator.setVisibility(View.GONE);
                        saveButton.setEnabled(true);
                        cancelButton.setEnabled(true);

                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Invalid Date")
                                .setMessage("This course is scheduled for " + courseday +
                                        "s only. Please select a " + courseday + " date.")
                                .setIcon(R.drawable.ic_caution)
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    dialogView.postDelayed(() -> {
                        if (instanceToEdit == null) {
                            YogaClass newInstance = new YogaClass();
                            newInstance.setId(null);
                            newInstance.setTitle(title);
                            newInstance.setDate(date);
                            newInstance.setTeacher(teacher);
                            newInstance.setDescription(description);
                            newInstance.setCourseId(this.courseId);
                            newInstance.setCourseType(courseType);
                            repository.insertYogaClass(newInstance);
                            Snackbar.make(requireView(), "Saving class...", Snackbar.LENGTH_SHORT).show();
                        } else {
                            instanceToEdit.setTitle(title);
                            instanceToEdit.setDate(date);
                            instanceToEdit.setTeacher(teacher);
                            instanceToEdit.setDescription(description);
                            repository.updateYogaClass(instanceToEdit);
                            Snackbar.make(requireView(), "Updating class...", Snackbar.LENGTH_SHORT).show();
                        }
                        // Refresh instances after a short delay to allow Firebase sync
                        dialogView.postDelayed(() -> {
                            loadInstances();
                            dialog.dismiss();
                        }, 1500);
                    }, 1000);
                });
            });
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void loadInstances() {
        Log.d("YogaClassFragment", "Loading instances from Firestore for courseId: " + courseId);
        
        repository.loadAllClassesFromFirebase(String.valueOf(courseId), new SyncYogaClassesListener() {
            @Override
            public void syncFailure(String errorMessage) {
                Log.e("YogaClassFragment", "Failed to load classes from Firebase: " + errorMessage);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Snackbar.make(requireView(), "Failed to load classes: " + errorMessage, Snackbar.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void syncClassesWithFirebase() {
                Log.d("YogaClassFragment", "Firebase sync successful, but no data returned");
            }

            @Override
            public void syncClassesWithFirebase(List<YogaClass> yogaClassList) {
                Log.d("YogaClassFragment", "Loaded " + yogaClassList.size() + " classes from Firebase");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adapter.setClasses(yogaClassList);
                    });
                }
            }
        });
    }



    private boolean isValidDate(String selectedDate, String  courseDay){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date date = simpleDateFormat.parse(selectedDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            String[] daysOfWeek = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            String selectedDayOfWeek = daysOfWeek[dayOfWeek];

            return selectedDayOfWeek.equalsIgnoreCase(courseDay);

        } catch (ParseException e) {
            Log.d("Error while parsing", Objects.requireNonNull(e.getMessage()));
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
