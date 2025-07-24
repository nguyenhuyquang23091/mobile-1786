package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaClassWithDetail;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.util.SyncFirebaseListener;

import java.util.List;

public interface YogaRepository {
    void insert(YogaCourse yogaCourse, SyncFirebaseListener syncFirebaseListener);

    void update(YogaCourse yogaCourse);
    void delete(YogaCourse yogaCourse);
    List<YogaCourse> getAll();

    YogaCourse findById(int id);

    void insertInstance(YogaClass yogaClass);
    List<YogaClass> getInstance(int courseId);
    void updateInstance(YogaClass yogaClass);
    void deleteInstance(YogaClass yogaClass);

    List<YogaClass> searchByTeacher(String teacher);

    List<YogaClass> searchByDate(String date);
    List<YogaClass> searchByDay(String day);

    YogaClassWithDetail getInstanceWithDetails(int instanceId);

    boolean isConnected();
    
    void loadAllCoursesFromFirebase(SyncFirebaseListener listener);

}
