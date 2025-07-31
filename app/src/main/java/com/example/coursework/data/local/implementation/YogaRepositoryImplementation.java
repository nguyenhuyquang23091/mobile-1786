package com.example.coursework.data.local.implementation;

import android.app.Application;
import android.util.Log;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.DAO.YogaDAO;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaClassWithDetail;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.repository.YogaRepository;
import com.example.coursework.data.local.util.ConnectivityCheck;
import com.example.coursework.data.local.util.SyncYogaClassesListener;
import com.example.coursework.data.local.util.SyncYogaCourseListener;

import java.util.List;
import java.util.UUID;

public class YogaRepositoryImplementation implements YogaRepository {
    private YogaDAO yogaDAO;
    private YogaFirebaseRepository yogaFirebaseRepository;
    private ConnectivityCheck connectivityCheck;

    public YogaRepositoryImplementation(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.yogaDAO = db.yogaClassDAO();
        this.connectivityCheck = new ConnectivityCheck(application);
        this.yogaFirebaseRepository = new YogaFirebaseRepository();
        this.connectivityCheck.RegisterNetworkCallback();
    }
    public boolean isConnected(){
        return connectivityCheck.isConnected();
    }
    @Override
    public void insertYogaCourse(YogaCourse yogaCourse, SyncYogaCourseListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaCourse.setUid(java.util.UUID.randomUUID().toString());
            yogaDAO.insertYogaCourse(yogaCourse);
            if (isConnected()) {
                YogaCourse classToSync = yogaDAO.getCourseById(yogaCourse.getUid());
                if (classToSync != null) {
                    yogaFirebaseRepository.upSertYogaCourse(classToSync, listener);
                } else {
                    if (listener != null) {
                        listener.syncFailure("Error: Could not retrieve saved course from local DB.");
                    }
                }
            } else {
                if (listener != null) {
                    listener.syncFailure("No network connection. Data saved locally.");
                }
            }
        });
    }

    @Override
    public void updateYogaCourse(YogaCourse yogaCourse) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaDAO.upsertYogaCourse(yogaCourse);
            if (isConnected()) {
                yogaFirebaseRepository.upSertYogaCourse(yogaCourse, new SyncYogaCourseListener() {
                    @Override
                    public void syncFirebaseWithLocal() {
                        Log.d("YogaRepositoryImplementation", "Course updated in Firebase successfully");
                    }

                    @Override
                    public void syncFailure(String errorMessage) {
                        Log.e("YogaRepositoryImplementation", "Failed to updateYogaCourse course in Firebase: " + errorMessage);
                    }
                });
            }
        });
    }

    @Override
    public void deleteYogaCourse(YogaCourse yogaCourse) {
       AppDatabase.databaseWriteExecutor.execute(() -> {
           yogaDAO.delete(yogaCourse);
           if (isConnected()) {
               yogaFirebaseRepository.deteleYogaCourse(yogaCourse, new SyncYogaCourseListener() {
                   @Override
                   public void syncFailure(String errorMessage) {
                       Log.e("YogaRepositoryImplementation", "Failed to deleteYogaCourse course in Firebase: " + errorMessage);
                   }

                   @Override
                   public void syncFirebaseWithLocal() {
                       Log.d("YogaRepositoryImplementation", "Deleted in Firebase successfully");

                   }
               });
           }
       });
    }


    @Override
    public void loadAllCoursesFromFirebase(SyncYogaCourseListener listener) {
        if (isConnected()){
            yogaFirebaseRepository.syncYogaCourse(new SyncYogaCourseListener() {
                @Override
                public void syncFailure(String errorMessage) {
                    if (listener != null) listener.syncFailure(errorMessage);
                }

                @Override
                public void syncFirebaseWithLocal() {

                }

                @Override
                public void syncFirebaseWithLocal(List<YogaCourse> courses) {
                    Log.d("YogaRepositoryImplementation", "Received " + courses.size() + " courses from Firebase, passing to fragment");
                    if (listener != null) listener.syncFirebaseWithLocal(courses);
                }


            });
        } else {
            if (listener != null) {
                listener.syncFailure("No network connection. Data saved locally.");
            }
        }
    }

    @Override
    public void loadAllClassesFromFirebase(String courseId, SyncYogaClassesListener listener) {
        if (isConnected()){
            yogaFirebaseRepository.syncAllYogaCLasses(courseId, new SyncYogaClassesListener() {
                @Override
                public void syncFailure(String errorMessage) {
                    if (listener != null)
                        listener.syncFailure(errorMessage);
                }

                @Override
                public void syncClassesWithFirebase() {

                }

                @Override
                public void syncClassesWithFirebase(List<YogaClass> yogaClassList) {
                    if(listener != null){
                        listener.syncClassesWithFirebase(yogaClassList);
                    }
                }
            });
        } else {
            if (listener != null) {
                listener.syncFailure("No network connection. Data saved locally.");
            }
        }


    }

    @Override
    public YogaCourse findById(String uid) {
        return yogaDAO.getCourseById(uid);
    }
    ///INSTANCE IMPLEMENTATION
    @Override
    public void insertYogaClass(YogaClass yogaClass) {
        yogaClass.setId(UUID.randomUUID().toString());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaDAO.insertYogaClass(yogaClass);
            if (isConnected()) {
                // Upload this class instance to Firebase
                yogaFirebaseRepository.upSertYogaCLasses(yogaClass, new SyncYogaClassesListener() {
                    @Override
                    public void syncFailure(String errorMessage) {
                        Log.e("YogaRepositoryImplementation", "Failed to sync class to Firebase: " + errorMessage);
                    }

                    @Override
                    public void syncClassesWithFirebase() {
                        Log.d("YogaRepositoryImplementation", "Class synced to Firebase successfully");
                    }

                    @Override
                    public void syncClassesWithFirebase(List<YogaClass> yogaClassList) {
                        // Not used for single class upload
                    }
                });
            }
        });
    }

    @Override
    public void updateYogaClass(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaDAO.upsertYogaClass(yogaClass);
            if (isConnected()) {
                // Upload this updated class instance to Firebase
                yogaFirebaseRepository.upSertYogaCLasses(yogaClass, new SyncYogaClassesListener() {
                    @Override
                    public void syncFailure(String errorMessage) {
                        Log.e("YogaRepositoryImplementation", "Failed to updateYogaCourse class in Firebase: " + errorMessage);
                    }

                    @Override
                    public void syncClassesWithFirebase() {
                        Log.d("YogaRepositoryImplementation", "Class updated in Firebase successfully");
                    }

                    @Override
                    public void syncClassesWithFirebase(List<YogaClass> yogaClassList) {
                        // Not used for single class upload
                    }
                });
            }
        });
    }
    @Override
    public void deleteYogaClass(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaDAO.deleteYogaClass(yogaClass);
            if (isConnected()) {
                // Delete this class instance from Firebase
                yogaFirebaseRepository.deleteYogaCLasses(yogaClass, new SyncYogaClassesListener() {
                    @Override
                    public void syncFailure(String errorMessage) {
                        Log.e("YogaRepositoryImplementation", "Failed to deleteYogaCourse class from Firebase: " + errorMessage);
                    }

                    @Override
                    public void syncClassesWithFirebase() {
                        Log.d("YogaRepositoryImplementation", "Class deleted from Firebase successfully");
                    }

                    @Override
                    public void syncClassesWithFirebase(List<YogaClass> yogaClassList) {
                        // Not used for deleteYogaCourse operation
                    }
                });
            }
        });
    }

    @Override
    public List<YogaClass> searchByTeacher(String teacher) {
        return yogaDAO.searchByTeacher(teacher);
    }

    @Override
    public List<YogaClass> searchByDate(String date) {
        return yogaDAO.searchByDate(date);
    }

    @Override
    public List<YogaClass> searchByDay(String day) {
        return yogaDAO.searchByDay(day);
    }

    @Override
    public YogaClassWithDetail getInstanceWithDetails(String instanceId) {
        return yogaDAO.getInstanceWithDetails(instanceId);
    }
}
