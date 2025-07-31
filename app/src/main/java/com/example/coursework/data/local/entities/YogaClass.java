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
    private String id;
    @ColumnInfo(name = "courseId")
    private String courseId;

    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "teacher")
    private String teacher;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "courseType")
    private String courseType;
    
    public YogaClass(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }
}
