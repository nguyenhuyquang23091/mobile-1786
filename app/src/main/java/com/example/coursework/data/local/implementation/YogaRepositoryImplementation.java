package com.example.coursework.data.local.implementation;

import android.app.Application;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.DAO.YogaDAO;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaClassWithDetail;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.repository.YogaRepository;
import com.example.coursework.data.local.repository.firebaseRepository.YogaFirebaseRepository;
import com.example.coursework.data.local.util.ConnectivityCheck;
import com.example.coursework.data.local.util.SyncFirebaseListener;

import java.util.List;

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
    public void insert(YogaCourse yogaCourse, SyncFirebaseListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long newUid = yogaDAO.insert(yogaCourse);
            if (isConnected()) {
                YogaCourse classToSync = yogaDAO.getCourseById((int) newUid);
                if (classToSync != null) {
                    yogaFirebaseRepository.insertYogaCourse(classToSync, listener);
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
    public void update(YogaCourse yogaCourse) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaDAO.update(yogaCourse);});
        if(isConnected()) {


        }
    }

    @Override
    public void delete(YogaCourse yogaCourse) {
        if(isConnected()) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                yogaDAO.delete(yogaCourse);
                yogaFirebaseRepository.deteleYogaCourse(yogaCourse);
            });
        } else {
            AppDatabase.databaseWriteExecutor.execute(() -> yogaDAO.delete(yogaCourse));
        }    }

    @Override
    public List<YogaCourse> getAll() {
        return yogaDAO.getAllClasses();
    }

    @Override
    public void loadAllCoursesFromFirebase(SyncFirebaseListener listener) {
        if (isConnected()) {
            yogaFirebaseRepository.getYogaCourse(new SyncFirebaseListener() {
                @Override
                public void syncFirebaseWithLocal() {
                    // Firebase load successful
                    if (listener != null) {
                        listener.syncFirebaseWithLocal();
                    }
                }

                @Override
                public void syncFirebaseWithLocal(List<YogaCourse> courses) {
                    // Real-time Firebase update - replace local data with current Firebase state
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Clear existing courses to sync with current Firebase state
                        List<YogaCourse> existingCourses = yogaDAO.getAllClasses();
                        for (YogaCourse existing : existingCourses) {
                            yogaDAO.delete(existing);
                        }
                        
                        // Insert all current courses from Firebase
                        for (YogaCourse course : courses) {
                            // Reset UID to let Room auto-generate local IDs
                            course.uid = 0;
                            yogaDAO.insert(course);
                        }
                    });

                    if (listener != null) {
                        listener.syncFirebaseWithLocal();
                    }
                }

                @Override
                public void syncFailure(String error) {
                    // Firebase load failed, fall back to local data
                    if (listener != null) {
                        listener.syncFailure("Firebase load failed: " + error + ". Using local data.");
                    }
                }
            });
        } else {
            // No connection, use local data only
            if (listener != null) {
                listener.syncFailure("No internet connection. Using local data only.");
            }
        }
    }

    @Override
    public YogaCourse findById(int uid) {
        return yogaDAO.getCourseById(uid);
    }
    ///INSTANCE IMPLEMENTATION
    @Override
    public void insertInstance(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaDAO.insertInstance(yogaClass);
            if (isConnected()) {
                // Call the helper method after inserting
                List<YogaClass> instances = yogaDAO.getInstances(yogaClass.courseId);
                yogaFirebaseRepository.syncAllClassInstances(String.valueOf(yogaClass.courseId), instances);
            }
        });
    }

    @Override
    public List<YogaClass> getInstance(int courseId) {
        return yogaDAO.getInstances(courseId);
    }
    @Override
    public void updateInstance(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaDAO.updateInstance(yogaClass));
    }
    @Override
    public void deleteInstance(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                yogaDAO.deleteInstance(yogaClass));
        if(isConnected()){
            yogaFirebaseRepository.deleteInstance(yogaClass);}
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
    public YogaClassWithDetail getInstanceWithDetails(int instanceId) {
        return yogaDAO.getInstanceWithDetails(instanceId);
    }




    //CREATE CHECK CONNECTION HELPER

}
