package com.example.coursework.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaClassWithDetail;
import com.example.coursework.data.local.entities.YogaCourse;

import java.util.List;

@Dao
public interface YogaDAO {
    @Delete()
    void delete(YogaCourse yogaCourse);
    @Query("SELECT * FROM yoga_courses WHERE uid = :uid")
    YogaCourse getCourseById(String uid);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertYogaCourse(YogaCourse yogaCourse);
    
    @Insert
    void insertYogaCourse(YogaCourse yogaCourse);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertYogaClass(YogaClass yogaClass);
    
    @Insert
    void insertYogaClass(YogaClass yogaClass);
    @Delete
    void deleteYogaClass(YogaClass yogaClass);
    @Query("SELECT yc.*, yco.type as courseType FROM yoga_classes yc " +
           "JOIN yoga_courses yco ON yc.courseId = yco.uid " +
           "WHERE yc.courseId = :courseId")
    List<YogaClass> getInstances(String courseId);
    @Query("SELECT * FROM yoga_classes WHERE teacher LIKE :teacher || '%'")
    List<YogaClass> searchByTeacher(String teacher);
    @Query("SELECT * FROM yoga_classes WHERE date LIKE '%' || :date || '%'")
    List<YogaClass> searchByDate(String date);
    @Query("SELECT ci.* FROM yoga_classes ci JOIN yoga_courses yc ON ci.courseId = yc.uid WHERE yc.day LIKE '%' || :day || '%'")
    List<YogaClass> searchByDay(String day);
    @Transaction
    @Query("SELECT * FROM yoga_classes WHERE id = :instanceId")
    YogaClassWithDetail getInstanceWithDetails(String instanceId);
}

