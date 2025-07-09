package com.example.coursework.data.local.implementation;

import android.app.Application;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.DAO.YogaClassDAO;
import com.example.coursework.data.local.entities.ClassInstance;
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

    @Override
    public YogaClass findById(int uid) {
        return yogaClassDAO.getClassById(uid);
    }
    ///INSTANCE IMPLEMENTATION
    @Override
    public void insertInstance(ClassInstance classInstance) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.insertInstance(classInstance));

    }

    @Override
    public List<ClassInstance> getInstance(int courseId) {
        return yogaClassDAO.getInstances(courseId);
    }

    @Override
    public void updateInstance(ClassInstance classInstance) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.updateInstance(classInstance));

    }

    @Override
    public void deleteInstance(ClassInstance classInstance) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.deleteInstance(classInstance));

    }

    @Override
    public List<ClassInstance> searchByTeacher(String teacher) {
        return yogaClassDAO.searchByTeacher(teacher);
    }

}
