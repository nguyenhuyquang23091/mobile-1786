package com.example.coursework.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.coursework.data.local.entities.yogaEntity.YogaClass;
import com.example.coursework.data.local.entities.yogaEntity.YogaClassWithDetail;
import com.example.coursework.data.local.entities.yogaEntity.YogaCourse;

import java.util.List;

@Dao
public interface YogaDAO {
    @Insert
    long insert(YogaCourse yogaCourse);
    @Update
    void update(YogaCourse yogaCourse);
    @Delete()
    void delete(YogaCourse yogaCourse);
    @Query("SELECT * FROM yoga_courses ORDER BY day, time")
    List<YogaCourse> getAllClasses();
    @Query("SELECT * FROM yoga_courses WHERE uid = :uid")
    YogaCourse getCourseById(int uid);
    @Insert
    void insertInstance(YogaClass yogaClass);
    @Update
    void updateInstance(YogaClass yogaClass);
    @Delete
    void deleteInstance(YogaClass yogaClass);
    @Query("SELECT yc.*, yco.type as courseType FROM yoga_classes yc " +
           "JOIN yoga_courses yco ON yc.courseId = yco.uid " +
           "WHERE yc.courseId = :courseId")
    List<YogaClass> getInstances(int courseId);
    @Query("SELECT * FROM yoga_classes WHERE teacher LIKE :teacher || '%'")
    List<YogaClass> searchByTeacher(String teacher);
    @Query("SELECT * FROM yoga_classes WHERE date LIKE '%' || :date || '%'")
    List<YogaClass> searchByDate(String date);
    @Query("SELECT ci.* FROM yoga_classes ci JOIN yoga_courses yc ON ci.courseId = yc.uid WHERE yc.day LIKE '%' || :day || '%'")
    List<YogaClass> searchByDay(String day);
    @Transaction
    @Query("SELECT * FROM yoga_classes WHERE id = :instanceId")
    YogaClassWithDetail getInstanceWithDetails(int instanceId);
}

