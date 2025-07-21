package com.example.coursework.data.local.repository;

import android.util.Log;

import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.util.SyncFirebaseListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class FirebaseRepository {
    private FirebaseFirestore firestore;

    public FirebaseRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void syncYogaCourse(YogaCourse yogaCourse, SyncFirebaseListener listener) {
        if (yogaCourse == null) {
            return;
        }
        String docId = String.valueOf(yogaCourse.uid);
        firestore.collection("yoga_classes").document(docId).set(yogaCourse)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseRepository", "Yoga course synced successfully");
                    if (listener != null) listener.syncSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.d("FirebaseRepository", "Failed to sync Yoga course: " + e.getMessage());
                    if (listener != null) listener.syncFailure("Failed to sync: " + e.getMessage());
                });
    }

    public void deteleYogaCourse(YogaCourse yogaCourse) {
        if (yogaCourse == null) return;
        String docId = String.valueOf(yogaCourse.uid);
        firestore.collection("yoga_classes").document(docId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Deleted Successfully", "Yoga course deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.d("Deleted Failed", "Failed to delete Yoga course")
                    ;
                });
    }

    public void syncAllClassInstances(String courseId, List<YogaClass> classes) {

        WriteBatch writeBatch = firestore.batch();
        CollectionReference instanceRef = firestore.collection("yoga_classes").document(courseId).collection("classes");

        for ( YogaClass yogaClass : classes) {
            DocumentReference documentReference = instanceRef.document(String.valueOf(yogaClass.id));
            writeBatch.set(documentReference, yogaClass, SetOptions.merge());
        }
        writeBatch.commit()
                .addOnSuccessListener(avoid -> {Log.d("FirebaseRepository", "All class classes synced successfully for course id: " + courseId);})
                .addOnFailureListener(e -> Log.d("Failed to sync", "Failed to sync for course id :" + courseId ));
    }
    public void deleteInstance(YogaClass yogaClass) {
        if(yogaClass == null ) return;
        String courseId = String.valueOf(yogaClass.courseId);
        String instanceId = String.valueOf(yogaClass.id);
        firestore.collection("yoga_classes").document(courseId).collection("instances").document(instanceId).delete();
    }
}
