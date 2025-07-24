package com.example.coursework.data.local.repository.firebaseRepository;

import android.util.Log;

import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.util.SyncFirebaseListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.ArrayList;

public class YogaFirebaseRepository {
    private FirebaseFirestore firestore;

    public YogaFirebaseRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }
    //loadYogaCourse


    public void getYogaCourse(SyncFirebaseListener listener) {
        CollectionReference collectionReference = firestore.collection("yoga_classes");

        // Use addSnapshotListener for real-time updates
        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.e("YogaFirebaseRepository", "Failed to load yoga courses: " + error.getMessage());
                if (listener != null) listener.syncFailure("Failed to load: " + error.getMessage());
                return;
            }

            if (queryDocumentSnapshots != null) {
                List<YogaCourse> yogaCourses = new ArrayList<>();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    try {
                        YogaCourse yogaCourse = documentSnapshot.toObject(YogaCourse.class);
                        yogaCourses.add(yogaCourse);
                        Log.d("YogaFirebaseRepository", "Real-time update - course: " + yogaCourse.toString());
                    } catch (Exception e) {
                        Log.e("YogaFirebaseRepository", "Error parsing document: " + e.getMessage());
                    }
                }

                Log.d("YogaFirebaseRepository", "Real-time sync: " + yogaCourses.size() + " yoga courses");
                if (listener != null) listener.syncFirebaseWithLocal(yogaCourses);
            }
        });
    }
    //Synce if changes, push all changes to firebase
    //sync
    public void insertYogaCourse(YogaCourse yogaCourse, SyncFirebaseListener listener) {
        if (yogaCourse == null) {
            return;
        }
        String docId = String.valueOf(yogaCourse.uid);
        firestore.collection("yoga_classes").document(docId).set(yogaCourse)
                .addOnSuccessListener(aVoid -> {
                    Log.d("YogaFirebaseRepository", "Yoga course synced successfully");
                    if (listener != null) listener.syncFirebaseWithLocal();
                })
                .addOnFailureListener(e -> {
                    Log.d("YogaFirebaseRepository", "Failed to sync Yoga course: " + e.getMessage());
                    if (listener != null) listener.syncFailure("Failed to sync: " + e.getMessage());
                });
    }

    //get


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
                .addOnSuccessListener(avoid -> {Log.d("YogaFirebaseRepository", "All class classes synced successfully for course id: " + courseId);})
                .addOnFailureListener(e -> Log.d("Failed to sync", "Failed to sync for course id :" + courseId ));
    }
    public void deleteInstance(YogaClass yogaClass) {
        if(yogaClass == null ) return;
        String courseId = String.valueOf(yogaClass.courseId);
        String instanceId = String.valueOf(yogaClass.id);
        firestore.collection("yoga_classes").document(courseId).collection("instances").document(instanceId).delete();
    }
}
