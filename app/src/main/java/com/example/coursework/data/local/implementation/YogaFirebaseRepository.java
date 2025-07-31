package com.example.coursework.data.local.implementation;

import android.util.Log;

import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.util.SyncYogaClassesListener;
import com.example.coursework.data.local.util.SyncYogaCourseListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.ArrayList;

public class YogaFirebaseRepository {
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    public YogaFirebaseRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }
    //loadYogaCourse
    void syncYogaCourse(SyncYogaCourseListener listener) {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        CollectionReference collectionReference = firestore.collection("yoga_classes");
        listenerRegistration = collectionReference.orderBy("day", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapShot, e) -> {
                    if( e != null ){
                        Log.e("YogaFirebaseRepository", "Listen failed", e);
                        if (listener != null) listener.syncFailure("Firestore listener failed: " + e.getMessage());
                        return;
                    }
                    if(queryDocumentSnapShot != null) {
                        List<YogaCourse> yogaCourses = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapShot){
                            YogaCourse yogaCourse = queryDocumentSnapshot.toObject(YogaCourse.class);
                            String docId = queryDocumentSnapshot.getId();
                            yogaCourse.setUid(docId);
                            yogaCourses.add(yogaCourse);

                        }
                        if (listener != null) listener.syncFirebaseWithLocal(yogaCourses);
                    }

                });

    }
    //Synce if changes, push all changes to firebase
    //sync
     void upSertYogaCourse(YogaCourse yogaCourse, SyncYogaCourseListener listener) {
        if (yogaCourse == null) {
            return;
        }
        String docId = yogaCourse.getUid();
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
     void deteleYogaCourse(YogaCourse yogaCourse, SyncYogaCourseListener listener) {
        if (yogaCourse == null) {
            if (listener != null) listener.syncFailure("Course is null");
            return;
        }
        String docId = yogaCourse.getUid();
        firestore.collection("yoga_classes").document(docId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("YogaFirebaseRepository", "Yoga course deleted successfully from Firestore");
                    if (listener != null) listener.syncFirebaseWithLocal();
                })
                .addOnFailureListener(e -> {
                    Log.e("YogaFirebaseRepository", "Failed to deleteYogaCourse Yoga course from Firestore: " + e.getMessage());
                    if (listener != null) listener.syncFailure("Failed to deleteYogaCourse: " + e.getMessage());
                });
    }
     void syncAllYogaCLasses(String courseId, SyncYogaClassesListener listener)
     {
         if (listenerRegistration != null) {
         listenerRegistration.remove();
     }
         CollectionReference collectionReference = firestore.collection("yoga_classes").document(courseId).collection("classes");
         listenerRegistration = collectionReference.orderBy("date", Query.Direction.ASCENDING)
                 .addSnapshotListener((queryDocumentSnapShot, e) -> {
                     if( e != null ){
                         Log.e("YogaFirebaseRepository", "Listen failed", e);
                         if (listener != null) listener.syncFailure("Firestore listener failed: " + e.getMessage());
                         return;

                     }
                     if(queryDocumentSnapShot != null) {
                         List<YogaClass> yogaClasses = new ArrayList<>();
                         for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapShot){
                             YogaClass yogaClass = queryDocumentSnapshot.toObject(YogaClass.class);
                             String docId = queryDocumentSnapshot.getId();
                             yogaClass.setId(docId);
                             yogaClasses.add(yogaClass);
                         }
                         if (listener != null) listener.syncClassesWithFirebase(yogaClasses);
                     }
                 });

    }
    void upSertYogaCLasses(YogaClass yogaClass, SyncYogaClassesListener listener) {
        if(yogaClass == null ){
            return;
        }
        String courseId = yogaClass.getCourseId();
        String classId = yogaClass.getId();
        firestore.collection("yoga_classes").document(courseId).collection("classes").document(classId).set(yogaClass)
                .addOnSuccessListener(aVoid -> {
                    Log.d("YogaFirebaseRepository", "Yoga course synced successfully");
                    if (listener != null) listener.syncClassesWithFirebase();
                })
                .addOnFailureListener(e -> {
                    Log.d("YogaFirebaseRepository", "Failed to sync Yoga course: " + e.getMessage());
                    if (listener != null) listener.syncFailure("Failed to sync: " + e.getMessage());
                });

    }
     void deleteYogaCLasses(YogaClass yogaClass, SyncYogaClassesListener listener) {
        if(yogaClass == null) {
            if (listener != null) listener.syncFailure("YogaClass is null");
            return;
        }
        String courseId = yogaClass.getCourseId();
        String classId = yogaClass.getId();
        
        Log.d("YogaFirebaseRepository", "Attempting to delete class with courseId: " + courseId + ", classId: " + classId);
        
        firestore.collection("yoga_classes").document(courseId).collection("classes").document(classId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("YogaFirebaseRepository", "Class instance deleted successfully from Firestore - courseId: " + courseId + ", classId: " + classId);
                    if (listener != null) listener.syncClassesWithFirebase();
                })
                .addOnFailureListener(e -> {
                    Log.e("YogaFirebaseRepository", "Failed to delete class instance from Firestore: " + e.getMessage() + " - courseId: " + courseId + ", classId: " + classId);
                    if (listener != null) listener.syncFailure("Failed to delete: " + e.getMessage());
                });
    }
}
