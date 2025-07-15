package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.entities.ClassInstanceWIthDetail;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.util.SyncFirebaseListener;

import java.util.List;
import java.util.concurrent.CompletableFuture;
public interface YogaClassRepository {
    void insert(YogaCourse yogaCourse, SyncFirebaseListener syncFirebaseListener);

    void update(YogaCourse yogaCourse);
    void delete(YogaCourse yogaCourse);
    List<YogaCourse> getAll();

    YogaCourse findById(int id);

    void insertInstance(ClassInstance classInstance);
    List<ClassInstance> getInstance(int courseId);
    void updateInstance(ClassInstance classInstance);
    void deleteInstance(ClassInstance classInstance);

    List<ClassInstance> searchByTeacher(String teacher);

    List<ClassInstance> searchByDate(String date);
    List<ClassInstance> searchByDay(String day);

    ClassInstanceWIthDetail getInstanceWithDetails(int instanceId);

    boolean isConnected();

}
