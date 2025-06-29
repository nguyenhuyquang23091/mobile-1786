package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.entities.YogaClass;

import java.util.List;
public interface YogaClassRepository {
    void insert(YogaClass yogaClass);
    void update(YogaClass yogaClass);
    void delete(YogaClass yogaClass);
    List<YogaClass> getAll();

}
