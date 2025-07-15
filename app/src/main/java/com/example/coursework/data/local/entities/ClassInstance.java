package com.example.coursework.data.local.entities;



import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;


@Entity(tableName = "class_instance",
        foreignKeys = @ForeignKey(
                entity = YogaCourse.class,
                parentColumns = "uid",
                childColumns = "courseId",
                onDelete = CASCADE
        ),
        indices = @Index(value = "courseId")
)
public class ClassInstance {
    @PrimaryKey(autoGenerate = true)
    @Exclude
    public int id;
    @ColumnInfo(name = "courseId")
    @Exclude
    public int courseId;

    @ColumnInfo(name = "date")
    public String date;
    @ColumnInfo(name = "teacher")
    public String teacher;
    public ClassInstance(){

    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClassInstance(int id, int courseId, String date, String teacher) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
    }

}
