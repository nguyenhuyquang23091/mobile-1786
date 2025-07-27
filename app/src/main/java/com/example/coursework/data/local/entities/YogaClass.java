package com.example.coursework.data.local.entities;



import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;


@Entity(tableName = "yoga_classes",
        foreignKeys = @ForeignKey(
                entity = YogaCourse.class,
                parentColumns = "uid",
                childColumns = "courseId",
                onDelete = CASCADE
        ),
        indices = @Index(value = "courseId")
)
public class YogaClass {
    @Exclude
    @PrimaryKey
    @NonNull
    public String id;
    @ColumnInfo(name = "courseId")
    public String courseId;

    @ColumnInfo(name = "date")
    public String date;
    @ColumnInfo(name = "teacher")
    public String teacher;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "description")
    public String description;
    
    @ColumnInfo(name = "courseType")
    public String courseType;
    public YogaClass(){


    }





}
