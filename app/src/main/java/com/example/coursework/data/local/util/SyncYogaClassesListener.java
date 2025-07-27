package com.example.coursework.data.local.util;

import com.example.coursework.data.local.entities.YogaClass;

import java.util.List;

public interface SyncYogaClassesListener {

    void syncFailure(String errorMessage);

    void syncClassesWithFirebase();
    default void syncClassesWithFirebase(List<YogaClass> yogaClassList){
        syncClassesWithFirebase();
    }

}
