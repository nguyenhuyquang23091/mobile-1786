package com.example.coursework.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.coursework.data.local.entities.YogaClass;

import java.util.List;


@Dao
public interface YogaClassDAO {
    @Insert
    void insert(YogaClass yogaClass);
    @Update
    void update(YogaClass yogaClass);
    @Delete()
    void delete(YogaClass yogaClass);

    @Query("SELECT * FROM yoga_classes ORDER BY day, time")
    List<YogaClass> getAllClasses();
    @Query("SELECT * FROM yoga_classes WHERE uid = :userId")
    YogaClass getClassById(int userId);
}
