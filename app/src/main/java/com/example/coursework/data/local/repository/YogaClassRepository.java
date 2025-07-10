package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.entities.YogaClass;

import java.util.List;
public interface YogaClassRepository {
    void insert(YogaClass yogaClass);
    void update(YogaClass yogaClass);
    void delete(YogaClass yogaClass);
    List<YogaClass> getAll();

    YogaClass findById(int id);

    void insertInstance(ClassInstance classInstance);
    List<ClassInstance> getInstance(int courseId);
    void updateInstance(ClassInstance classInstance);
    void deleteInstance(ClassInstance classInstance);

    List<ClassInstance> searchByTeacher(String teacher);

    List<ClassInstance> searchByDate(String date);
    List<ClassInstance> searchByDay(String day);





}
