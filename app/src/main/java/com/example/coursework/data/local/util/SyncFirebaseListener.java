package com.example.coursework.data.local.util;

import com.example.coursework.data.local.entities.YogaCourse;
import java.util.List;

public interface SyncFirebaseListener {

    void syncFailure(String errorMessage);
    void syncFirebaseWithLocal();

    default void syncFirebaseWithLocal(List<YogaCourse> courses) {
        syncFirebaseWithLocal();
    }
}
