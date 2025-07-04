package com.example.coursework.data.local.entities;



import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;


@Entity(tableName = "class_instance",
        foreignKeys = @ForeignKey(
                entity = YogaClass.class,
                parentColumns = "uid",
                childColumns = "courseId",
                onDelete = CASCADE
        ),
        indices = @Index(value = "courseId")
)
public class ClassInstance {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "courseId")
    public int courseId;

    @ColumnInfo(name = "date")
    public String date;
    @ColumnInfo(name = "teacher")
    public String teacher;

    @ColumnInfo(name = "comments")
    public List<String> comment;



}
