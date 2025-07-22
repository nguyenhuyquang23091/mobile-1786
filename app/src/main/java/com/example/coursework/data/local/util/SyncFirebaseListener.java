package com.example.coursework.data.local.util;

import com.example.coursework.data.local.entities.yogaEntity.YogaCourse;
import java.util.List;

public interface SyncFirebaseListener {
    void syncFirebasewithLocal(List<YogaCourse> courses);

    void syncFailure(String errorMessage);
    void syncFirebasewithLocal();

    default void syncSuccess(List<YogaCourse> courses) {
        syncFirebasewithLocal();
    }
}
