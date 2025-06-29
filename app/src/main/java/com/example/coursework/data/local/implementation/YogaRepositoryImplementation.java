package com.example.coursework.data.local.implementation;

import android.app.Application;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.DAO.YogaClassDAO;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.repository.YogaClassRepository;

import java.util.Collections;
import java.util.List;

public class YogaRepositoryImplementation implements YogaClassRepository {
    private YogaClassDAO yogaClassDAO;
    public YogaRepositoryImplementation(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.yogaClassDAO = db.yogaClassDAO();

    }
    @Override
    public void insert(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.insert(yogaClass));
    }

    @Override
    public void update(YogaClass yogaClass) {

        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.update(yogaClass));

    }

    @Override
    public void delete(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.delete(yogaClass));
    }

    @Override
    public List<YogaClass> getAll() {
        return yogaClassDAO.getAllClasses();
    }
}
