package com.example.coursework.data.local.implementation;

import android.app.Application;
import android.util.Log;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.DAO.YogaClassDAO;
import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.entities.ClassInstanceWIthDetail;
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
            // 1. Insert locally AND get the new auto-generated ID back from the database.
            long newUid = yogaClassDAO.insert(yogaCourse);

            // 2. Now check if the device is online.
            if (isConnected()) {
                // 3. Use the new, correct ID to fetch the definitive object from the database.
                YogaCourse classToSync = yogaClassDAO.getCourseById((int) newUid);

                if (classToSync != null) {
                    // 4. Sync the correct object to Firebase and report success.
                    firebaseRepository.syncYogaCourse(classToSync);
                    if (listener != null) {
                        listener.syncSuccess();
                    }
                } else {
                    // This would be a rare database error.
                    if (listener != null) {
                        listener.syncFailure("Error: Could not retrieve saved course from local DB.");
                    }
                }
            } else {
                // 5. If offline, report the correct reason for sync failure.
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
    public YogaCourse findById(int uid) {
        return yogaClassDAO.getCourseById(uid);
    }
    ///INSTANCE IMPLEMENTATION
    @Override
    public void insertInstance(ClassInstance classInstance) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            yogaClassDAO.insertInstance(classInstance);
            if (isConnected()) {
                // Call the helper method after inserting
                List<ClassInstance> instances = yogaClassDAO.getInstances(classInstance.courseId);
                firebaseRepository.syncAllClassInstances(String.valueOf(classInstance.courseId), instances);
            }
        });
    }

    @Override
    public List<ClassInstance> getInstance(int courseId) {
        return yogaClassDAO.getInstances(courseId);
    }
    @Override
    public void updateInstance(ClassInstance classInstance) {
        AppDatabase.databaseWriteExecutor.execute(() -> yogaClassDAO.updateInstance(classInstance));
    }
    @Override
    public void deleteInstance(ClassInstance classInstance) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                yogaClassDAO.deleteInstance(classInstance));
                if(isConnected()){
                    firebaseRepository.deleteInstance(classInstance);}
    }
    @Override
    public List<ClassInstance> searchByTeacher(String teacher) {
        return yogaClassDAO.searchByTeacher(teacher);
    }

    @Override
    public List<ClassInstance> searchByDate(String date) {
        return yogaClassDAO.searchByDate(date);
    }

    @Override
    public List<ClassInstance> searchByDay(String day) {
        return yogaClassDAO.searchByDay(day);
    }

    @Override
    public ClassInstanceWIthDetail getInstanceWithDetails(int instanceId) {
       return yogaClassDAO.getInstanceWithDetails(instanceId);
    }


}
