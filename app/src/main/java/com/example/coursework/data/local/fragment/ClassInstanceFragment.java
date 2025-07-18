package com.example.coursework.data.local.fragment;

import static android.app.ProgressDialog.show;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.ClassInstanceAdapter;
import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.databinding.FragmentClassInstanceBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ClassInstanceFragment extends Fragment {
    private FragmentClassInstanceBinding binding;
    private YogaRepositoryImplementation repository;
    private ClassInstanceAdapter adapter;
    private int courseId;



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
            courseId = ClassInstanceFragmentArgs.fromBundle(getArguments()).getCourseId();
        }
        if (courseId == -1) {
            Snackbar.make(requireView(), "Error: Course ID not found", Snackbar.LENGTH_SHORT).show();
            //go back if no return
            Navigation.findNavController(view).popBackStack();
            return;
        }
        setupRecyclerView();
        setupFab();
        loadInstances();
    }

    private void setupRecyclerView() {
        binding.recyclerViewInstances.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassInstanceAdapter(new ClassInstanceAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(ClassInstance classInstance) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Confirm Deletion")
                                .setMessage("Are you sure you want to delete this instance?")
                                        .setNegativeButton("Cancel", null)
                                                .setPositiveButton("Delete", ((dialog, which) -> {
                                                    repository.deleteInstance(classInstance);
                                                    Snackbar.make(requireView(), "Instance Deleted", Snackbar.LENGTH_LONG).show();
                                                })).show();



                loadInstances();
            }
            @Override
            public void onEditCLick(ClassInstance classInstance) {
                showAddEditInstanceDialog(classInstance);
            }

            @Override
            public void onItemClick(ClassInstance classInstance) {

            }
        }, true); // pass true to show Edit/Delete buttons
        binding.recyclerViewInstances.setAdapter(adapter);
    }
    private void setupFab() {
        binding.addInstanceFab.setOnClickListener(v -> showAddEditInstanceDialog(null));
    }
    private void showAddEditInstanceDialog(final ClassInstance instanceToEdit) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_instance, null);

        final TextInputEditText dateInput = dialogView.findViewById(R.id.date_input);
        final TextInputEditText teacherInput = dialogView.findViewById(R.id.teacher_input);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateInput.setText(sdf.format(new Date(selection)));
        });
        dateInput.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));

        if (instanceToEdit != null) {
            dateInput.setText(instanceToEdit.date);
            teacherInput.setText(instanceToEdit.teacher);
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(instanceToEdit == null ? "Add Instance" : "Edit Instance")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String date = Objects.requireNonNull(dateInput.getText()).toString();
                    String teacher = Objects.requireNonNull(teacherInput.getText()).toString();

                    if (date.isEmpty() || teacher.isEmpty()) {
                        Snackbar.make(requireView(), "Please fill in all fields", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (instanceToEdit == null) {
                        ClassInstance newInstance = new ClassInstance();
                        newInstance.date = date;
                        newInstance.teacher = teacher;
                        newInstance.courseId = this.courseId;
                        repository.insertInstance(newInstance);
                        Snackbar.make(requireView(), "Instance saved successfully", Snackbar.LENGTH_SHORT).show();
                    } else {
                        instanceToEdit.date = date;
                        instanceToEdit.teacher = teacher;
                        repository.updateInstance(instanceToEdit);
                        Snackbar.make(requireView(), "Instance updated successfully", Snackbar.LENGTH_SHORT).show();
                    }
                    loadInstances();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void loadInstances() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ClassInstance> instances = repository.getInstance(courseId);
            requireActivity().runOnUiThread(() -> adapter.setClasses(instances));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
