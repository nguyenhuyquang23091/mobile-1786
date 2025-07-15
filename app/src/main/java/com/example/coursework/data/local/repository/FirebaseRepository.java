package com.example.coursework.data.local.repository;

import android.util.Log;

import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.entities.YogaCourse;
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

    public void syncYogaCourse(YogaCourse yogaCourse) {
        if (yogaCourse == null) return;
        String docId = String.valueOf(yogaCourse.uid);
        firestore.collection("yoga_classes").document(docId).set(yogaCourse)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseRepository", "Yoga course synced successfully");
                })
                .addOnFailureListener(e -> {
                    Log.d("FirebaseRepository", "Failed to sync Yoga course")
                    ;
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

    public void syncAllClassInstances(String courseId, List<ClassInstance> instances) {

        WriteBatch writeBatch = firestore.batch();

        CollectionReference instanceRef = firestore.collection("yoga_classes").document(courseId).collection("instances");

        for ( ClassInstance instance : instances) {
            DocumentReference documentReference = instanceRef.document(String.valueOf(instance.getId()));
            writeBatch.set(documentReference, instance, SetOptions.merge());

        }
        writeBatch.commit()
                .addOnSuccessListener(avoid -> {Log.d("FirebaseRepository", "All class instances synced successfully");})
                .addOnFailureListener(e -> Log.d("Failed to sync", "Failed to sync"));
    }
}
