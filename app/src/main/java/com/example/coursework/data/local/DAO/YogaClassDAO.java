package com.example.coursework.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.coursework.data.local.entities.ClassInstance;
import com.example.coursework.data.local.entities.ClassInstanceWIthDetail;
import com.example.coursework.data.local.entities.YogaCourse;

import java.util.List;

@Dao
public interface YogaClassDAO {

    @Insert
    long insert(YogaCourse yogaCourse);
    @Update
    void update(YogaCourse yogaCourse);
    @Delete()
    void delete(YogaCourse yogaCourse);


    @Query("SELECT * FROM yoga_classes ORDER BY day, time")
    List<YogaCourse> getAllClasses();
    @Query("SELECT * FROM yoga_classes WHERE uid = :uid")
    YogaCourse getCourseById(int uid);
    @Insert
    void insertInstance(ClassInstance classInstance);
    @Update
    void updateInstance(ClassInstance classInstance);
    @Delete
    void deleteInstance(ClassInstance classInstance);
    @Query("SELECT * FROM class_instance WHERE courseId = :courseId")
    List<ClassInstance> getInstances(int courseId);
    @Query("SELECT * FROM class_instance WHERE teacher LIKE :teacher || '%'")
    List<ClassInstance> searchByTeacher(String teacher);
    @Query("SELECT * FROM class_instance WHERE date LIKE '%' || :date || '%'")
    List<ClassInstance> searchByDate(String date);
    @Query("SELECT ci.* FROM class_instance ci JOIN yoga_classes yc ON ci.courseId = yc.uid WHERE yc.day LIKE '%' || :day || '%'")
    List<ClassInstance> searchByDay(String day);

    @Transaction
    @Query("SELECT * FROM class_instance WHERE id = :instanceId")
    ClassInstanceWIthDetail getInstanceWithDetails(int instanceId);

}

