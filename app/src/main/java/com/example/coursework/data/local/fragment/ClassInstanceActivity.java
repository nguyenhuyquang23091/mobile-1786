package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.ClassInstanceAdapter;
import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
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

public class ClassInstanceActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "com.example.coursework.EXTRA_COURSE_ID";
    private YogaRepositoryImplementation repository;
    private ClassInstanceAdapter adapter;
    private int courseId;

    private ExtendedFloatingActionButton addInstanceFab;
    private RecyclerView recyclerViewInstances;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_instance);
        addInstanceFab = findViewById(R.id.add_instance_fab);
        recyclerViewInstances = findViewById(R.id.recyclerViewInstances);

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
        repository = new YogaRepositoryImplementation(getApplication());
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);

        if (courseId == -1) {
            Toast.makeText(this, "Error: Course ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setupRecyclerView();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInstances();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewInstances);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ClassInstanceAdapter(new ClassInstanceAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(ClassInstance classInstance) {
                repository.deleteInstance(classInstance);
                Toast.makeText(ClassInstanceActivity.this, "Instance Deleted", Toast.LENGTH_SHORT).show();
                loadInstances();
            }

            @Override
            public void onEditCLick(ClassInstance classInstance) {
                showAddEditInstanceDialog(classInstance);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        ExtendedFloatingActionButton fab = findViewById(R.id.add_instance_fab);
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
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateInput.setText(sdf.format(new Date(selection)));
        });
        dateInput.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        if (instanceToEdit != null) {
            dateInput.setText(instanceToEdit.date);
            teacherInput.setText(instanceToEdit.teacher);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(instanceToEdit == null ? "Add Instance" : "Edit Instance")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String date = Objects.requireNonNull(dateInput.getText()).toString();
                    String teacher = Objects.requireNonNull(teacherInput.getText()).toString();

                    if (date.isEmpty() || teacher.isEmpty()) {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (instanceToEdit == null) {
                        ClassInstance newInstance = new ClassInstance();
                        newInstance.date = date;
                        newInstance.teacher = teacher;
                        newInstance.courseId = this.courseId;
                        repository.insertInstance(newInstance);
                        Toast.makeText(this, "Instance Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        instanceToEdit.date = date;
                        instanceToEdit.teacher = teacher;
                        repository.updateInstance(instanceToEdit);
                        Toast.makeText(this, "Instance Updated", Toast.LENGTH_SHORT).show();
                    }
                    loadInstances();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadInstances() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ClassInstance> instances = repository.getInstance(courseId);
            runOnUiThread(() -> adapter.setClasses(instances));
        });
    }
}