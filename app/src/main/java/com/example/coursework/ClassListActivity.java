package com.example.coursework;

import android.app.Application;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ClassListActivity extends AppCompatActivity {
    private YogaClassRepository yogaClassRepository;
    private YogaClassAdapter yogaClassAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_class_list);
        yogaClassRepository = new YogaRepositoryImplementation(getApplication());
        setupRecyclerView();
    }
    @Override
    protected void onResume(){
        super.onResume();
        loadClasses();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewClasses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        yogaClassAdapter = new YogaClassAdapter(yogaClass -> {
            yogaClassRepository.delete(yogaClass);
            loadClasses();
        });
    };
    private void loadClasses(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<YogaClass> classes = yogaClassRepository.getAll();
            runOnUiThread(() -> {
                yogaClassAdapter.setClasses(classes);
            });
        });
    }
}
