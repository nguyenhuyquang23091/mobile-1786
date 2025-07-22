package com.example.coursework.data.local.implementation;

import android.app.Application;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.DAO.YogaClassDAO;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaClassWithDetail;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.data.local.repository.FirebaseRepository;
import com.example.coursework.data.local.util.ConnectivityCheck;
import com.example.coursework.data.local.util.SyncFirebaseListener;

import java.util.List;

public class YogaRepositoryImplementation implements YogaClassRepository {
    private YogaClassDAO yogaClassDAO;
    private FirebaseRepository firebaseRepository;
    private ConnectivityCheck connectivityCheck;

    public YogaRepositoryImplementation(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.yogaClassDAO = db.yogaClassDAO();
        this.connectivityCheck = new ConnectivityCheck(application);
        this.firebaseRepository = new FirebaseRepository();
        this.connectivityCheck.RegisterNetworkCallback();
    }
    public boolean isConnected(){
        return connectivityCheck.isConnected();
    }
    @Override
    public void insert(YogaCourse yogaCourse, SyncFirebaseListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long newUid = yogaClassDAO.insert(yogaCourse);
            if (isConnected()) {
                YogaCourse classToSync = yogaClassDAO.getCourseById((int) newUid);

                if (classToSync != null) {
                    firebaseRepository.insertYogaCourse(classToSync, listener);
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
            yogaClassDAO.update(yogaCourse);});
        if(isConnected()) {


        }
    }

    @Override
    public void delete(YogaCourse yogaCourse) {
        if(isConnected()) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                yogaClassDAO.delete(yogaCourse);
                firebaseRepository.deteleYogaCourse(yogaCourse);
            });
        } else {
            AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.delete(yogaCourse));
        }    }

    @Override
    public List<YogaCourse> getAll() {
        return yogaClassDAO.getAllClasses();
    }

    @Override
    public void loadAllCoursesFromFirebase(SyncFirebaseListener listener) {
        if (isConnected()) {
            firebaseRepository.getYogaCourse(new SyncFirebaseListener() {
                @Override
                public void syncFirebasewithLocal() {
                    // Firebase load successful
                    if (listener != null) {
                        listener.syncFirebasewithLocal();
                    }
                }

                @Override
                public void syncFirebasewithLocal(List<YogaCourse> courses) {
                    // Firebase load successful with data, sync to local database
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        for (YogaCourse course : courses) {
                            // Check if course already exists to avoid duplicates
                            YogaCourse existingCourse = yogaClassDAO.getCourseById(course.uid);
                            if (existingCourse == null) {
                                yogaClassDAO.insert(course);
                            }
                        }
                    });
                    
                    if (listener != null) {
                        listener.syncFirebasewithLocal();
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
        return yogaClassDAO.getCourseById(uid);
    }
    ///INSTANCE IMPLEMENTATION
    @Override
    public void insertInstance(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaClassDAO.insertInstance(yogaClass);
                if (isConnected()) {
                // Call the helper method after inserting
                List<YogaClass> instances = yogaClassDAO.getInstances(yogaClass.courseId);
                firebaseRepository.syncAllClassInstances(String.valueOf(yogaClass.courseId), instances);
            }
        });
    }

    @Override
    public List<YogaClass> getInstance(int courseId) {
        return yogaClassDAO.getInstances(courseId);
    }
    @Override
    public void updateInstance(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.updateInstance(yogaClass));
    }
    @Override
    public void deleteInstance(YogaClass yogaClass) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                yogaClassDAO.deleteInstance(yogaClass));
                if(isConnected()){
                    firebaseRepository.deleteInstance(yogaClass);}
    }
    @Override
    public List<YogaClass> searchByTeacher(String teacher) {
        return yogaClassDAO.searchByTeacher(teacher);
    }

    @Override
    public List<YogaClass> searchByDate(String date) {
        return yogaClassDAO.searchByDate(date);
    }

    @Override
    public List<YogaClass> searchByDay(String day) {
        return yogaClassDAO.searchByDay(day);
    }

    @Override
    public YogaClassWithDetail getInstanceWithDetails(int instanceId) {
       return yogaClassDAO.getInstanceWithDetails(instanceId);
    }




    //CREATE CHECK CONNECTION HELPER

}
