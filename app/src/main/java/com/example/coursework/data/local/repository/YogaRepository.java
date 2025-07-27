package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaClassWithDetail;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.util.SyncYogaClassesListener;
import com.example.coursework.data.local.util.SyncYogaCourseListener;

import java.util.List;

public interface YogaRepository {
    void insertYogaCourse(YogaCourse yogaCourse, SyncYogaCourseListener syncYogaCourseListener);
    void updateYogaCourse(YogaCourse yogaCourse);
    void deleteYogaCourse(YogaCourse yogaCourse);
    YogaCourse findById(String id);

    void insertYogaClass(YogaClass yogaClass);
    void updateYogaClass(YogaClass yogaClass);
    void deleteYogaClass(YogaClass yogaClass);

    List<YogaClass> searchByTeacher(String teacher);

    List<YogaClass> searchByDate(String date);
    List<YogaClass> searchByDay(String day);

    YogaClassWithDetail getInstanceWithDetails(String instanceId);

    boolean isConnected();
    
    void loadAllCoursesFromFirebase(SyncYogaCourseListener listener);

    void loadAllClassesFromFirebase(String courseId, SyncYogaClassesListener listener);

}
