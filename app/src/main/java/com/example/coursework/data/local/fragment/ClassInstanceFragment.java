package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.coursework.databinding.FragmentCreateClassBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ClassInstanceFragment extends Fragment {

    public static final String EXTRA_COURSE_ID = "com.example.coursework.EXTRA_COURSE_ID";
    private YogaRepositoryImplementation repository;
    private ClassInstanceAdapter adapter;
    private int courseId;

    private ExtendedFloatingActionButton addInstanceFab;
    private RecyclerView recyclerViewInstances;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_instance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addInstanceFab = view.findViewById(R.id.add_instance_fab);
        recyclerViewInstances = view.findViewById(R.id.recyclerViewInstances);
        repository = new YogaRepositoryImplementation(requireActivity().getApplication());

        recyclerViewInstances.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                if (dy > 0 && addInstanceFab.isExtended()) {
                    addInstanceFab.shrink();
                } else if (dy < 0 && !addInstanceFab.isExtended()) {
                    addInstanceFab.extend();
                }
            }
        });
        if (getArguments() != null) {
            courseId = ClassInstanceFragmentArgs.fromBundle(getArguments()).getCourseId();
        }
        if (courseId == -1) {
            Toast.makeText(getContext(), "Error: Course ID not found.", Toast.LENGTH_SHORT).show();
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

    private void setupRecyclerView() {
        recyclerViewInstances.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ClassInstanceAdapter(new ClassInstanceAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(ClassInstance classInstance) {
                repository.deleteInstance(classInstance);
                Toast.makeText(getContext(), "Instance Deleted", Toast.LENGTH_SHORT).show();
                loadInstances();
            }

            @Override
            public void onEditCLick(ClassInstance classInstance) {
                showAddEditInstanceDialog(classInstance);
            }
        });
        recyclerViewInstances.setAdapter(adapter);
    }

    private void setupFab() {
        ExtendedFloatingActionButton fab = requireView().findViewById(R.id.add_instance_fab);
        fab.setOnClickListener(v -> showAddEditInstanceDialog(null));
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
            // *** FIX: Use UTC timezone to match the search format ***
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
                        Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (instanceToEdit == null) {
                        ClassInstance newInstance = new ClassInstance();
                        newInstance.date = date;
                        newInstance.teacher = teacher;
                        newInstance.courseId = this.courseId;
                        repository.insertInstance(newInstance);
                        Toast.makeText(getContext(), "Instance Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        instanceToEdit.date = date;
                        instanceToEdit.teacher = teacher;
                        repository.updateInstance(instanceToEdit);
                        Toast.makeText(getContext(), "Instance Updated", Toast.LENGTH_SHORT).show();
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
}
