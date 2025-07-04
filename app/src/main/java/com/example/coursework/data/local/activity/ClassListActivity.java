package com.example.coursework.data.local.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.adapter.YogaClassAdapter;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;

import java.util.List;

public class ClassListActivity extends AppCompatActivity {
    private YogaClassRepository yogaClassRepository;
    private YogaClassAdapter yogaClassAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        yogaClassAdapter = new YogaClassAdapter(new YogaClassAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(YogaClass yogaClass) {
                yogaClassRepository.delete(yogaClass);
                Toast.makeText(ClassListActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                loadClasses();
            }

            @Override
            public void onEditCLick(YogaClass yogaClass) {
                Intent intent = new Intent(ClassListActivity.this, MainActivity.class);
                intent.putExtra("uid", yogaClass.uid);
                startActivity(intent);

            }
        });

        recyclerView.setAdapter(yogaClassAdapter);
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
